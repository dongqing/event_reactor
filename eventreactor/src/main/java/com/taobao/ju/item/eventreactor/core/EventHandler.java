package com.taobao.ju.item.eventreactor.core;


/**
 * �¼�������
 * ҵ����Ҫ�ṩ����ӿڵ�ʵ��
 */
public interface EventHandler {

    /**
     * ����һ���¼����õ�һ������¼�
     * @param inputEvent ������������¼�
     * @return  ������һ��{@link Reactor}��Ҫ������¼������ΪNULL,���ʾ�����˽��� ��
     */
    public Event handleEvent(Event inputEvent);

}
