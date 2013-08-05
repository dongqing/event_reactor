package com.taobao.ju.item.eventreactor.core.xml;

import org.apache.log4j.Logger;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;

/**
  读classpath:event_reactor.xml，获取{@link com.taobao.ju.item.eventreactor.core.Reactor}的配置信息。
    代码参考了spring ioc容器读取bean配置文件的解析过程。

 */
public class XmlEventReactorConfigReader {

     private  Logger xmlParseLogger = Logger.getLogger("xmlParseLogger");


    /**
     * Indicates that the validation should be disabled.
     */
    public static final int VALIDATION_NONE = XmlValidationModeDetector.VALIDATION_NONE;

    /**
     * Indicates that the validation mode should be detected automatically.
     */
    public static final int VALIDATION_AUTO = XmlValidationModeDetector.VALIDATION_AUTO;

    /**
     * Indicates that DTD validation should be used.
     */
    public static final int VALIDATION_DTD = XmlValidationModeDetector.VALIDATION_DTD;

    /**
     * Indicates that XSD validation should be used.
     */
    public static final int VALIDATION_XSD = XmlValidationModeDetector.VALIDATION_XSD;


    private DocumentLoader documentLoader = new DefaultDocumentLoader();

    private ErrorHandler errorHandler = new SimpleSaxErrorHandler();

    private final XmlValidationModeDetector validationModeDetector = new XmlValidationModeDetector();



    private int validationMode = VALIDATION_NONE;    //检查xml文件的注释，看采用什么方式校验xml ;

    private boolean namespaceAware ;

    /**
     * Set the validation mode to use. Defaults to {@link #VALIDATION_AUTO}.
     */
    public void setValidationMode(int validationMode) {
        this.validationMode = validationMode;
    }

    /**
     * Return the validation mode to use.
     */
    public int getValidationMode() {
        return this.validationMode;
    }

    /**
     * Set whether or not the XML parser should be XML namespace aware.
     * Default is "false".
     */
    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    /**
     * Return whether or not the XML parser should be XML namespace aware.
     */
    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }


    protected int detectValidationMode(Resource resource) {
        if (resource.isOpen()) {
            throw new BeanDefinitionStoreException(
                    "Passed-in Resource [" + resource + "] contains an open stream: " +
                            "cannot determine validation mode automatically. Either pass in a Resource " +
                            "that is able to create fresh streams, or explicitly specify the validationMode " +
                            "on your XmlBeanDefinitionReader instance.");
        }

        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                    "Unable to determine validation mode for [" + resource + "]: cannot open InputStream. " +
                            "Did you attempt to load directly from a SAX InputSource without specifying the " +
                            "validationMode on your XmlBeanDefinitionReader instance?", ex);
        }

        try {
            return this.validationModeDetector.detectValidationMode(inputStream);
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("Unable to determine validation mode for [" +
                    resource + "]: an error occurred whilst reading from the InputStream.", ex);
        }
    }

    protected int getValidationModeForResource(Resource resource) {
        int validationModeToUse = getValidationMode();
        if (validationModeToUse != VALIDATION_AUTO) {
            return validationModeToUse;
        }
        int detectedMode = detectValidationMode(resource);
        if (detectedMode != VALIDATION_AUTO) {
            return detectedMode;
        }
        // Hmm, we didn't get a clear indication... Let's assume XSD,
        // since apparently no DTD declaration has been found up until
        // detection stopped (before finding the document's root tag).
        return VALIDATION_XSD;
    }




    /**
     * 从xml中解析得到{@link com.taobao.ju.item.eventreactor.core.Reactor}的定义。
     * @param document   sax的输入源。
     * @param resource  spring框架抽象的io资源。
     */
    protected void doLoadReactorsDefinitions(Document document,Resource resource){
        EventReactorParserDelegate eventReactorParserDelegate = new EventReactorParserDelegate();
        eventReactorParserDelegate.parse(document);
    }

    public void readConfig(String classpathXmlFile) throws IOException {


        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource(classpathXmlFile);
        if(!resource.exists()){
            System.err.println(classpathXmlFile + " doesn't exist..");
            return;
        }

        InputStream inputStream =  resource.getInputStream();
        try{
            int validationMode = getValidationModeForResource(resource);
            InputSource inputSource = new InputSource(inputStream);
            ResourceEntityResolver resourceEntityResolver = new ResourceEntityResolver(defaultResourceLoader) ;
             Document doc = this.documentLoader.loadDocument(inputSource,resourceEntityResolver,this.errorHandler, validationMode,
                        this.namespaceAware);
            this.doLoadReactorsDefinitions(doc,resource);
        } catch (Exception e) {
            xmlParseLogger.error(e);
            throw new FatalBeanException("create event refactor from " + classpathXmlFile + " error...");
        } finally{
            inputStream.close();
        }

    }

    public static void main(String []args) throws IOException {
        XmlEventReactorConfigReader xmlReactorConfigReader = new XmlEventReactorConfigReader();
        xmlReactorConfigReader.readConfig("classpath:event_reactor.xml");

    }

}
