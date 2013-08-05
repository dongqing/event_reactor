package com.taobao.ju.item.eventreactor.core;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 �ܵ���ʵ�ʴ���Event��Workhorse;
 */
public class Pipeline {

    private class Worker implements Runnable{

        /**
         * �¼��ɷ���{@link EventDispatcher}
         */
        private EventDispatcher eventDispatcher;

        /**
         * �洢�������¼�{@link Event}������{@link EventQueue}
         */
        private EventQueue eventQueue;

        private ReentrantLock eventQueueLock = new ReentrantLock();

        private Condition eventQueueEmpty = eventQueueLock.newCondition();

        private AtomicBoolean prepareStop = new AtomicBoolean(false) ;


        /**
         * �¼�������{@link EventHandler}
         */
        private EventHandler eventHandler;

        /**
         *
         * ������Pipeline
         */
        private  Pipeline pipeline;



        public Worker(EventDispatcher eventDispatcher,EventQueue eventQueue,EventHandler eventHandler,Pipeline pipeline){
            this.eventDispatcher = eventDispatcher;
            this.eventQueue  = eventQueue;
            this.eventHandler = eventHandler;
            this.pipeline = pipeline;
        }

        public void run(){
            Event inputEvent = null,outputEvent = null;
            try{
                while( (inputEvent = this.eventQueue.take()) != null ){ //�����Ӷ�����Event;
                    outputEvent =  this.eventHandler.handleEvent(inputEvent);//�����¼�
                    if(outputEvent != null){
                        eventDispatcher.postDispatch(outputEvent); //�ɷ��������Reactor����
                    }
                    //һ��ѭ����������Ժ�������г���Ϊ0������׼��ֹͣ����break��ѭ������Ҫ�ټ��������ˡ�����
                    eventQueueLock.lock();
                    try{
                         if(eventQueue.size() == 0 && prepareStop.get()){
                             eventQueueEmpty.signalAll();
                             break;
                         }
                    }finally{
                        eventQueueLock.unlock();
                    }
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        /**
         * ����ͣ��WorkerThread
         */
        public void stopNow(){
            //ֱ���жϹ����̣߳�������С�
            workerThread.interrupt();

            eventQueueLock.lock();
            try{
                eventQueue.clear();
            }finally{
                eventQueueLock.unlock();
            }
        }

        /**
         * ��eventQueue�����е�event����������Ժ���ͣ��WorkerThread;
         */
        public void stopAfterWorking(){

            eventQueueLock.lock();
            try{
                if(eventQueue.size() == 0){  //����û��Ԫ��Ҫ�����ˣ��ж��̡߳�
                    workerThread.interrupt();
                }else{
                    prepareStop.set(true); //��֪Worker�߳�׼��Stop;

                   //���л���Ԫ�أ����Ȱ�Pipeline��Reactor�Ķ������Ƴ����ٵȴ�����Ϊ���Ժ��ж��̡߳�
                  reactor.offlinePipeline(pipeline);
                    //double check
                    if(eventQueue.size() == 0){
                        workerThread.interrupt();
                    }else{
                        //�ȴ�һ��ʱ��(��ȴ�5���ӡ�)�������û�еȵ�eventQueueEmpty�źţ���ǿ���˳���
                        try {
                            eventQueueEmpty.wait(300000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        workerThread.interrupt();

                    }
                }
            }finally{
                eventQueueLock.unlock();
            }

        }
    }


    /**
     * Ψһ��ʶ��������
     */
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    /**
     * Ψһ��ʶ
     */
    private Integer id;

    /**
     * �¼��ɷ���{@link EventDispatcher}
     */
   private EventDispatcher eventDispatcher;

    /**
     * �洢�������¼�{@link Event}������{@link EventQueue}
     */
    private EventQueue eventQueue;

    /**
     * �¼�������{@link EventHandler}
     */
    private EventHandler eventHandler;


    /**
     * ʵ�ʸɻ��Runnable;
     */
    private Worker worker;

    /**
     *
     *ʵ�ʸɻ��Thread
     */
    private Thread workerThread;

    /**
     * pipeline���ڵ�{@link Reactor}
     */
    private Reactor reactor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public EventQueue getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(EventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public Reactor getReactor() {
        return reactor;
    }

    public void setReactor(Reactor reactor) {
        this.reactor = reactor;
    }

    public Pipeline(EventDispatcher eventDispatcher,EventQueue eventQueue,EventHandler eventHandler){

        this.eventDispatcher = eventDispatcher;
        this.eventQueue  = eventQueue;
        this.eventHandler = eventHandler;

        this.id = idGenerator.getAndIncrement();

    }


    public Pipeline(EventDispatcher eventDispatcher,EventQueue eventQueue,EventHandler eventHandler,Reactor reactor){

        this.reactor = reactor;

        this.eventDispatcher = eventDispatcher;
        this.eventQueue  = eventQueue;
        this.eventHandler = eventHandler;

        this.id = idGenerator.getAndIncrement();

    }

    public Pipeline(){}


    /**
     * ����Pipeline
     */
    public void start(){
        worker = new Worker(eventDispatcher,eventQueue,eventHandler,this);//�ڹ��캯���Ժ��ٳ�ʼ������ֹδ��ʼ�������⡣
        workerThread = new Thread(worker);
        workerThread.start();
    }


    /**
     * �ر�Pipeline .��� immediatelyΪtrue; ��ᶪ��eventQueue��δ��ɴ����Event,
     *              ���  immediatelyΪfalse,��Ҫ��eventQueue�е���������ϡ�
     * @param immediately
     */
    public void stop(boolean immediately){
        if(immediately){
            this.stopImmediately();
        }else{
            this.stopAfterWorking();
        }
    }

    /**
     * �Ѵ�������¼��ύ���ܵ���
     * @param event
     */
    public void submitEvent(Event event){
        boolean isOfferSuccess = eventQueue.offer(event);
        if(!isOfferSuccess){
            //TODO �����ύ������ʧ�ܵ�Event,�־û��洢��������������
        }

    }


    /**
     * ����ֹͣWorkerThread ,������eventQueue��δ��ɴ����Event,
     */
    private void stopImmediately(){
        worker.stopNow();
    }

    private void stopAfterWorking(){
        worker.stopAfterWorking();
    }


    public boolean equals(Object object){
        if(object == null){
            return false;
        }
        if(!(object instanceof Pipeline)){
            return false;
        }

        Pipeline pipeline = (Pipeline)object;
        return this.id != null && this.id.equals(pipeline.getId());


    }

    public int hashCode(){
        return this.id.hashCode();
    }


}
