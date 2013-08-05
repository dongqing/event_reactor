package com.taobao.ju.item.eventreactor.core;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
  默认的事件派发器 。
 1、preDispatch方法的处理，默认采用roundrobin的处理方式，从{@link Reactor}的  {@link Reactor#copyOnWritePipelineList}管道列表中选择一个管道进行投递。

 2、postDispatch 方法的处理，读取{@link Reactor}的拓扑结构{@link TopologyConfig}，选择事件{@link Event}对应的Reactor进行异步处理。



 */
public class DefaultEventDispatcher implements EventDispatcher{

    /**
     * 事件反应炉
     */
    private Reactor reactor;

    /**
     * 存储了事件{@link Event}和对应事件反应炉控制器{@link ReactorController}
     * 映射关系的配置。
     */
    private TopologyConfig topologyConfig;

    /**
     * RR算法选择pipeline时使用的pipeline数组下标。
     */
    private int rrIndex = 0 ;

    /**
     * 生成{@link DefaultEventDispatcher#rrIndex} 时使用的可重入锁。
     */
    private ReentrantLock rrIndexLock = new ReentrantLock();




    public DefaultEventDispatcher(Reactor reactor,TopologyConfig topologyConfig){
        this.reactor = reactor;
        this.topologyConfig  = topologyConfig;
    }


    @Override
    public void preDispatch(Event event) {

        Pipeline pipeline =  null;
        CopyOnWriteArrayList<Pipeline> copyOnWriteArrayList = this.reactor.getCopyOnWritePipelineList();
        //RR算法选择Pipeline;
        rrIndexLock.lock();
        try{
            rrIndex++;
            if(rrIndex == copyOnWriteArrayList.size()){
                rrIndex = 0;
            }
            pipeline = copyOnWriteArrayList.get(rrIndex);

        }finally{
               rrIndexLock.unlock();
        }

        pipeline.submitEvent(event);

    }


    @Override
    public void postDispatch(Event event) {
        //选择event对应的Reactor提交event
        Reactor reactor =  this.topologyConfig.getEventReactorMap().get(event);
        if(reactor != null){
            reactor.asyncHandleEvent(event);
        }

    }




    /**
     * 2013.7.31 中午写的这一版在每次调用preDispatch方法的时候，都要做加锁操作。
     *  所以，后面改成了CopyOnWriteArrayList的方式，因为场景是多读少写。
     *
     * @Override
        public void preDispatch(Event event) {

        Pipeline pipeline =  null;

        pipelinesLock.lock();
        try{
        List<Pipeline> pipelines =   reactor.getPipelineList();
        rrIndexLock.lock();
        try{
        pipeline = pipelines.get(rrIndex);
        rrIndex++;
        if(rrIndex == pipelines.size()){
        rrIndex = 0;
        }
        }finally{
        rrIndexLock.unlock();
        }

        }finally{
        pipelinesLock.unlock();
        }

        pipeline.submitEvent(event);

        }

         @Override
         public void onRemove(Pipeline pipeline) {

         pipelinesLock.lock();
         try{
         List<Pipeline> pipelines =   reactor.getPipelineList();
         pipelines.remove(pipeline);

         }finally {
         pipelinesLock.unlock();
         }

         }

     */
}
