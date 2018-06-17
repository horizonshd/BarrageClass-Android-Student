/**
 * 弹幕用的消息类
 */
package com.horizonshd.www.barrageclassstudent;

public class Message {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    public static final int TYPE_MENTION = 2;
    private String from;
    private int type;
    private String content;

    public Message(String from,int type,String content){
        this.from = from;
        this.type = type;
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public int getType(){
        return type;
    }

    public String getContent() {
        return content;
    }


}
