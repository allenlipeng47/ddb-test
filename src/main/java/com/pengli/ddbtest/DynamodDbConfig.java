package com.pengli.ddbtest;

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

public class DynamodDbConfig {

    public static DynamoDbAsyncClient getDynamoDbAsyncClient() {
        DynamoDbAsyncClientBuilder clientBuilder = DynamoDbAsyncClient.builder()
                .region(Region.US_EAST_1);

        AwsCredentials fakeCredentials = AwsBasicCredentials.create("xxx", "xxx");
        clientBuilder.credentialsProvider(StaticCredentialsProvider.create(fakeCredentials));

        return clientBuilder.build();
    }

    public static DynamoDbClient getDynamoDbClient() {
        DynamoDbClientBuilder clientBuilder = DynamoDbClient.builder()
                .region(Region.US_EAST_1);

        AwsCredentials fakeCredentials = AwsBasicCredentials.create("xxx", "xxx");
        clientBuilder.credentialsProvider(StaticCredentialsProvider.create(fakeCredentials));

        return clientBuilder.build();
    }

    public static DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient() {
        DynamoDbAsyncClient dynamoDbAsyncClient = getDynamoDbAsyncClient();
        return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(dynamoDbAsyncClient).build();
    }

    public static DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
        DynamoDbClient dynamoDbClient = getDynamoDbClient();
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

}
