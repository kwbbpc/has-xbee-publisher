package core;

import com.digi.xbee.api.AbstractXBeeDevice;

public interface SensorHandler {

    void processMessage(byte[] data, AbstractXBeeDevice device);

}
