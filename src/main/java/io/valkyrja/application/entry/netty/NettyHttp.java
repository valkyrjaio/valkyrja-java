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
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.request.factory.RequestFactory;

/**
 * HTTP entry point for the Netty worker runtime.
 *
 * <p>Bootstraps the application once, then registers a Netty pipeline that dispatches every
 * incoming request to an isolated {@link io.valkyrja.container.manager.ChildContainer} for the
 * lifetime of that request.
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
                                                                    handle(
                                                                            app,
                                                                            data,
                                                                            getRequest(ctx, req));
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
     * Get the HTTP request from a Netty channel context and request.
     *
     * <p>Override to populate the request from Netty metadata (headers, body, remote address, etc.)
     * once the full request adapter exists.
     *
     * @param ctx the Netty channel handler context
     * @param request the incoming Netty HTTP request
     * @return the current server request
     */
    public static ServerRequestContract getRequest(
            ChannelHandlerContext ctx, FullHttpRequest request) {
        return RequestFactory.fromGlobals();
    }
}
