package com.pengli.ddbtest;


import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Data
public class Record {
    private String id;
    private String name;
    private String city;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("ID")
    public String getId() {
        return id;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("NAME")
    public String getName() {
        return name;
    }

    @DynamoDbSecondarySortKey(indexNames = {"CITY-index"})
    @DynamoDbAttribute("CITY")
    public String getCity() {
        return city;
    }

}
