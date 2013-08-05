package com.taobao.ju.item.eventreactor.example;

import com.taobao.ju.item.eventreactor.core.Event;

/**

 */
public class BaseEvent extends Event {

    public BaseEvent(){
        super(EventIdEnum.BASE_EVENT.getId());
    }

    @Override
    public Object getContextObject() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
