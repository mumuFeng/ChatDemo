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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVObject;

import com.avos.avoscloud.FindCallback;
import com.example.infocenter.chatdemo.App;
import com.example.infocenter.chatdemo.R;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Button btTOCHAT;
    private EditText etNAME;
    private EditText etPSW;
    private Button btLOGIN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.tt();
        btTOCHAT = (Button)findViewById(R.id.btToChat);
        btLOGIN = (Button)findViewById(R.id.btLogin);
        etNAME = (EditText)findViewById(R.id.etName);
        etPSW = (EditText)findViewById(R.id.etPsw);

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
