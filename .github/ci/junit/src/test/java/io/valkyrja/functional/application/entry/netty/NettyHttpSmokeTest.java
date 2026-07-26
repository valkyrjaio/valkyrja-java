/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.application.entry.netty;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

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
import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.entry.netty.NettyHttp;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Smoke test for the {@link NettyHttp} adapter over a real Netty pipeline.
 *
 * <p>The blocking {@code run(...)} loop is not exercised directly (it blocks on the channel's close
 * future and never returns); instead the adapter's request path — {@link NettyHttp#getRequest}
 * feeding {@link WorkerHttp#handle} — is driven through a real, stoppable pipeline bound to an
 * ephemeral port.
 */
@Timeout(20)
final class NettyHttpSmokeTest {

    @Test
    void serverDispatchesAnIncomingRequestThroughTheAdapter() throws Exception {
        ApplicationContract app = WorkerHttp.bootstrap(new HttpConfig());
        CountDownLatch dispatched = new CountDownLatch(1);
        RequestHandlerContract handler = mock(RequestHandlerContract.class);
        doAnswer(
                        invocation -> {
                            dispatched.countDown();
                            return null;
                        })
                .when(handler)
                .run(any());
        app.getContainer().setSingleton(RequestHandlerContract.class, handler);
        ContainerData data = (ContainerData) app.getContainer().getData();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
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
                                                                    WorkerHttp.handle(
                                                                            app,
                                                                            data,
                                                                            NettyHttp.getRequest(
                                                                                    ctx, req));
                                                                }
                                                            });
                                        }
                                    })
                            .bind(0)
                            .sync()
                            .channel();

            int port = ((InetSocketAddress) channel.localAddress()).getPort();
            send(port, dispatched);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static void send(int port, CountDownLatch dispatched) throws IOException, InterruptedException {
        try (Socket socket = new Socket("localhost", port)) {
            OutputStream out = socket.getOutputStream();
            out.write(
                    "GET / HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n"
                            .getBytes(StandardCharsets.US_ASCII));
            out.flush();
            assertTrue(
                    dispatched.await(10, TimeUnit.SECONDS),
                    "the adapter did not dispatch the incoming request");
        }
    }
}
