package com.nico.disruptor.v2;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloEventHandler implements EventHandler<MessageModel> {
    @Override
    public void onEvent(MessageModel event, long sequence, boolean endOfBatch) throws Exception {
        Thread.sleep(1000);
        log.info("消费者处理消息开始");
        if (event != null) {
            log.info("消费者消费的信息是：{}", event);
        }
        log.info("消费者处理消息结束");
    }
}
