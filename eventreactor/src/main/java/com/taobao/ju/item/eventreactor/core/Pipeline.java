package com.taobao.ju.item.eventreactor.core;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 管道，实际处理Event的Workhorse;
 */
public class Pipeline {

    private class Worker implements Runnable{

        /**
         * 事件派发器{@link EventDispatcher}
         */
        private EventDispatcher eventDispatcher;

        /**
         * 存储待处理事件{@link Event}的容器{@link EventQueue}
         */
        private EventQueue eventQueue;

        private ReentrantLock eventQueueLock = new ReentrantLock();

        private Condition eventQueueEmpty = eventQueueLock.newCondition();

        private AtomicBoolean prepareStop = new AtomicBoolean(false) ;


        /**
         * 事件处理器{@link EventHandler}
         */
        private EventHandler eventHandler;

        /**
         *
         * 关联的Pipeline
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
                while( (inputEvent = this.eventQueue.take()) != null ){ //阻塞从队列拿Event;
                    outputEvent =  this.eventHandler.handleEvent(inputEvent);//处理事件
                    if(outputEvent != null){
                        eventDispatcher.postDispatch(outputEvent); //派发给后面的Reactor处理
                    }
                    //一次循环处理完毕以后，如果队列长度为0，并且准备停止，则break出循环，不要再继续处理了。。。
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
         * 立即停掉WorkerThread
         */
        public void stopNow(){
            //直接中断工作线程，清理队列。
            workerThread.interrupt();

            eventQueueLock.lock();
            try{
                eventQueue.clear();
            }finally{
                eventQueueLock.unlock();
            }
        }

        /**
         * 等eventQueue中所有的event被处理完毕以后，再停掉WorkerThread;
         */
        public void stopAfterWorking(){

            eventQueueLock.lock();
            try{
                if(eventQueue.size() == 0){  //队列没有元素要处理了，中断线程。
                    workerThread.interrupt();
                }else{
                    prepareStop.set(true); //告知Worker线程准备Stop;

                   //队列还有元素，则先把Pipeline从Reactor的队列中移除，再等待队列为空以后，中断线程。
                  reactor.offlinePipeline(pipeline);
                    //double check
                    if(eventQueue.size() == 0){
                        workerThread.interrupt();
                    }else{
                        //等待一段时间(最长等待5分钟。)，如果还没有等到eventQueueEmpty信号，则强制退出。
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
     * 唯一标识生成器。
     */
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 事件派发器{@link EventDispatcher}
     */
   private EventDispatcher eventDispatcher;

    /**
     * 存储待处理事件{@link Event}的容器{@link EventQueue}
     */
    private EventQueue eventQueue;

    /**
     * 事件处理器{@link EventHandler}
     */
    private EventHandler eventHandler;


    /**
     * 实际干活的Runnable;
     */
    private Worker worker;

    /**
     *
     *实际干活的Thread
     */
    private Thread workerThread;

    /**
     * pipeline所在的{@link Reactor}
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
     * 启动Pipeline
     */
    public void start(){
        worker = new Worker(eventDispatcher,eventQueue,eventHandler,this);//在构造函数以后再初始化。防止未初始化的问题。
        workerThread = new Thread(worker);
        workerThread.start();
    }


    /**
     * 关闭Pipeline .如果 immediately为true; 则会丢弃eventQueue中未完成处理的Event,
     *              如果  immediately为false,则要等eventQueue中的任务处理完毕。
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
     * 把待处理的事件提交到管道。
     * @param event
     */
    public void submitEvent(Event event){
        boolean isOfferSuccess = eventQueue.offer(event);
        if(!isOfferSuccess){
            //TODO 对于提交到队列失败的Event,持久化存储，后续做重做。
        }

    }


    /**
     * 立即停止WorkerThread ,并丢弃eventQueue中未完成处理的Event,
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
