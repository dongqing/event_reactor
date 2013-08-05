package com.taobao.ju.item.eventreactor.core.xml;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**

 */
public class SimpleSaxErrorHandler implements ErrorHandler {

    Logger xmlParseLogger = Logger.getLogger("xmlParseLogger");


    @Override
    public void warning(SAXParseException e) throws SAXException {
        xmlParseLogger.warn(e);
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }
}
