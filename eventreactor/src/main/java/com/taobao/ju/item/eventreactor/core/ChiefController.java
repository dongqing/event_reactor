package com.taobao.ju.item.eventreactor.core;

import com.taobao.ju.item.eventreactor.example.BaseEvent;
import org.apache.commons.cli.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * 负责所有事件反应炉{@link Reactor}的控制。
 *
 */
public class ChiefController {

    private TopologyConfig topologyConfig ;


    private  void printHelp(Options options){
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar eventreactor.jar",options);
    }


    public void startEngine(String []args){
        String configFilePathOption  = "config_file_path";
        Options options = new Options();
        options.addOption(configFilePathOption,true,"the config file path"); //需要argument;    //没有默认，需要指定。

        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options,args);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        if(commandLine == null){
            System.out.println("the commandLine is null");
            return;
        }

        if(!commandLine.hasOption(configFilePathOption)){
            printHelp(options);
            return;
        }

        String configFilePath = commandLine.getOptionValue(configFilePathOption);
         //读取xml配置文件，生成event和reactor的映射关系。
        ApplicationContext ac = new ClassPathXmlApplicationContext(configFilePath);
        Map map = ac.getBeansOfType(com.taobao.ju.item.eventreactor.core.Reactor.class);
        Iterator iterator = map.values().iterator();
        Reactor reactor = null;
        this.topologyConfig = new TopologyConfig();
        while(iterator.hasNext()){
            reactor = (Reactor) iterator.next();
            this.topologyConfig.getEventReactorMap().put(reactor.getEvent(),reactor);
        }

       //启动所有的reactor.
       iterator = map.values().iterator();
        while(iterator.hasNext()){
            reactor = (Reactor) iterator.next();
            reactor.start(this.topologyConfig);
        }

        Reactor reactor1 = (Reactor) ac.getBean("reactor");
        for(int i = 0 ; i < 10; ++i){
            reactor1.asyncHandleEvent(new BaseEvent());
        }

    }




    public static void main(String []args){
        ChiefController chiefController = new ChiefController() ;
        chiefController.startEngine(args);
    }



}
