package com.example.infocenter.chatdemo.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.example.infocenter.chatdemo.Entities.User;
import com.example.infocenter.chatdemo.R;

public class ChatMainMuMu extends ActionBarActivity {

    /*--------------------------------*/

    ArrayList<HashMap<String,Object>> chatList=null;
    AVIMMessage message = new AVIMMessage();
    String[] from={"image","text"};
    int[] to={R.id.chatlist_image_me,R.id.chatlist_text_me,R.id.chatlist_image_other,R.id.chatlist_text_other};
    int[] layout={R.layout.chat_me,R.layout.chat_he};
    /**
     * 这里两个布局文件使用了同一个id，测试一下是否管用
     * TT事实证明这回产生id的匹配异常！所以还是要分开。。
     *
     * userQQ用于接收Intent传递的qq号，进而用来调用数据库中的相关的联系人信息，这里先不讲
     * 先暂时使用一个头像
     */

    public final static int OTHER=1;
    public final static int ME=0;


    protected ListView chatListView=null;
    protected Button chatSendButton=null;
    protected EditText editText=null;
    protected String mywords = null;
    protected AVIMConversation avimConversation = null;

    protected MyChatAdapter adapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        /* *利用json的方法传递没有继承序列化接口的对象，这里是接受端*
        String msg = getIntent().getExtras().getString("as");
        User u = JSON.parseObject(msg,User.class);
        Toast.makeText(ChatMain.this,u.getName(),Toast.LENGTH_LONG).show();*/

        /**获取传递过来的会话conversation用以发送消息**/
        //String name = getIntent().getExtras().getString("conversation");
        // String name  = JSON.parseObject(msg,AVIMClient.class);
        OncreateConverstation();

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_main_mu_mu);
        chatList=new ArrayList<HashMap<String,Object>>();
        addTextToList("你是哪个", ME);
        addTextToList("你猜呢？\n  ^_^", OTHER);
        addTextToList("爱说不说", ME);
        addTextToList("那就不说，拜拜！", OTHER);

        chatSendButton=(Button)findViewById(R.id.chat_bottom_sendbutton);//发送消息按钮
        editText=(EditText)findViewById(R.id.chat_bottom_edittext);//消息编辑框
        chatListView=(ListView)findViewById(R.id.chat_list);//聊天记录显示界面（正中间最大哪一块）
        adapter=new MyChatAdapter(this,chatList,layout,from,to);//界面适配器??????

        chatSendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /**
                 * 这是一个发送消息的监听器，注意如果文本框中没有内容，那么getText()的返回值可能为
                 * null，这时调用toString()会有异常！所以这里必须在后面加上一个""隐式转换成String实例
                 * ，并且不能发送空消息。
                 */

                mywords=(editText.getText()+"").toString();

                if(mywords.length()==0)
                    return;
                editText.setText("");
                //发送消息到服务器
                message.setContent(mywords);
                sendmessage(mywords);
                /**
                 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
                 */
                adapter.notifyDataSetChanged();//??????
                chatListView.setSelection(chatList.size()-1);//???????

            }
        });

        chatListView.setAdapter(adapter);

    }

    private void sendmessage(final String mywords) {
        AVIMMessage message = new AVIMMessage();
        message.setContent(mywords);
        avimConversation.sendMessage(message,new AVIMConversationCallback() {
            @Override
            public void done(AVException e) {
                if (null != e) {
                    // 出错了。。。
                    e.printStackTrace();
                    Toast.makeText(ChatMainMuMu.this,"发送失败",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatMainMuMu.this,"发送成功",Toast.LENGTH_SHORT).show();
                    addTextToList(mywords,ME);
                }
            }
        });
    }

    private void OncreateConverstation() {
        final AVIMClient client = AVIMClient.getInstance("mumu");
        client.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                if (null != e) {
                    // 出错了，可能是网络问题无法连接 LeanCloud 云端，请检查网络之后重试。
                    // 此时聊天服务不可用。错误处理
                    e.printStackTrace();
                    Toast.makeText(ChatMainMuMu.this,"网络无法连接~",Toast.LENGTH_LONG).show();

                } else {
                    // 成功登录，可以开始进行聊天了（假设为 MainActivity）。
                    List<String> clientIds = new ArrayList<String>();
                    clientIds.add("yangyang");

                    // 我们给对话增加一个自定义属性 type，表示单聊还是群聊
                    // 常量定义：
                    // int ConversationType_OneOne = 0; // 两个人之间的单聊
                    // int ConversationType_Group = 1;  // 多人之间的群聊
                    Map<String, Object> attr = new HashMap<String, Object>();
                    attr.put("type", 0);
                    client.createConversation(clientIds, attr, new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation conversation, AVException e) {
                            if (null != conversation) {
                               avimConversation = conversation;
                            }
                            else{
                                Toast.makeText(ChatMainMuMu.this,"聊天连接失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    protected void addTextToList(String text, int who) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("person", who);
                map.put("image", who == ME ? R.drawable.user1 : R.drawable.user2);
                map.put("text", text);
                chatList.add(map);
            }

    private class MyChatAdapter extends BaseAdapter {

                Context context = null;
                ArrayList<HashMap<String, Object>> chatList = null;
                int[] layout;
                String[] from;
                int[] to;

                public MyChatAdapter(Context context,
                                     ArrayList<HashMap<String, Object>> chatList, int[] layout,
                                     String[] from, int[] to) {
                    super();
                    this.context = context;
                    this.chatList = chatList;
                    this.layout = layout;
                    this.from = from;
                    this.to = to;
                }

                @Override
                public int getCount() {
                    // TODO Auto-generated method stub
                    return chatList.size();
                }

                @Override
                public Object getItem(int arg0) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    // TODO Auto-generated method stub
                    return position;
                }

                class ViewHolder {
                    public ImageView imageView = null;
                    public TextView textView = null;

                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // TODO Auto-generated method stub
                    ViewHolder holder = null;
                    int who = (Integer) chatList.get(position).get("person");

                    convertView = LayoutInflater.from(context).inflate(
                            layout[who == ME ? 0 : 1], null);
                    holder = new ViewHolder();
                    holder.imageView = (ImageView) convertView.findViewById(to[who * 2 + 0]);
                    holder.textView = (TextView) convertView.findViewById(to[who * 2 + 1]);


                    System.out.println(holder);
                    System.out.println("WHYWHYWHYWHYW");
                    System.out.println(holder.imageView);
                    holder.imageView.setBackgroundResource((Integer) chatList.get(position).get(from[0]));
                    holder.textView.setText(chatList.get(position).get(from[1]).toString());
                    return convertView;
                }

            }


        }