package com.pengli.ddbtest;

import com.pengli.ddbtest.entity.Record;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@SpringBootApplication
public class App {

    static DynamoDbAsyncTable<Record> table;
    static DynamoDbTable<Record> table2;

    static DynamoDbAsyncClient client;
    static DynamoDbClient client2;


    public static void main(String[] args) throws Exception{
//        init();
//        testGetItem();
//        testQueryNormal();
//        testQuerySomeSortKey();
//        testQueryPagination1stCall();
        SpringApplication.run(App.class, args);
    }




}
