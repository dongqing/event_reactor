package com.taobao.ju.item.eventreactor.example;

import com.taobao.ju.item.eventreactor.core.Event;
import com.taobao.ju.item.eventreactor.core.EventHandler;

/**

 */
public class DoNothingEventHandler  implements EventHandler {
    @Override
    public Event handleEvent(Event inputEvent) {
        System.out.println("DoNothingEventHandler recv inputEvent:" + inputEvent );
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
