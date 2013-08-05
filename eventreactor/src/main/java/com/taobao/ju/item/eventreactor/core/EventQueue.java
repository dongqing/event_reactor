package com.taobao.ju.item.eventreactor.core;

import java.util.concurrent.LinkedBlockingQueue;

/**
 ����{@link Reactor}֮�䴫���¼�{@link Event}��ͨ����
 ͬʱҲ��{@link Reactor}�ڲ��Ĺܵ�{@link Pipeline}�洢�¼�{@link Event}��������

 */
public class EventQueue {

    private LinkedBlockingQueue<Event> linkedBlockingQueue = new LinkedBlockingQueue<Event>();

    /**
     * ��β������{@link Event}������������ˣ���һֱ������
     * @param event   ��Ҫ�������β�����¼�{@link Event}
     */
    public void put(Event event) throws InterruptedException {
        this.linkedBlockingQueue.put(event);
    }

    /**
     * ��ͷ����ȡ�¼�{@link Event}������������ˣ���һֱ������ֱ�����зǿա�
     * @return ��ͷ����ȡ�����¼�
     */
    public Event take() throws InterruptedException {
        return linkedBlockingQueue.take();

    }

    /**
     * �����β������һ���¼�{@link Event}������������ˣ��򷵻�false;
     * ���û�������ɹ������ˣ��򷵻�true;
     * @param event
     * @return  �Ƿ������β������һ���¼��ɹ���
     */
    public boolean offer(Event event){
      return   linkedBlockingQueue.offer(event);
    }


    /**
     * �Ӷ���ͷ��ȡһ���¼�{@link Event}���������Ϊ�գ�����������NULL.
     * @return    �Ӷ���ͷ��ȡ�����¼����������Ϊ�գ�����������NULL
     */
    public Event poll(){
        return linkedBlockingQueue.poll();
    }

    /**
     * ��ն��������е�Ԫ�ء�
     */
    public void clear(){
        this.linkedBlockingQueue.clear();
    }


    /**
     * ���ض���Ԫ�صĸ���
     * @return
     */
    public int size(){
        return this.linkedBlockingQueue.size();
    }


}
