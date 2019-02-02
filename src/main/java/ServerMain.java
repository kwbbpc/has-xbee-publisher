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
    private static boolean isDeviceOpen = false;
    private static boolean isDeviceCreated = false;
    private static XBeeDevice myXBeeDevice = null;

    public static void main(String args[]){
        logger.info("The Publisher server is starting.....");


        while(!isDeviceCreated) {
            try {
                myXBeeDevice = new XBeeDevice(USB_PORT, 9600);
                logger.debug("XBee device created on {}", USB_PORT);
                isDeviceCreated = true;
            } catch (Exception e) {
                logger.error("There was an error creating the XBEE device on port {}", USB_PORT);
                logger.info("Retrying xbee creation...");
            }
        }


        BasicAWSCredentials basicCreds = new BasicAWSCredentials("","");
        try {
            AwsCredentialsLoader creds = new AwsCredentialsLoader("awsAccessKeys.properties");
            basicCreds = new BasicAWSCredentials(creds.getAccessKey(), creds.getSecretKey());
            logger.debug("AWS Credentials loaded: {}", creds.toString());


        }catch (Exception e){
            logger.error("Error loading AWS credentials: {}", e);
            logger.error("Fatal error, can't connect to AWS.  Exiting.");
            System.exit(1);
        }

        DataPipe handler = null;
        try{

            logger.info("Creating the SNS Publisher");
            handler =
                    new SNSPublisher(new AWSStaticCredentialsProvider(basicCreds));
            logger.info("SNS Publisher created successfully");
        }catch (Exception e){
            logger.error("Error creating the SNS publisher.  Cannot continue.");
            System.exit(1);
        }

        while(!isDeviceOpen) {
            try {


                logger.info("Opening USB Port at {}...", USB_PORT);
                myXBeeDevice.open();
                logger.info("USB {} opened successfully!", USB_PORT);

                myXBeeDevice.addDataListener(new XBeeListener(handler));
                isDeviceOpen = true;
                break;

            } catch (Exception e) {
                logger.error("Error when reading from xbee device.  Will retry in 5 seconds.  Error: {}", e);
                sleep();
            }
        }

        //loop forever
        while(true){
            sleep();
        }
    }

    private static void sleep(){
        try {
            Thread.sleep(5000);
        } catch (Exception ex) {
            logger.error("Error during sleep operation: {}", ex);
        }
        return;
    }
}
