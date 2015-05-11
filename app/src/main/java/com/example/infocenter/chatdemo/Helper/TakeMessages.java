package com.example.infocenter.chatdemo.Helper;

/**
 * Created by Infocenter on 2015/5/11.
 */
public class TakeMessages {
    private String message = "这里是接收到的消息";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private static  TakeMessages takeMessages = null;
    private TakeMessages(){}
    public static TakeMessages getInstance(){
        if(takeMessages == null){
            takeMessages = new TakeMessages();
        }
        return takeMessages;
    }
}
