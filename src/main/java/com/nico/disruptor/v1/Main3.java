package com.nico.disruptor.v1;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

public class Main3 {

    public static void main(String[] args) throws IOException {
        // Specify the size of the ring buffer, must be power of 2
        int bufferSize = 1024;
        // Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);
        // Connect the handler
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> System.out.println("Event: " + event));
        // start the disruptor, starts all threads running
        disruptor.start();
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent(((event, sequence) -> event.set(10000L)));
        ringBuffer.publishEvent((event, sequence, l1) -> event.set(l1), 10000L);
        System.in.read();
    }
}
