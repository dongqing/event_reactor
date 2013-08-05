package com.taobao.ju.item.eventreactor.example;

import com.taobao.ju.item.eventreactor.core.Event;

/**

 */
public class BaseEvent2 extends Event {

    public BaseEvent2(){
        super(EventIdEnum.BASE_EVENT2.getId());
    }

    @Override
    public Object getContextObject() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
