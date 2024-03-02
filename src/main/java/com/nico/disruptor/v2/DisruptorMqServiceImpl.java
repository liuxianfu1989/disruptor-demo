package com.nico.disruptor.v2;

import com.lmax.disruptor.RingBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@Service
public class DisruptorMqServiceImpl implements DisruptorMqService {

    @Autowired
    private RingBuffer<MessageModel> messageModelRingBuffer;

    @Override
    public void sayHelloMq(String message) {
        log.info("record the message: {}", message);
        long sequence = messageModelRingBuffer.next();
        try {
            MessageModel event = messageModelRingBuffer.get(sequence);
            event.setMessage(message);
            log.info("add message: {} to mq", event);
        } catch (Exception e) {
            log.error("error: ",e);
        } finally {
            messageModelRingBuffer.publish(sequence);
        }
    }
}
