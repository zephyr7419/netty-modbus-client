//package com.example.NettyClient.util.mqtt;
//
//import com.example.NettyClient.util.modbus.ModbusCommunication;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.eclipse.paho.mqttv5.client.MqttClient;
//import org.eclipse.paho.mqttv5.common.MqttMessage;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class DataPublisher {
//
//    private final String[] deviceIps = { "127.0.0.1", /* 다른 IP들 */ };
//    private final MqttClient mqttClient;
//    private final ModbusCommunication modbusCommunication;
//
//    @Scheduled(fixedRate = 60000)
//    public void publishData() {
//        for (String deviceIp : deviceIps) {
//            try {
//                byte[] data = modbusCommunication.readData(deviceIp);
//
//                StringBuilder dataStr = new StringBuilder();
//                for (byte b : data) {
//                    BigDecimal bd = new BigDecimal(b);
//                    dataStr.append(bd.stripTrailingZeros().toPlainString()).append(", ");
//                }
//
//                MqttMessage message = new MqttMessage();
//                message.setPayload(dataStr.toString().getBytes(StandardCharsets.UTF_8));
//                String topic = "application/" + deviceIp.replace('.', '_'); // IP 주소의 '.'을 '_'로 대체
//                mqttClient.publish(topic, message); // IP 주소를 포함하여 topic을 구분
//            } catch (Exception e) {
//                log.error("Failed to publish data for device with IP: {}", deviceIp, e);
//            }
//        }
//    }
//}
