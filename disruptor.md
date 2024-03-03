# Disruptor

## 介绍

主页：https://lmax-exchange.github.io/disruptor/

源码：https://github.com/LMAX-Exchange/disruptor

## Disruptor的特点

对比ConcurrentLinkedQueue: 链表实现

jdk中没有ConcurrentArrayQueue

Disruptor是数组实现的

无锁，高并发，使用环形Buffer，直接覆盖（不要清除）旧数据，降低GC频率

实现了基于事件的生产者消费者模式（观察者模式）

## RingBuffer

环形队列

RingBuffer的序号，指向下一个可用的元素

采用数组实现，没有首尾指针

对比ConcurrentLinkedQueue，采用数组实现的速度更快。

```
假设长度为8，当添加到第12个元素的时候在哪个序号上呢，用12%8决定
当Buffer被填满的时候到底是覆盖还是等待，由Producer决定
长度设为2的n次幂，利于二进制计算，例如：12%8=12 & （8 - 1） pos = num & (size - 1)
```

### Disruptor开发步骤

1、定义Event--队列中需要处理的元素

2、定义EventFactory，用于填充队列

> 这里牵扯到效率问题：disruptor初始化的时候，会调用Event工厂，对ringBuffer进行内存的提前分配

> GC产频率会降低

3、定义EventHandler(消费者)，处理容器中的元素

### 事件发布模板

```java
// 定义事件
public class LongEvent {
    private long value;

    public void set(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LongEvent{" +
                "value=" + value +
                "}";

    }
}
```

```java
// 产生事件的工厂
public class LongEventFactory implements EventFactory<LongEvent> {
    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
```

```java
// 消费者消费事件处理
public class LongEventHandler implements EventHandler<LongEvent> {

    public static long count = 0;

    /**
     *
     * @param longEvent
     * @param sequence RingBuffer的序号
     * @param endOfBatch 是否为最后一个元素
     * @throws Exception
     */
    @Override
    public void onEvent(LongEvent longEvent, long sequence, boolean endOfBatch) throws Exception {
        count++;
        System.out.println("[" + Thread.currentThread().getName() + "]" + longEvent + " 序号：" + sequence + " endOfBatch: " + endOfBatch);
    }
}
```

```java
// disruptor示例代码
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
```

### ProducerType生产者线程模式

ProducerType有两种模式Producer.MULTI和Producer.SINGLE

默认是MULTI，表示在多线程模式下产生sequence

如果确认是单线程生产者，那么可以指定SINGLE，效率会提升

### 等待策略

1、（常用）BlockingWaitStrategy: 通过线程阻塞的方式，等待生产者唤醒，被唤醒后，再循环检查依赖的sequence是否已经消费。

2、BusySpinWaitStrategy: 线程一直自旋等待，可能比较耗cpu

3、LiteBlockingWaitStrategy: 线程阻塞等待生产者唤醒，与BlockingWaitStrategy相比，区别在signalNeeded.getAndSet，如果两个线程同时访问waitfor, 一个访问signalAll时，可以减少lock加锁次数。

4、LiteTimeoutBlockingWaitStrategy: 与LiteBlockingWaitStrategy相比，设置了阻塞时间，超过时间后抛异常。

5、PhasedBackoffWaitStrategy: 根据时间参数和传入的等待策略来决定使用哪种等待策略。

6、TimeoutBlockingWaitStrategy: 相对于BlockingWaitStrategy来说，设置了等待时间，超过后抛异常。

7、（常用）YieldingWaitStrategy: 尝试100次，然后Thread.yield()让出cpu.

8、（常用）SleepingWaitStrategy: sleep

### 消费者异常处理

默认：disruptor.setDefaultExceptionHandler()

覆盖：disruptor.handleExceptionFor().with()
