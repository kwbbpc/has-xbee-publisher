package db;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.sensor.SensorDao;
import util.JsonUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SNSPublisher implements DataPipe {

    private AmazonSNS sns;

    public SNSPublisher(AWSCredentialsProvider creds){
        this.sns = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(creds).build();

        //lookup all the types of message ids in dynamo db and create all the sns topics needed.
    }




    public void publishMessage(SensorDao sensorData, int messageId, byte[] payload) {

        ObjectNode obj = JsonUtils.MAPPER.createObjectNode();
        obj.put("messageId", messageId);
        obj.put("nodeId", sensorData.getAddress64bit());
        obj.set("nodeInfo", JsonUtils.MAPPER.valueToTree(sensorData));

        String base64Payload = new String(Base64.getEncoder().encode(payload));

        obj.put("payload", base64Payload);
        obj.put("encoding", "base64");

        try {
            String message = JsonUtils.MAPPER.writeValueAsString(obj);

            PublishRequest pub = new PublishRequest(
                    "arn:aws:sns:us-east-1:051846041120:weather", message
            );

            PublishResult result = sns.publish(pub);

            System.out.println(result.getMessageId());


        }catch (Exception e){
            //Do somethign better to report, maybe publish to an errors topic?
            System.err.println(e);
        }

    }
}
