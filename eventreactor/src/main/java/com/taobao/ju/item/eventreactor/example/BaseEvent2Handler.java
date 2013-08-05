package com.taobao.ju.item.eventreactor.example;

import com.taobao.ju.item.eventreactor.core.Event;
import com.taobao.ju.item.eventreactor.core.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: dongqingswt
 * Date: 13-7-31
 * Time: обнГ5:23
 * To change this template use File | Settings | File Templates.
 */
public class BaseEvent2Handler implements EventHandler {
    @Override
    public Event handleEvent(Event inputEvent) {
        System.out.println("BaseEvent2Handler recv inputEvent:" + inputEvent );
        return null;

    }
}
