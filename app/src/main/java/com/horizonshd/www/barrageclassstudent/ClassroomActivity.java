package com.horizonshd.www.barrageclassstudent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ClassroomActivity extends BaseActivity {
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_NAME = "course_name";

    private List<Message> messageList = new ArrayList<>();

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText editInput;
    private Button btnSend;
    private MessageAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        //1.注册socket监听的事件
        MyApplication.getSocket().on("send-message",onMessage);
        MyApplication.getSocket().on("mention",onMention);
        MyApplication.getSocket().on("dismiss",onDismiss);
        MyApplication.getSocket().on("distribute_paper",onDistributePaper);

        //2.初始化Activity控件
        initView();

        //3.发送消息
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editInput.getText().toString().trim();
                if(content == null || content.trim().length() == 0){

                }else {
                    try {
                        MyApplication.getSocket().emit("send-message",content);
                        editInput.setText("");
                        messageList.add(new Message(MyApplication.getAuthenticatedAccount(),Message.TYPE_SENT,content));
                        adapter.notifyItemInserted(messageList.size()-1);
                        //adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size()-1);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        //4.发送"student_getin"事件
        MyApplication.getSocket().emit("student_getin",getIntent().getStringExtra(COURSE_NAME));


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //1.撤销socket上的相关监听事件
        MyApplication.getSocket().off("send-message",onMessage);
        MyApplication.getSocket().off("mention",onMention);
        MyApplication.getSocket().off("dismiss",onDismiss);
        MyApplication.getSocket().off("distribute_paper",onDistributePaper);
    }

    //toolbar中添加Action按钮
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_classroom,menu);
        return true;
    }

    //toolbar中菜单选项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.list:
                MyApplication.getSocket().emit("list", new Ack() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    JSONArray jsonArray = new JSONArray(args[0].toString());
                                    String items[] = new String[jsonArray.length()];
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        items[i] = jsonObject.getString("name");
                                        LogUtil.d("List",jsonObject.getString("name"));
                                    }
                                    AlertDialog dialog = new AlertDialog.Builder(ClassroomActivity.this)
                                            .setTitle("在线列表")//设置对话框的标题
                                            .setItems(items, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).setCancelable(false)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create();
                                    dialog.show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.dopaper:
//                Intent intent = new Intent(this,DoPaperActivity.class);
//                startActivity(intent);
                break;
            //离开教室
            case R.id.out:
                //1.弹对话框提示
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提醒");
                builder.setMessage("你将离开教室！");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //发送"student_getout"事件
                        MyApplication.getSocket().emit("student_getout");
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;

            default:
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //1.弹对话框提示
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒");
        builder.setMessage("你将离开教室！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //发送"student_getout"事件
                MyApplication.getSocket().emit("student_getout");
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("课堂 ["+getIntent().getStringExtra(COURSE_NAME)+"]");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        editInput = (EditText) findViewById(R.id.edt_input);
        btnSend = (Button) findViewById(R.id.btn_send);

        setSupportActionBar(toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

    }

    //"message" 事件处理
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        Message message = new Message(data.getString("from"),Message.TYPE_RECEIVED,data.getString("text"));
                        messageList.add(message);
                        adapter.notifyItemInserted(messageList.size()-1);
                        recyclerView.scrollToPosition(messageList.size()-1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }//call
    };//onMessage

    //"mention" 事件处理
    private Emitter.Listener onMention = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Message message = new Message("SERVER",Message.TYPE_MENTION,args[0].toString());
                        messageList.add(message);
                        adapter.notifyItemInserted(messageList.size()-1);
                        recyclerView.scrollToPosition(messageList.size()-1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };//onMention

    //"dismiss" 事件处理
    private Emitter.Listener onDismiss = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //1.弹对话框提示
            //Looper.prepare();
            //Looper.loop();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ClassroomActivity.this);
                    builder.setTitle("提醒");
                    builder.setMessage("教师已离开教室，你将退出教室！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            });

        }//call
    };//onDismiss

    //"distribute_paper" 事件处理
    private Emitter.Listener onDistributePaper = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray jsonArray = new JSONArray(args[0].toString());
                        List<Question> questionList = new ArrayList<>();

                        //LogUtil.d("LENGTH",String.valueOf(jsonArray.length()));
                        for(int i=0;i<jsonArray.length()-1;i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Question question = new Question(jsonObject.getString("questionid"),jsonObject.getString("description"),jsonObject.getString("optiona"),jsonObject.getString("optionb"),jsonObject.getString("optionc"),jsonObject.getString("optiond"),jsonObject.getString("answer"));
                            questionList.add(question);
                        }
                        Intent intent = new Intent(ClassroomActivity.this,DoPaperActivity.class);
                        intent.putExtra("Paper",(Serializable) questionList);
                        intent.putExtra("PaperName",jsonArray.getJSONObject(jsonArray.length()-1).getString("papername"));
                        intent.putExtra("PaperID",jsonArray.getJSONObject(jsonArray.length()-1).getString("paperid"));
                        intent.putExtra("CourseID",getIntent().getStringExtra(COURSE_ID));
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };//onDistributePaper




}
