package com.pengli.ddbtest.web;

import com.pengli.ddbtest.entity.Record;
import com.pengli.ddbtest.entity.RecordWithLastKey;
import com.pengli.ddbtest.service.DdbPlayground;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class WebController {

    private final DdbPlayground ddbPlayground;

    @GetMapping(path = "/getItem", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public Mono<Record> getItem() throws Exception {
        return Mono.fromFuture(ddbPlayground.getItem());
    }

    @GetMapping(path = "/getItems", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public Flux<Record> getItems() throws Exception {
        return Flux.from(ddbPlayground.queryNormal()
                .flatMapIterable(Page::items));
    }

    @GetMapping(path = "/getItemsSk", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public Flux<Record> getItemsSk() throws Exception {
        return Flux.from(ddbPlayground.querySomeSortKey()
                .flatMapIterable(Page::items));
    }

    @GetMapping(path = "/get1stPage", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public Mono<RecordWithLastKey> get1stPage() throws Exception {
        ddbPlayground.queryPagination1stCall();
        return ddbPlayground.queryPagination1stCall();
    }

    @GetMapping(path = "/getNextPage/{pk}/{sk}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public Mono<RecordWithLastKey> getNextPage(@PathVariable("pk") final String pk,
            @PathVariable("sk") final String sk) throws Exception {
        return ddbPlayground.queryPaginationNextCall(pk, sk);
    }

}
