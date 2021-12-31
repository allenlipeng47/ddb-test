package com.pengli.ddbtest;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
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

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class App {

    static DynamoDbAsyncTable<Record> table;
    static DynamoDbTable<Record> table2;

    static DynamoDbAsyncClient client;
    static DynamoDbClient client2;


    public static void main(String[] args) throws Exception{
        init();
//        testGetItem();
//        testQueryNormal();
//        testQuerySomeSortKey();
        testQueryPagination1stCall();
    }

    public static void init() {
        client = DynamodDbConfig.getDynamoDbAsyncClient();
        table = DynamodDbConfig.getDynamoDbEnhancedAsyncClient().table("pengli-test", TableSchema.fromClass(Record.class));

        client2 = DynamodDbConfig.getDynamoDbClient();
        table2 = DynamodDbConfig.getDynamoDbEnhancedClient().table("pengli-test", TableSchema.fromClass(Record.class));
    }

    public static void testGetItem() throws Exception {
        CompletableFuture<Record> recordFuture = table.getItem(Key.builder()
                .partitionValue("001")
                .sortValue(33)
                .build());
        System.out.println(recordFuture.get());
    }

    public static void testQueryNormal() throws Exception {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue("003")
                .build());
        PagePublisher<Record> pagePublisher = table.query(queryConditional);
        Flux<Page<Record>> recordFlux = Flux.from(pagePublisher);
        recordFlux.subscribe(record -> {
            System.out.println(record.items());
        });
        Thread.sleep(2000l);
    }

    public static void testQuerySomeSortKey() throws Exception {
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
    }

    public static void testQueryPagination1stCall() throws Exception {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue("003")
                .build());
        QueryEnhancedRequest enhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(2)
                .build();
        PagePublisher<Record> pagePublisher = table.query(enhancedRequest);
        Flux<Page<Record>> recordFlux = Flux.from(pagePublisher);
        /*
        If simply do subscribe like below, Flux by default will get all pages.
        Rewrite subscribe, we can control the page fetching.
        recordFlux.subscribe(record -> {
            System.out.println(record);
        });
         */
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
                    testQueryPagination2ndCall(recordPage.lastEvaluatedKey());
                } catch (Exception e) {}
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
        Thread.sleep(4000l);    // above subscribe is async. So wait a little time.
    }

    public static void testQueryPagination2ndCall(Map<String, AttributeValue> lastEvaluatedKey) throws Exception {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue("003")
                .build());
        QueryEnhancedRequest enhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .exclusiveStartKey(lastEvaluatedKey)
                .limit(2)
                .build();

        PagePublisher<Record> pagePublisher = table.query(enhancedRequest);
        Flux<Page<Record>> recordFlux = Flux.from(pagePublisher);
        recordFlux.subscribe(record -> {
            System.out.println("pengli, " + record.items());
        });
        Thread.sleep(4000l);
    }


}
