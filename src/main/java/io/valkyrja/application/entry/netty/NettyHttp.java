/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.entry.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP entry point for the Netty worker runtime.
 *
 * <p>Bootstraps the application once, then registers a Netty pipeline that dispatches every
 * incoming request to an isolated {@link io.valkyrja.container.manager.ChildContainer} for the
 * lifetime of that request and writes the framework response back through the channel.
 */
public class NettyHttp extends WorkerHttp {

    /**
     * Start the Netty server worker loop.
     *
     * @param config the HTTP configuration
     * @throws InterruptedException if the server thread is interrupted
     */
    public static void run(HttpConfigContract config) throws InterruptedException {
        server(config).closeFuture().sync();
    }

    /**
     * Bind and start the Netty server, returning the bound channel without blocking.
     *
     * <p>{@link #run} calls this and then blocks on the channel's close future. Exposed separately
     * so the server can be started, exercised, and stopped (e.g. from a test) by closing the
     * returned channel. The boss/worker event-loop groups are shut down when the channel closes —
     * whether that is a worker shutdown or a test closing the channel — mirroring the {@code
     * finally} in the blocking loop; if start-up fails before the channel is returned, they are
     * shut down too.
     *
     * @param config the HTTP configuration
     * @return the bound server channel
     * @throws InterruptedException if the bind is interrupted
     */
    public static Channel server(HttpConfigContract config) throws InterruptedException {
        ApplicationContract app = bootstrap(config);
        ContainerData data = (ContainerData) app.getContainer().getData();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        boolean started = false;
        try {
            Channel channel =
                    new ServerBootstrap()
                            .group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(
                                    new ChannelInitializer<SocketChannel>() {
                                        @Override
                                        protected void initChannel(SocketChannel ch) {
                                            ch.pipeline().addLast(new HttpServerCodec());
                                            ch.pipeline().addLast(new HttpObjectAggregator(65_536));
                                            ch.pipeline()
                                                    .addLast(
                                                            new SimpleChannelInboundHandler<
                                                                    FullHttpRequest>() {
                                                                @Override
                                                                protected void channelRead0(
                                                                        ChannelHandlerContext ctx,
                                                                        FullHttpRequest req) {
                                                                    dispatch(
                                                                            app,
                                                                            data,
                                                                            getRequest(ctx, req),
                                                                            response ->
                                                                                    emit(
                                                                                            response,
                                                                                            ctx));
                                                                }
                                                            });
                                        }
                                    })
                            .bind(config.port())
                            .sync()
                            .channel();
            channel.closeFuture()
                    .addListener(
                            future -> {
                                bossGroup.shutdownGracefully();
                                workerGroup.shutdownGracefully();
                            });
            started = true;
            return channel;
        } finally {
            if (!started) {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }

    /**
     * Get the framework request from a Netty channel context and request.
     *
     * @param ctx the Netty channel handler context
     * @param nettyRequest the incoming Netty HTTP request
     * @return the current server request
     */
    public static ServerRequestContract getRequest(
            ChannelHandlerContext ctx, FullHttpRequest nettyRequest) {
        String uri = nettyRequest.uri();
        int queryStart = uri.indexOf('?');
        String query = queryStart >= 0 ? uri.substring(queryStart + 1) : null;

        Map<String, String> headers = new LinkedHashMap<>();
        for (Map.Entry<String, String> header : nettyRequest.headers()) {
            headers.merge(
                    header.getKey(), header.getValue(), (existing, next) -> existing + ", " + next);
        }

        SocketAddress remote = ctx.channel().remoteAddress();
        String remoteAddr =
                remote instanceof InetSocketAddress inet
                        ? inet.getAddress().getHostAddress()
                        : null;

        return request(
                nettyRequest.method().name(),
                uri,
                query,
                nettyRequest.protocolVersion().text(),
                remoteAddr,
                headers,
                nettyRequest.content().toString(StandardCharsets.UTF_8));
    }

    /**
     * Write a framework response back out through the Netty channel, then close the connection.
     *
     * @param response the framework response
     * @param ctx the Netty channel handler context to write through
     */
    public static void emit(ResponseContract response, ChannelHandlerContext ctx) {
        byte[] body = response.getBody().getContents().getBytes(StandardCharsets.UTF_8);

        FullHttpResponse nettyResponse =
                new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf(
                                response.getStatusCode().getValue(), response.getReasonPhrase()),
                        Unpooled.wrappedBuffer(body));

        response.getHeaders()
                .getAll()
                .values()
                .forEach(
                        header ->
                                nettyResponse
                                        .headers()
                                        .add(header.getName(), header.getHeaderLine()));
        nettyResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.length);

        ctx.writeAndFlush(nettyResponse).addListener(ChannelFutureListener.CLOSE);
    }
}
