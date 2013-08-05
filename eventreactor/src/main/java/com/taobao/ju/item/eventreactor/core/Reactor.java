package com.taobao.ju.item.eventreactor.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 事件反应炉.
 理解为处理事件{@link Event}的一个阶段（Stage). ，eventreactor框架由若干个Reactor组合而成。

 */
public class Reactor {

    /**
     * 默认的Pipeline的个数；
     */
    private static final int DEFAULT_PIPELINE_SIZE = 5;


    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 要处理的事件{@link Event}
     */
    private Event event;
    /**
     * 事件处理器{@link EventHandler }
     */
    private EventHandler eventHandler;
    /**
     * 事件派发器{@link EventDispatcher}
     */
    private EventDispatcher eventDispatcher;
    /**
     * 关联的{@link Pipeline} 列表 。
     * 这里采用 CopyOnWriteArrayList的目的，是因为默认的事件派发器{@link DefaultEventDispatcher} 在派发{@link Event}的
     * 时候会对{@link Pipeline}列表进行读操作。而Reactor会对Pipe列表进行写。 但是读远大于写 。
     *
     */
    private CopyOnWriteArrayList<Pipeline> copyOnWritePipelineList;

    public CopyOnWriteArrayList<Pipeline> getCopyOnWritePipelineList() {
        return copyOnWritePipelineList;
    }

    public Reactor(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * 异步处理事件
     * @param event
     */
    public void asyncHandleEvent(Event event){
         eventDispatcher.preDispatch(event);
    }


    public void start(TopologyConfig topologyConfig){
        this.start(this.eventHandler,topologyConfig);
    }

    /**
     * 启动事件反应炉
     */
    public void start(EventHandler eventHandler,TopologyConfig topologyConfig){
        List<Pipeline> pipelines = new ArrayList<Pipeline>(DEFAULT_PIPELINE_SIZE);


        //默认的事件派发器。
        DefaultEventDispatcher defaultEventDispatcher = new DefaultEventDispatcher(this,topologyConfig);
        this.eventDispatcher =  defaultEventDispatcher;

        //初始化一组管道并启动。
        for(int i = 0; i < DEFAULT_PIPELINE_SIZE; ++i){
            Pipeline pipeline = new Pipeline(eventDispatcher,new EventQueue(),eventHandler,this);
            pipelines.add(pipeline);
            pipeline.start();
        }

        //因为CopyOnWriteArrayList的写成本比较高，所以这里采用了一个普通的ArrayList来辅助。
        copyOnWritePipelineList = new CopyOnWriteArrayList<Pipeline>(pipelines);
    }

    /**
     * {@link ReactorController}根据Reactor的运行情况，对{@link Pipeline}进行
     * 动态调整，增加指定个数的Pipeline;
     * @param count  需要增加多少个Pipeline
     */
    public void addPipeline(int count){}

    /**
     * {@link ReactorController}根据Reactor的运行情况，对{@link Pipeline}进行
     * 动态调整,移除掉一个管道
     * @param pipeline 需要移除的管道
     */
    public void removePipeline(Pipeline pipeline){}


    /**
     * 下线Pipeline;
     * @param pipeline
     */
    public void offlinePipeline(Pipeline pipeline){
        copyOnWritePipelineList.remove(pipeline);
    }

}
