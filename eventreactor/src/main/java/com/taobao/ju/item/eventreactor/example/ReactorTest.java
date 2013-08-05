package com.taobao.ju.item.eventreactor.example;

import com.taobao.ju.item.eventreactor.core.Reactor;
import com.taobao.ju.item.eventreactor.core.TopologyConfig;
import junit.framework.TestCase;
import org.junit.Test;

/**

 */
public class ReactorTest extends TestCase{

    private Reactor reactor;

    public void setUp(){
        reactor = new Reactor();
    }

    @Test
    public void test(){
       reactor.start(new DoNothingEventHandler(),new TopologyConfig());
       for(int i = 0; i < 10; ++i){
           reactor.asyncHandleEvent(new BaseEvent());
       }

        try {
            Thread.currentThread().sleep(30000000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
