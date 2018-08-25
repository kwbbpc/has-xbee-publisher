package db;

import com.fasterxml.jackson.core.JsonProcessingException;
import db.sensor.SensorDao;

public interface DataPipe {

    void publishMessage(SensorDao sensorData, int messageId, byte[] payload);

}
