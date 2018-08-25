package db.sensor;

import com.digi.xbee.api.AbstractXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

public class SensorDao {

    private String address64bit;
    private String address16bit;
    private String firmwareVersion;
    private String hardwareVersion;
    private int hardwareVersionId;
    private String nodeId;
    private String panId;
    private String xbeeProtocol;

    public SensorDao() {
    }

    public SensorDao(AbstractXBeeDevice device) throws XBeeException{
        this.address16bit = device.get16BitAddress().toString();
        this.address64bit = device.get64BitAddress().toString();
        if(device.getFirmwareVersion() != null)
            this.firmwareVersion = device.getFirmwareVersion();

        if(device.getHardwareVersion() != null) {
            this.hardwareVersion = device.getHardwareVersion().getDescription();
            this.hardwareVersionId = device.getHardwareVersion().getValue();
        }
        this.nodeId = device.getNodeID();
        this.panId = null;
        this.xbeeProtocol = device.getXBeeProtocol().getDescription();
    }


    public Integer getHardwareVersionId() {
        return hardwareVersionId;
    }

    public void setHardwareVersionId(Integer hardwareVersionId) {
        this.hardwareVersionId = hardwareVersionId;
    }

    public String getAddress64bit() {
        return address64bit;
    }

    public void setAddress64bit(String address64bit) {
        this.address64bit = address64bit;
    }

    public String getAddress16bit() {
        return address16bit;
    }

    public void setAddress16bit(String address16bit) {
        this.address16bit = address16bit;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }

    public String getXbeeProtocol() {
        return xbeeProtocol;
    }

    public void setXbeeProtocol(String xbeeProtocol) {
        this.xbeeProtocol = xbeeProtocol;
    }
}
