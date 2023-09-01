package com.example.NettyClient.util;

import com.example.NettyClient.handler.ClientHandler;
import com.example.NettyClient.handler.ModbusReqHandler;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Slf4j
//@Component
public class MachineConnector {

    private static final int MACHINE_COUNT = 1;
    private final int[] transactionIds = new int[MACHINE_COUNT];
    private final ModbusReqHandler modbusReqHandler;
    private final ClientHandler clientHandler;
    private int[] startAddresses = {0x0305};
    private int[] numberOfRegs = {1};
    private List<Channel> channels;
    private Bootstrap bootstrap;
    private ModbusTCPMaster modbusTCPMaster = new ModbusTCPMaster("localhost", 502, 2000, true);

    public MachineConnector(ModbusReqHandler modbusReqHandler, ClientHandler clientHandler) {
        this.modbusReqHandler = modbusReqHandler;
        this.clientHandler = clientHandler;
        this.channels =  new ArrayList<>();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer(this.clientHandler));

        for (int i = 0; i < MACHINE_COUNT; i++) {
            connectMachine(i);
        }
    }

    private void connectMachine(int index) {
        if (channels.size() > index && channels.get(index) != null && channels.get(index).isActive()) {
            return; // 이미 연결된 경우
        }
        ChannelFuture future = bootstrap.connect("localhost", 5020 + index);

        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                Channel channel = channelFuture.channel();
                while (channels.size() <= index) {
                    channels.add(null); // 인덱스까지 null로 채우기
                }
                channels.set(index, channel);
            } else {
                log.error("Failed to connect to machine at index: " + index, channelFuture.cause());
            }
        });
    }

    @Scheduled(fixedDelay = 60000) // 1분간격
    private void requestAndCheckData() {
        for (int i = 0; i < MACHINE_COUNT; i++) {
            Channel channel = channels.get(i);

            if (channel == null || !channel.isActive()) {
                transactionIds[i] = 0;
                connectMachine(i); // 연결이 끊어진 경우 재연결
                channel = channels.get(i); // 재연결 후 채널 얻기
                log.info("channel : {}", i);
            }

            for (int j = 0; j < startAddresses.length; j++) {
//                byte[] request = modbusReqHandler.createReadRequest(startAddresses[j], numberOfRegs[j]);
                byte[] request = modbusReqHandler.createTCPReadRequest((byte) transactionIds[i], startAddresses[j], numberOfRegs[j]);
                try {
                    InputRegister inputRegister = (InputRegister) modbusTCPMaster.readCoils(startAddresses[j], numberOfRegs[j]);
                    int value = inputRegister.getValue();
                    log.info("value: {}", value);
                } catch (ModbusException e) {
                    throw new RuntimeException(e);
                }
                ByteBuf buffer = Unpooled.copiedBuffer(request);
                // 비동기로 데이터 요청
                ChannelFuture future = channel.writeAndFlush(buffer);

                future.addListener((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        log.error("Failed to send data request to machine at index: ", channelFuture.cause());
                    }
                });

                transactionIds[i]++;
            }

            // 모든 요청을 보낸 후에 로그를 찍습니다.
            log.info("All requests sent to channel: {}", i);

            // 해당 기계에 데이터 요청
//            channel.writeAndFlush("데이터 요청 메시지\n");

            // 응답을 처리하는 로직은 ClientHandler 내에서 구현
        }
    }

}
