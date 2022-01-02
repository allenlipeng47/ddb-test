package com.pengli.ddbtest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class RecordWithLastKey {
    private List<Record> recordList;
    private RecordKey lastEvaluatedRecordKey;
}
