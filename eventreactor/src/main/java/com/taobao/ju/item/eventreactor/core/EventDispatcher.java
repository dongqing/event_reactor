package com.taobao.ju.item.eventreactor.core;

/**
  事件分发器。
  被事件反应炉{@link Reactor} 用来做两件事情。
 1、分派给一个{@link Pipeline}进行处理。
 2、Pipeline处理结束以后，分发给下一个{@link Reactor}处理。

这个类可以理解为{@link Reactor}的事件输入和事件输出阀门。

 */
public interface EventDispatcher {

    /**
     * 把事件{@link Event}分发给一个{@link Pipeline}进行处理
     * @param event 需要分发的事件
     */
   public void preDispatch(Event event);

    /**
     * 把事件{@link Event}分发给下一个{@link Reactor}处理。
     * 什么都不做，则表示不需要下一个Reactor处理。
     * @param event
     */
   public void postDispatch(Event event);



}
