import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.digi.xbee.api.XBeeDevice;
import db.DataPipe;
import db.SNSPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerMain {


    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    private static final String USB_PORT =  "/dev/ttyUSB0";

    public static void main(String args[]){
        logger.info("Server is starting.....");


        logger.trace("Testing logger!");

        XBeeDevice myXBeeDevice = new XBeeDevice(USB_PORT, 9600);

        logger.info("Server was started listening to port {}", USB_PORT);


        try{

            AwsCredentialsLoader creds = new AwsCredentialsLoader("awsAccessKeys.properties");
            BasicAWSCredentials basicCreds = new BasicAWSCredentials(creds.getAccessKey(), creds.getSecretKey());

            logger.debug("AWS Credentials loaded: {}", creds.toString());
            DataPipe handler =
                    new SNSPublisher(new AWSStaticCredentialsProvider(basicCreds));

            myXBeeDevice.open();

            myXBeeDevice.addDataListener(new XBeeListener(handler));

            while (true) {
                //hang and loop forever
                Thread.sleep(1);
            }
        }catch (Exception e){
            logger.error("Error: " + e);
        }
    }
}
