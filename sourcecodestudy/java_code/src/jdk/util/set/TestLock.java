package com.yao.sourcecode.jdk.set;

import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by user on 2018/4/26.
 */
public class TestLock {

    private final ReentrantLock takeLock = new ReentrantLock();

    /** Lock held by put, offer, etc */
    private final ReentrantLock putLock = new ReentrantLock();
    /** Wait queue for waiting puts */
    private final Condition notFull = putLock.newCondition();
    /** Wait queue for waiting takes */
    private final Condition notEmpty = takeLock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        TestLock lock = new TestLock();
        lock.test();
    }

    public void test() throws InterruptedException {
        takeLock.lock();
        try {
            while (true) {
                System.out.println("wating");
                notEmpty.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            takeLock.unlock();
        }

    }
}
