package com.example.webservice.operations;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class CounterOperation {
    private final Lock lock = new ReentrantLock();
    private static int count;

    public void add() {
        lock.lock();
        count++;
        lock.unlock();
    }

    public int getCount() {
        return count;
    }
}
