package com.nico.disruptor.v2;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class DisruptorMqServiceImplTest {

    @Autowired
    private DisruptorMqService disruptorMqService;

    @Test
    public void sayHelloMqTest() throws InterruptedException {
        disruptorMqService.sayHelloMq("消息到了，hello world");
        log.info("消息已经方式");
        Thread.sleep(2000);
    }
}