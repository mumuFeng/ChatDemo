package com.example.infocenter.chatdemo;

import android.app.Application;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.example.infocenter.chatdemo.Activity.MainActivity;

/**
 * Created by Infocenter on 2015/4/30.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.print("123");
        AVOSCloud.initialize(this, "y91t28zzeqf035ugwjbr910pandf48dso3rn2aoesj0jq0gt", "myelg4szmkpr6azwg68cyw2txsgjth05x5otcd5jzgwz2nzo");
        AVObject testObject = new AVObject("TestObject");
        testObject.put("tttttttttt", "bttttttttttttttttar");
        testObject.saveInBackground();
    }

    public static boolean tt(){
        return true;
    }
}
