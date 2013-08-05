package com.taobao.ju.item.eventreactor.core;


/**
   �¼����Ǹ���{@link Reactor}֮�䴫�ݵ�ʵ��
 */
public abstract class Event<T> {

    protected enum EventIdEnum{
        BASE_EVENT(1),
        BASE_EVENT2(2);

        private int id;

         EventIdEnum(int id){
            this.id = id;
        }

        public int getId(){
            return this.id;
        }
    }

    private int id;


    public Event(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected Event() {
    }



    /**
     * ��ȡ�¼��������Ķ���
     * @return �¼������������Ķ���
     */
    public abstract T getContextObject();


    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!(obj instanceof Event)){
            return false;
        }
        Event event  = (Event)obj;
        return event.getId() == this.id;
    }

    public int hashCode(){
        return this.id;
    }

    public String toString(){
        StringBuilder info  = new StringBuilder();
        info.append("event[id:").append(this.id).append("]");
        return info.toString();
    }


}
