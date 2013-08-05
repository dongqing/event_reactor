package com.taobao.ju.item.eventreactor.core;

import java.util.concurrent.LinkedBlockingQueue;

/**
 各个{@link Reactor}之间传递事件{@link Event}的通道，
 同时也是{@link Reactor}内部的管道{@link Pipeline}存储事件{@link Event}的容器。

 */
public class EventQueue {

    private LinkedBlockingQueue<Event> linkedBlockingQueue = new LinkedBlockingQueue<Event>();

    /**
     * 从尾部插入{@link Event}，如果容器满了，则一直阻塞。
     * @param event   需要插入队列尾部的事件{@link Event}
     */
    public void put(Event event) throws InterruptedException {
        this.linkedBlockingQueue.put(event);
    }

    /**
     * 从头部获取事件{@link Event}，如果容器满了，则一直阻塞，直到队列非空。
     * @return 从头部获取到的事件
     */
    public Event take() throws InterruptedException {
        return linkedBlockingQueue.take();

    }

    /**
     * 向队列尾部插入一个事件{@link Event}，如果队列满了，则返回false;
     * 如果没有满，成功插入了，则返回true;
     * @param event
     * @return  是否向队列尾部插入一个事件成功。
     */
    public boolean offer(Event event){
      return   linkedBlockingQueue.offer(event);
    }


    /**
     * 从队列头获取一个事件{@link Event}，如果队列为空，则立即返回NULL.
     * @return    从队列头获取到的事件，如果队列为空，则立即返回NULL
     */
    public Event poll(){
        return linkedBlockingQueue.poll();
    }

    /**
     * 清空队列中所有的元素。
     */
    public void clear(){
        this.linkedBlockingQueue.clear();
    }


    /**
     * 返回队列元素的个数
     * @return
     */
    public int size(){
        return this.linkedBlockingQueue.size();
    }


}
