import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.digi.xbee.api.XBeeDevice;
import db.DataPipe;
import db.SNSPublisher;

public class ServerMain {


    public static void main(String args[]){

        System.out.println("Server is starting.....");
        XBeeDevice myXBeeDevice = new XBeeDevice("/dev/ttyUSB0", 9600);



        try{

            AwsCredentialsLoader creds = new AwsCredentialsLoader("awsAccessKeys.properties");
            BasicAWSCredentials basicCreds = new BasicAWSCredentials(creds.getAccessKey(), creds.getSecretKey());

            DataPipe handler =
                    new SNSPublisher(new AWSStaticCredentialsProvider(basicCreds));

            myXBeeDevice.open();

            myXBeeDevice.addDataListener(new XBeeListener(handler));

            while (true) {
                Thread.sleep(1);
            }
        }catch (Exception e){
            System.out.println("Error: " + e);
        }
    }
}
