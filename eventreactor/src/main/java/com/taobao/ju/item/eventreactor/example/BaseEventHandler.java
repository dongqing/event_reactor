package com.taobao.ju.item.eventreactor.example;

import com.taobao.ju.item.eventreactor.core.Event;
import com.taobao.ju.item.eventreactor.core.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: dongqingswt
 * Date: 13-7-31
 * Time: обнГ5:19
 * To change this template use File | Settings | File Templates.
 */
public class BaseEventHandler implements EventHandler {
    @Override
    public Event handleEvent(Event inputEvent) {
        System.out.println("BaseEventHandler recv inputEvent:" + inputEvent );
        return new BaseEvent2() ;

    }
}
