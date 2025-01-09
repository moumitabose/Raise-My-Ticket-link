package com.example.support_ticket_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2ClientBuilder;
import software.amazon.awssdk.services.lexruntimev2.LexRuntimeV2Client;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.translate.TranslateClient;


import java.time.Duration;


@Configuration
@PropertySource("classpath:application.properties")
public class AwsConfig {


    @Bean
    public SnsClient snsClient() {

        AwsCredentials credentials = DefaultCredentialsProvider.create().resolveCredentials();
        System.out.println("AWS Access Key: " + credentials.accessKeyId());
        System.out.println("AWS Secret Key: " + credentials.secretAccessKey());

        return SnsClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

    }


    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.US_EAST_2) // Use the appropriate region
                .credentialsProvider(DefaultCredentialsProvider.create()) // Use default credentials provider
                .build();
    }


    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_2) // Use your desired region
                .credentialsProvider(DefaultCredentialsProvider.create()) // Use default credentials provider
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }




    @Bean
    public SesClient sesClient() {
        return SesClient.create();
    }


    @Bean
    public PollyClient pollyClient() {
        return PollyClient.builder()
                .region(Region.US_EAST_2)  // Set your preferred region
                .build();
    }

    @Bean
    public TranslateClient translateClient() {
        return TranslateClient.builder()
                .region(Region.US_EAST_2)  // Use the appropriate AWS region
                .build();
    }


    @Bean
    public LexRuntimeV2Client lexRuntimeV2Client() {
        return LexRuntimeV2Client.builder()
                .region(Region.US_EAST_1)  // Set your AWS region (example: US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())  // Use default credentials provider
                .build();
    }

    // Configure AWS Lex Models V2 Client (for managing bots and models)
    @Bean
    public LexModelsV2Client lexModelsV2Client() {
        return LexModelsV2Client.builder()
                .region(Region.US_EAST_1)  // Set your AWS region (example: US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())  // Use default credentials provider
                .build();
    }

}
