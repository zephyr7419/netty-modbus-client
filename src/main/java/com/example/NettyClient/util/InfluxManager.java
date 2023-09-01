package com.example.NettyClient.util;

import com.example.NettyClient.config.InfluxConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class InfluxManager {

    private final InfluxConfig config;
    private final Gson gson;
    private WriteApiBlocking writeApi;

    @PostConstruct
    public void init() {
        writeApi = config.influxDBClient().getWriteApiBlocking();
    }

    public void saveDataToInfluxDB(String topic, Object objectValue) {
        try {
            Point pointBuilder = Point.measurement(topic).addTag(topic, topic);

            if (objectValue instanceof Integer) {
                pointBuilder.addField(topic, (Integer) objectValue);
            } else if (objectValue instanceof Double) {
                pointBuilder.addField(topic, (Double) objectValue);
            } else {
                pointBuilder.addField(topic, gson.toJson(objectValue));
            }

            Point point = pointBuilder.time(Instant.now().toEpochMilli(), WritePrecision.MS);

            log.info("Writing point: {}", point.toLineProtocol());
            writeApi.writePoint(point);
        } catch(Exception e) {
            log.error("Error writing to InfluxDB", e);
        }
    }


}
