package com.taobao.ju.item.eventreactor.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 �¼���Ӧ¯.
 ���Ϊ�����¼�{@link Event}��һ���׶Σ�Stage). ��eventreactor��������ɸ�Reactor��϶��ɡ�

 */
public class Reactor {

    /**
     * Ĭ�ϵ�Pipeline�ĸ�����
     */
    private static final int DEFAULT_PIPELINE_SIZE = 5;


    /**
     * Ψһ��ʶ
     */
    private Long id;

    /**
     * Ҫ������¼�{@link Event}
     */
    private Event event;
    /**
     * �¼�������{@link EventHandler }
     */
    private EventHandler eventHandler;
    /**
     * �¼��ɷ���{@link EventDispatcher}
     */
    private EventDispatcher eventDispatcher;
    /**
     * ������{@link Pipeline} �б� ��
     * ������� CopyOnWriteArrayList��Ŀ�ģ�����ΪĬ�ϵ��¼��ɷ���{@link DefaultEventDispatcher} ���ɷ�{@link Event}��
     * ʱ����{@link Pipeline}�б���ж���������Reactor���Pipe�б����д�� ���Ƕ�Զ����д ��
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
     * �첽�����¼�
     * @param event
     */
    public void asyncHandleEvent(Event event){
         eventDispatcher.preDispatch(event);
    }


    public void start(TopologyConfig topologyConfig){
        this.start(this.eventHandler,topologyConfig);
    }

    /**
     * �����¼���Ӧ¯
     */
    public void start(EventHandler eventHandler,TopologyConfig topologyConfig){
        List<Pipeline> pipelines = new ArrayList<Pipeline>(DEFAULT_PIPELINE_SIZE);


        //Ĭ�ϵ��¼��ɷ�����
        DefaultEventDispatcher defaultEventDispatcher = new DefaultEventDispatcher(this,topologyConfig);
        this.eventDispatcher =  defaultEventDispatcher;

        //��ʼ��һ��ܵ���������
        for(int i = 0; i < DEFAULT_PIPELINE_SIZE; ++i){
            Pipeline pipeline = new Pipeline(eventDispatcher,new EventQueue(),eventHandler,this);
            pipelines.add(pipeline);
            pipeline.start();
        }

        //��ΪCopyOnWriteArrayList��д�ɱ��Ƚϸߣ��������������һ����ͨ��ArrayList��������
        copyOnWritePipelineList = new CopyOnWriteArrayList<Pipeline>(pipelines);
    }

    /**
     * {@link ReactorController}����Reactor�������������{@link Pipeline}����
     * ��̬����������ָ��������Pipeline;
     * @param count  ��Ҫ���Ӷ��ٸ�Pipeline
     */
    public void addPipeline(int count){}

    /**
     * {@link ReactorController}����Reactor�������������{@link Pipeline}����
     * ��̬����,�Ƴ���һ���ܵ�
     * @param pipeline ��Ҫ�Ƴ��Ĺܵ�
     */
    public void removePipeline(Pipeline pipeline){}


    /**
     * ����Pipeline;
     * @param pipeline
     */
    public void offlinePipeline(Pipeline pipeline){
        copyOnWritePipelineList.remove(pipeline);
    }

}
