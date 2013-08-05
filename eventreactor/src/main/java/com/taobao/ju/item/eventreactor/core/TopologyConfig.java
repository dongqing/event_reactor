package com.taobao.ju.item.eventreactor.core;

import java.util.HashMap;
import java.util.Map;

/**

 */
public class TopologyConfig {

    public Map<Event,Reactor> eventReactorMap = new HashMap<Event, Reactor>();

    public Map<Event, Reactor> getEventReactorMap() {
        return eventReactorMap;
    }
}
