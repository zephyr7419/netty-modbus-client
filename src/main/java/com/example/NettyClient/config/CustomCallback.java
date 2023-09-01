package com.example.NettyClient.config;

import com.example.NettyClient.util.modbus.ModbusBusiness;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
public class CustomCallback implements MqttCallback {
    private MqttClient mqttClient;
    private String topic;
    private ModbusBusiness businessService;

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        log.error("Mqtt Broker is disconnected : {}", disconnectResponse.getException().getMessage());
        try {
            mqttClient.reconnect();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String[] topicParts = topic.split("/");
        if (topicParts.length >= 2) {
            String deviceIp = topicParts[1]; // 또는 적절한 인덱스
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            businessService.updateRegister(deviceIp, payload);
        }
    }


    @Override
    public void deliveryComplete(IMqttToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("Mqtt Broker is Connected!");
        try {
            mqttClient.subscribe(topic, 0);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {

    }
}
