import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import data.Weather;
import db.DataPipe;
import db.sensor.SensorDao;

import java.util.Arrays;

public class XBeeListener implements IDataReceiveListener {

    private DataPipe dataHandler;

    public XBeeListener(DataPipe dataHandler){

        this.dataHandler = dataHandler;

        System.out.println("Listening on COM4");
    }

    public void dataReceived(XBeeMessage xBeeMessage) {


        try {

            SensorDao sensor = new SensorDao(xBeeMessage.getDevice());

            byte[] rawData = xBeeMessage.getData();
            int messageId = rawData[0];
            byte[] payload = Arrays.copyOfRange(rawData, 1, rawData.length);

            Weather.WeatherMessage msg = Weather.WeatherMessage.parseFrom(payload);

            this.dataHandler.publishMessage(sensor, messageId, payload);

        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("Data Received!");
        System.out.println("Device: " + xBeeMessage.getDevice().get16BitAddress());
        System.out.println("Data: " + xBeeMessage.getDataString());
        System.out.println("Data: " + xBeeMessage.getData());
    }
}
