package com.taobao.ju.item.eventreactor.core;

/**
  �¼��ַ�����
  ���¼���Ӧ¯{@link Reactor} �������������顣
 1�����ɸ�һ��{@link Pipeline}���д���
 2��Pipeline��������Ժ󣬷ַ�����һ��{@link Reactor}����

�����������Ϊ{@link Reactor}���¼�������¼�������š�

 */
public interface EventDispatcher {

    /**
     * ���¼�{@link Event}�ַ���һ��{@link Pipeline}���д���
     * @param event ��Ҫ�ַ����¼�
     */
   public void preDispatch(Event event);

    /**
     * ���¼�{@link Event}�ַ�����һ��{@link Reactor}����
     * ʲô�����������ʾ����Ҫ��һ��Reactor����
     * @param event
     */
   public void postDispatch(Event event);



}
