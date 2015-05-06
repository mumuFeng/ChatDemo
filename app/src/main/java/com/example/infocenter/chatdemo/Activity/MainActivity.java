package com.example.infocenter.chatdemo.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.example.infocenter.chatdemo.App;
import com.example.infocenter.chatdemo.Entities.User;
import com.example.infocenter.chatdemo.Helper.MessageHandler;
import com.example.infocenter.chatdemo.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private Button btTOCHAT;
    private EditText etNAME;
    private EditText etPSW;
    private Button btLOGIN;
    private Button btMuChat;
    private Button btLChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.tt();
        btTOCHAT = (Button)findViewById(R.id.btToChat);
        btLOGIN = (Button)findViewById(R.id.btLogin);
        etNAME = (EditText)findViewById(R.id.etName);
        etPSW = (EditText)findViewById(R.id.etPsw);
        btMuChat = (Button)findViewById(R.id.MToL);
        btLChat = (Button)findViewById(R.id.LToM);

        btTOCHAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ChatMain.class);
                startActivity(intent);
            }
        });
        /*登陆*/
        btLOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etNAME.getText().toString();
                String psw = etPSW.getText().toString();

                /*处理登陆*/
                AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
                query.whereEqualTo("username", name);
                query.findInBackground(new FindCallback<AVObject>() {
                    public void done(List<AVObject> avObjects, AVException e) {
                        if (e == null&&(avObjects.size())>0) {//登陆成功
                            Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                            Toast.makeText(MainActivity.this,"登陆成功",Toast.LENGTH_LONG).show();
                          //  Intent intentUserList = new Intent();
                        } else {
                            Log.d("失败", "查询错误: " + e.getMessage());
                            Toast.makeText(MainActivity.this,"用户不存在",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        /*我是mumu,这里进入我的聊天界面，我去找ljj聊天*/
        btMuChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*登陆对话初始化*/
                final AVIMClient imClient = AVIMClient.getInstance("mumu");
                imClient.open(new AVIMClientCallback() {
                    @Override
                    public void done(final AVIMClient avimClient, AVException e) {
                        if (null != e) {
                            // 出错了，可能是网络问题无法连接 LeanCloud 云端，请检查网络之后重试。
                            // 此时聊天服务不可用。
                            e.printStackTrace();
                        } else {
                            // 成功登录，可以开始进行聊天了（假设为 MainActivity）。
                            List<String> clientIds = new ArrayList<String>();
                            clientIds.add("mumu");
                            clientIds.add("ljj");
                            // 我们给对话增加一个自定义属性 type，表示单聊还是群聊
                            // 常量定义：
                            // int ConversationType_OneOne = 0; // 两个人之间的单聊
                            // int ConversationType_Group = 1;  // 多人之间的群聊
                            Map<String, Object> attr = new HashMap<String, Object>();
                            attr.put("type", 0);

                            imClient.createConversation(clientIds, attr, new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation conversation, AVException e) {
                                    if (null != conversation) {
                                        // 成功了，这时候可以显示对话的 Activity 页面（假定为 ChatActivity）了。

                                         //发送消息
                                        final AVIMMessage message = new AVIMMessage();
                                        message.setContent("hello");
                                        String msgid = message.getMessageId();
                                        conversation.sendMessage(message, new AVIMConversationCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (null != e) {
                                                    // 出错了。。。
                                                    e.printStackTrace();
                                                } else {
                                                    Log.d("success","发送成功，msgId=" + message.getMessageId());
                                                }
                                            }
                                        });
                                        Log.d("success",message.getContent());
                                        avimClient.close(new AVIMClientCallback() {
                                            @Override
                                            public void done(AVIMClient avimClient, AVException e) {
                                            }
                                        });
                                        //接受消息


                                        Intent intent = new Intent(MainActivity.this, ChatMain.class);
                                        intent.putExtra("conversation", JSON.toJSONString(conversation));

                            /*       ******利用json的方法传递没有继承序列化接口的对象，这里的发送端
                                        User u =new User();
                                        u.setName("mumu");
                                        u.setPsw("123");
                                        intent.putExtra("as",JSON.toJSONString(u));*/

                                        startActivity(intent);
                                    }
                                }
                            });


                        };
                    }
                });
/*
                List<String> clientIds = new ArrayList<String>();
                clientIds.add("Tom");
                clientIds.add("Bob");
                // 我们给对话增加一个自定义属性 type，表示单聊还是群聊
                // 常量定义：
                // int ConversationType_OneOne = 0; // 两个人之间的单聊
                // int ConversationType_Group = 1;  // 多人之间的群聊
                Map<String, Object> attr = new HashMap<String, Object>();
                attr.put("type", 0);

                imClient.createConversation(clientIds, attr, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation conversation, AVException e) {
                        if (null != conversation) {
                            // 成功了，这时候可以显示对话的 Activity 页面（假定为 ChatActivity）了。
                            Intent intent = new Intent(MainActivity.this, ChatMain.class);
                            Intent.putExtra("conversation", conversation);
                            startActivity(intent);
                        }
                    }
                });*/
            }
        });

        /*我是ljj,这里是我的聊天界面，我去找木木聊天*/
        btLChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //接收mumu发送过来的消息
                final AVIMClient imClientL = AVIMClient.getInstance("ljj");
                imClientL.open(new AVIMClientCallback(){
                    @Override
                    public void done(AVIMClient client, AVException e) {
                        if (null != e) {
                            // 出错了，可能是网络问题无法连接 LeanCloud 云端，请检查网络之后重试。
                            // 此时聊天服务不可用。
                            e.printStackTrace();
                        } else {
                            // 成功登录，可以开始进行聊天了。
                            Toast.makeText(MainActivity.this,"我是jj登陆成功",Toast.LENGTH_SHORT).show();
                        };
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
