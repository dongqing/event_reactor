package com.taobao.ju.item.eventreactor.core.xml;

import com.taobao.ju.item.eventreactor.core.Event;
import com.taobao.ju.item.eventreactor.core.Reactor;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 实际解析event_reactor.xml配置文件的代理
 */
public class EventReactorParserDelegate {


    private void parseReactorElement(Element reactorElement){
       String id = reactorElement.getAttribute("id");
       Reactor reactor = new Reactor();
        reactor.setId(Long.parseLong(id));

        NodeList nl = reactorElement.getChildNodes();
        Node node = null;
        for(int i = 0; i < nl.getLength(); ++i){
             node = nl.item(i);
            if(node instanceof Element){
                Element element = (Element)node;
                if(element.getTagName().equals("inputevent")){
                    Class clazz = null;
                    try {
                        clazz = Class.forName(element.getAttribute("class"));
                    } catch (ClassNotFoundException e) {
                        StringBuilder err = new StringBuilder();
                        err.append("<").append(reactorElement.getTagName()).append(" id=\"").append(id).append("/>")
                                .append(" has wrong inputevent element with class ").append(element.getAttribute("class"));
                        System.out.println(err.toString());
                        throw new XmlParserException(err.toString());
                    }


                }
            }
        }

    }

      private void parseReactorsElement(Element element){
          NodeList nl = element.getChildNodes();
          Node node = null;
          for(int  i = 0; i < nl.getLength(); ++i){
              node = nl.item(i) ;
              if(node instanceof Element){
                  Element ele = (Element)node;
                  if(ele.getTagName().equals(EventReactorsXmlContext.REACTOR_ELEMENT_NAME)){
                       parseReactorElement(ele);
                  }
              }
          }

      }


    public void parse(Document document){
        NodeList nl = document.getChildNodes();
        Node node = null;
        for(int  i = 0; i < nl.getLength(); ++i){
             node = nl.item(i) ;
            if(node instanceof Element){
                Element element = (Element)node;
                if(element.getTagName().equals(EventReactorsXmlContext.REACTORS_ELEMENT_NAME)){
                    parseReactorsElement(element);
                }
            }
        }

    }

}
