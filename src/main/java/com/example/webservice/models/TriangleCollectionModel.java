package com.example.webservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class TriangleCollectionModel {
    @JsonProperty("collection")
    public Collection<Triangle> collection;
}
