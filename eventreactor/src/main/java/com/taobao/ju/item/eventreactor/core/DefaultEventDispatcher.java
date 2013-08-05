package com.taobao.ju.item.eventreactor.core;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
  Ĭ�ϵ��¼��ɷ��� ��
 1��preDispatch�����Ĵ���Ĭ�ϲ���roundrobin�Ĵ���ʽ����{@link Reactor}��  {@link Reactor#copyOnWritePipelineList}�ܵ��б���ѡ��һ���ܵ�����Ͷ�ݡ�

 2��postDispatch �����Ĵ�����ȡ{@link Reactor}�����˽ṹ{@link TopologyConfig}��ѡ���¼�{@link Event}��Ӧ��Reactor�����첽����



 */
public class DefaultEventDispatcher implements EventDispatcher{

    /**
     * �¼���Ӧ¯
     */
    private Reactor reactor;

    /**
     * �洢���¼�{@link Event}�Ͷ�Ӧ�¼���Ӧ¯������{@link ReactorController}
     * ӳ���ϵ�����á�
     */
    private TopologyConfig topologyConfig;

    /**
     * RR�㷨ѡ��pipelineʱʹ�õ�pipeline�����±ꡣ
     */
    private int rrIndex = 0 ;

    /**
     * ����{@link DefaultEventDispatcher#rrIndex} ʱʹ�õĿ���������
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
        //RR�㷨ѡ��Pipeline;
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
        //ѡ��event��Ӧ��Reactor�ύevent
        Reactor reactor =  this.topologyConfig.getEventReactorMap().get(event);
        if(reactor != null){
            reactor.asyncHandleEvent(event);
        }

    }




    /**
     * 2013.7.31 ����д����һ����ÿ�ε���preDispatch������ʱ�򣬶�Ҫ������������
     *  ���ԣ�����ĳ���CopyOnWriteArrayList�ķ�ʽ����Ϊ�����Ƕ����д��
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
