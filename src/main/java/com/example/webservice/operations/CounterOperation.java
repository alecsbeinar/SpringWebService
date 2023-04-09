package com.example.webservice.operations;

import org.springframework.stereotype.Component;

@Component
public class CounterOperation {
    private static int count;

    public void Add(){count++;}
    public int GetCount(){return count;}
}
