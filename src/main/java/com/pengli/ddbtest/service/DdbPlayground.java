package com.pengli.ddbtest.service;

import com.pengli.ddbtest.entity.Record;
import com.pengli.ddbtest.entity.RecordKey;
import com.pengli.ddbtest.entity.RecordWithLastKey;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class DdbPlayground {

    DynamoDbAsyncTable<Record> table;
    DynamoDbTable<Record> table2;

    DynamoDbAsyncClient client;
    DynamoDbClient client2;

    public DdbPlayground(DynamoDbAsyncClient dynamoDbAsyncClient,
                DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                DynamoDbClient dynamoDbClient,
                DynamoDbEnhancedClient dynamoDbEnhancedClient
            ) {
        this.client = dynamoDbAsyncClient;
        this.table = dynamoDbEnhancedAsyncClient.table("pengli-test", TableSchema.fromClass(Record.class));
        this.client2 = dynamoDbClient;
        this.table2 = dynamoDbEnhancedClient.table("pengli-test", TableSchema.fromClass(Record.class));
    }

    public CompletableFuture<Record> getItem() throws Exception {
        CompletableFuture<Record> recordFuture = table.getItem(Key.builder()
                .partitionValue("001")
                .sortValue("peng")
                .build());
        System.out.println(recordFuture.get());
        return recordFuture;
    }

    public Flux<Page<Record>> queryNormal() throws Exception {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue("003")
                .build());
        PagePublisher<Record> pagePublisher = table.query(queryConditional);
        Flux<Page<Record>> recordFlux = Flux.from(pagePublisher);
        recordFlux.subscribe(record -> {
            System.out.println(record.items());
        });
        Thread.sleep(2000l);
        return recordFlux;
    }

    public Flux<Page<Record>> querySomeSortKey() throws Exception {
        QueryConditional queryConditional = QueryConditional.sortBeginsWith(Key.builder()
                .partitionValue("002")
                .sortValue("pp")
                .build());
        PagePublisher<Record> pagePublisher = table.query(queryConditional);
        Flux<Page<Record>> recordFlux = Flux.from(pagePublisher);
        recordFlux.subscribe(record -> {
            System.out.println(record.items());
            System.out.println(record.lastEvaluatedKey());
        });
        Thread.sleep(2000l);
        return recordFlux;
    }

    public Mono<RecordWithLastKey> queryPagination1stCall() throws Exception {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue("003")
                .build());
        QueryEnhancedRequest enhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(2)
                .build();
        PagePublisher<Record> pagePublisher = table.query(enhancedRequest);
        Flux<Page<Record>> recordFlux = Flux.from(pagePublisher);

        return recordFlux.next()
                .map(record -> new RecordWithLastKey(record.items(), getRecordKey(record.lastEvaluatedKey())));
    }

    /*
    Old way to retrieve only 1 page in Flux:
        If simply do subscribe like below, Flux by default will get all pages.
        Rewrite subscribe, we can control the page fetching.
        recordFlux.subscribe(record -> {
            System.out.println(record);
        });
        recordFlux.subscribe(new Subscriber<Page<Record>>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(Page<Record> recordPage) {
                System.out.println(recordPage.items());
                try {
                    // got the lastEvaluatedKey and call the next query
                    queryPagination2ndCall(recordPage.lastEvaluatedKey());
                } catch (Exception e) {}
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
     */
    public Mono<RecordWithLastKey> queryPaginationNextCall(String pk, String sk) throws Exception {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue("003")
                .build());
        Map<String, AttributeValue> lastEvaluatedKey = new HashMap<>();
        lastEvaluatedKey.put("ID", AttributeValue.builder().s(pk).build());
        lastEvaluatedKey.put("NAME", AttributeValue.builder().s(sk).build());
        QueryEnhancedRequest enhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .exclusiveStartKey(lastEvaluatedKey)
                .limit(2)
                .build();

        PagePublisher<Record> pagePublisher = table.query(enhancedRequest);
        Flux<Page<Record>> recordFlux = Flux.from(pagePublisher);

        return recordFlux.next()
                .map(record -> new RecordWithLastKey(record.items(), getRecordKey(record.lastEvaluatedKey())));
    }

    private RecordKey getRecordKey(Map<String, AttributeValue> valueMap) {
        RecordKey recordKey = null;
        if (valueMap != null) {
            recordKey = RecordKey.builder()
                    .pk(valueMap.get("ID").s())
                    .sk(valueMap.get("NAME").s())
                    .build();
        }
        return recordKey;
    }
}
