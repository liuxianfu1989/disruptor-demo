package com.nico.disruptor.v1;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.Executors;

public class Main1 {
    public static void main(String[] args) {
        // The factory for the event
        LongEventFactory factory = new LongEventFactory();

        // Specify the size of the ring buffer, must be power of 2
        int bufferSize = 1024;

        // Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, Executors.defaultThreadFactory());

        // connect the handler
        disruptor.handleEventsWith(new LongEventHandler());

        // start the Disruptor, starts all threads running
        disruptor.start();

        // get the ring buffer from the disruptor to be used for publishing
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        // 官方例程
        // grab the next sequence
        long sequence = ringBuffer.next();
        try {
            LongEvent event = ringBuffer.get(sequence);
            event.set(8888L);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
