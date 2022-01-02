package com.pengli.ddbtest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@Configuration
public class DynamodDbConfig {

    @Bean
    public DynamoDbAsyncClient getDynamoDbAsyncClient() {
        DynamoDbAsyncClientBuilder clientBuilder = DynamoDbAsyncClient.builder()
                .region(Region.US_EAST_1);

        AwsCredentials fakeCredentials = AwsBasicCredentials.create("xxx", "xxx");
        clientBuilder.credentialsProvider(StaticCredentialsProvider.create(fakeCredentials));

        return clientBuilder.build();
    }

    @Bean
    public DynamoDbClient getDynamoDbClient() {
        DynamoDbClientBuilder clientBuilder = DynamoDbClient.builder()
                .region(Region.US_EAST_1);

        AwsCredentials fakeCredentials = AwsBasicCredentials.create("xxx", "xxx");
        clientBuilder.credentialsProvider(StaticCredentialsProvider.create(fakeCredentials));

        return clientBuilder.build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient(DynamoDbAsyncClient dynamoDbAsyncClient) {
        return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(dynamoDbAsyncClient).build();
    }

    @Bean
    public DynamoDbEnhancedClient getDynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

}
