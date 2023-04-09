package com.example.webservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;

public class TriangleCollectionResultModel {
    @JsonProperty("result")
    public Collection<CalculatingResultModel> results;

    @JsonProperty("memberStatistics")
    public Collection<TriangleMemberStatistic> stats;

    public TriangleCollectionResultModel(Collection<CalculatingResultModel> results, Collection<TriangleMemberStatistic> stats) {
        this.results = results;
        this.stats = stats;
    }
}
