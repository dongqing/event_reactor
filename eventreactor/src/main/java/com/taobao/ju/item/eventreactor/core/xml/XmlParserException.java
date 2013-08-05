package com.taobao.ju.item.eventreactor.core.xml;

/**

 */
public class XmlParserException  extends RuntimeException{

    public  XmlParserException(String msg){
        super(msg);
    }

    public XmlParserException(Exception e){
        super(e);
    }

    public XmlParserException(String msg,Exception e){
        super(msg,e);
    }

}
