package com.taobao.ju.item.eventreactor.core;

/**
  事件反应炉{@link Reactor}的控制器
 */
public class ReactorController {

    private ReactorRundataCollector reactorRundataCollector;

    private Reactor reactor;

    public ReactorRundataCollector getReactorRundataCollector() {
        return reactorRundataCollector;
    }

    public void setReactorRundataCollector(ReactorRundataCollector reactorRundataCollector) {
        this.reactorRundataCollector = reactorRundataCollector;
    }

    public Reactor getReactor() {
        return reactor;
    }

    public void setReactor(Reactor reactor) {
        this.reactor = reactor;
    }

    public void start(){}

    public void onUpdate(ReactorRundata reactorRundata){}

    private void startReactor(){}

    private void startReactorRundataCollector(){}


}
