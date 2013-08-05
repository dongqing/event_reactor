package com.taobao.ju.item.eventreactor.core;


/**
 * 事件处理器
 * 业务方需要提供这个接口的实现
 */
public interface EventHandler {

    /**
     * 处理一个事件，得到一个输出事件
     * @param inputEvent 被处理的输入事件
     * @return  返回下一个{@link Reactor}需要处理的事件。如果为NULL,则表示处理到此结束 。
     */
    public Event handleEvent(Event inputEvent);

}
