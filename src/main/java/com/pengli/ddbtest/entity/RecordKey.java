package com.pengli.ddbtest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class RecordKey {
    private String pk;
    private String sk;
}
