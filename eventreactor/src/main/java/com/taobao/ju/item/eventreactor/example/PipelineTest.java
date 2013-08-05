package com.taobao.ju.item.eventreactor.example;

import com.taobao.ju.item.eventreactor.core.*;
import junit.framework.TestCase;
import org.junit.Test;

/**
 */

public class PipelineTest extends TestCase {

    private Pipeline pipeline;


    public void setUp(){
         pipeline = new Pipeline(new DefaultEventDispatcher(new Reactor()
         ,new TopologyConfig()),new EventQueue(),new DoNothingEventHandler());

    }

    @Test
    public void testStop(){
       Thread t =  new Thread(new Runnable() {
            @Override
            public void run() {
                pipeline.start();
                pipeline.stop(true);
            }
        });
        t.start();
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
