package com.example.webservice.cache;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class TriangleCache<T, U> {
    private final Map<T, U> myCache = Collections.synchronizedMap(new HashMap<>());

    public void Push(T key, U value){
        if(!myCache.containsKey(key))
            myCache.put(key, value);
    }

    public U Get(T key){
        if(myCache.containsKey(key)){
            return myCache.get(key);
        }
        return null;
    }
}
