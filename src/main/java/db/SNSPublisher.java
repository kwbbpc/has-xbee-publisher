package db;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.node.ObjectNode;
import db.sensor.SensorDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.java2d.pipe.Region;
import util.JsonUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SNSPublisher implements DataPipe {

    private AmazonSNS sns;

    private AmazonDynamoDB client;

    private Map<Integer, String> arnCacheMap;

    private static final Logger logger = LoggerFactory.getLogger(SNSPublisher.class);

    public SNSPublisher(AWSCredentialsProvider creds){
        this.sns = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(creds).build();

        //lookup all the types of message ids in dynamo db and create all the sns topics needed.
        this.arnCacheMap = new HashMap<Integer, String>();

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(creds).build();

        ScanRequest scanRequest = new ScanRequest()
                .withTableName("MessageIdMapping");

        ScanResult result = client.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.getItems()){
            //load the results into a cached map.

            logger.info("Registering message id {} to arn {}", item.get("messageId").getN(), item.get("snsArn").getS());

            this.arnCacheMap.put(Integer.parseInt(item.get("messageId").getN()), item.get("snsArn").getS());
        }
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

            //get the arn out of the cached map.

            String arn = this.arnCacheMap.get(messageId);
            if(arn == null){
                arn = this.arnCacheMap.get(-1);
            }

            PublishRequest pub = new PublishRequest(arn, message
            );

            PublishResult result = sns.publish(pub);

            System.out.println(result.getMessageId());


        }catch (Exception e){
            //Do somethign better to report, maybe publish to an errors topic?
            System.err.println(e);
        }

    }
}
