package com.example.NettyClient.util;

import com.example.NettyClient.handler.ModbusRespHandler;
import com.example.NettyClient.handler.ResponseHandler;
import com.example.NettyClient.protocol.ModbusProtocol;
import com.example.NettyClient.protocol.ReadRequestParameters;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.ghgande.j2mod.modbus.util.ModbusUtil.calculateCRC;

@Slf4j
//@Component
public class MachineConnector {
    private final String serverHost = "172.30.1.233";
    private final int serverPort = 5300;

    private EventLoopGroup group;
    private Channel channel;
    private final Bootstrap bootstrap = new Bootstrap();

    public MachineConnector() {
        group = new NioEventLoopGroup();
        initializeClient();
    }

    private void initializeClient() {

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        // Add your handlers here if needed
                        pipeline.addLast(new ResponseHandler());
                    }
                });

        try {
            // Connect to the server asynchronously
            ChannelFuture future = bootstrap.connect(serverHost, serverPort);
            future.addListener((ChannelFutureListener) futureListener -> {
                if (futureListener.isSuccess()) {
                    log.info("Connected to the server");
                    channel = futureListener.channel();
                } else {
                    log.error("Failed to connect to the server");
                }
            });
        } catch (Exception e) {
            log.error("Error while initializing the client", e);
        }
    }

//    @Scheduled(fixedDelay = 60000) // 1분 간격
    public void requestAndCheckData() {
        if (channel != null && channel.isActive()) {
            // Create and send your Modbus request message here
            ByteBuffer buffer = ByteBuffer.allocate(14);
            buffer.order(ByteOrder.BIG_ENDIAN);

            buffer.put((byte) 1);
            buffer.put((byte) 0x03);
            buffer.putShort((short) 300);
            buffer.putShort((short) 10);
            int[] crc = calculateCRC(buffer.array(), 0, buffer.position());
            byte[] crcBytes = new byte[2];
            crcBytes[0] = (byte) ((crc[0] >> 8) & 0xFF);
            crcBytes[1] = (byte) (crc[0] & 0xFF);
            buffer.put(crcBytes[0]);
            buffer.put(crcBytes[1]);
            ModbusProtocol protocol = new ModbusProtocol();
            ReadRequestParameters parameters = new ReadRequestParameters((byte) 1, 0x0300, 10);
            byte[] apply = protocol.getReadRequest.apply(parameters);
            channel.writeAndFlush(apply).addListener((GenericFutureListener<Future<Void>>) future -> {
                if (!future.isSuccess()) {
                    log.error("Failed to send request", future.cause());
                }
            });

            // Handle the response in your Netty handlers
            
        } else {
            log.error("Channel is not active. Reconnecting...");
            initializeClient();
        }
    }

    @PreDestroy
    public void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
