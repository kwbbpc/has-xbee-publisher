import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import data.Weather;
import db.DataPipe;
import db.sensor.SensorDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class XBeeListener implements IDataReceiveListener {

    private DataPipe dataHandler;
    private static final Logger logger = LoggerFactory.getLogger(XBeeListener.class);

    public XBeeListener(DataPipe dataHandler){

        this.dataHandler = dataHandler;

        logger.info("Listening on COM4");
    }

    public void dataReceived(XBeeMessage xBeeMessage) {


        logger.info("Got a new xbee message!");
        try {

            SensorDao sensor = new SensorDao(xBeeMessage.getDevice());

            byte[] rawData = xBeeMessage.getData();
            int messageId = rawData[0];
            byte[] payload = Arrays.copyOfRange(rawData, 1, rawData.length);

            logger.debug("Parsing weather message id {}: [{}]", messageId, payload);
            Weather.WeatherMessage msg = Weather.WeatherMessage.parseFrom(payload);

            logger.debug("Publishing message to sns queue");
            this.dataHandler.publishMessage(sensor, messageId, payload);

        }catch (Exception e){
            logger.error("Error: " + e.getMessage());
        }

        logger.debug("Data Received!");
        logger.debug("Device: " + xBeeMessage.getDevice().get16BitAddress());
        logger.debug("Data: " + xBeeMessage.getDataString());
        logger.debug("Data: " + xBeeMessage.getData());
    }
}
