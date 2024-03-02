package com.nico.disruptor.v2;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MQManager {

    @Bean("messageModel")
    public RingBuffer<MessageModel> messageModelRingBuffer() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        HelloEventFactory factory = new HelloEventFactory();

        int bufferSize = 1024 * 256;

        Disruptor<MessageModel> disruptor = new Disruptor<>(factory, bufferSize, executor,
                ProducerType.SINGLE, new BlockingWaitStrategy());

        disruptor.handleEventsWith(new HelloEventHandler());

        disruptor.start();

        RingBuffer<MessageModel> ringBuffer = disruptor.getRingBuffer();
        return ringBuffer;

    }
}
