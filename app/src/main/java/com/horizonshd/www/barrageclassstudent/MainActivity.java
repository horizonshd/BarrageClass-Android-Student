package com.horizonshd.www.barrageclassstudent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView navView;
    private View HeaderViewInNavView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
//    private FloatingActionButton fab;

    private static CourseAdapter adapter;
    private static List<Course> courseList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1.连接服务器的socket
        MyApplication.createSocket(MyApplication.getServerURL());
        MyApplication.getSocket().on("forceoffline",onForceOffLine);

        //2.发送"student_login"事件
        MyApplication.getSocket().emit("student_login",MyApplication.getAuthenticatedAccount());


        initView();
        initToolbar();
        initNavigationView();
        //initFAB();

        Intent intent = getIntent();
        String account = intent.getStringExtra("account");
        TextView txtViewInHeader = (TextView) HeaderViewInNavView.findViewById(R.id.account);
        txtViewInHeader.setText(account);

        new getActiveCourseListTask().execute();
    }

    private void initView(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        HeaderViewInNavView = navView.getHeaderView(0);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getActiveCourseListTask().execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CourseAdapter(courseList);//空的
        recyclerView.setAdapter(adapter);
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        }
    }

    private void initNavigationView(){
        navView.setCheckedItem(R.id.nav_change);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_change:
                        //切换账号
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("提醒");
                        builder.setMessage("你将会退出当前账号，并重新登录！");
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCollector.finishAll();//销毁所有活动
                                Intent intent1 = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(intent1);//转到登录界面
                            }
                        });
                        builder.show();
                        break;
                    case R.id.nav_logout:
                        //退出应用
                        item.setChecked(true);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        ActivityCollector.finishAll();
                        break;
                    default:
                }

                return true;
            }
        });
    }


//    private void initFAB(){
//        //fab按钮的点击事件
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v,"Data deleted", Snackbar.LENGTH_SHORT).setAction("Undo", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(MainActivity.this,"Data restored",Toast.LENGTH_SHORT).show();
//                    }
//                }).show();
//            }
//        });
//    }



    //toolbar中添加Action按钮
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    //toolbar中菜单选项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.backup:
                Toast.makeText(this,"You clicked Backup",Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this,"You clicked Delete",Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this,"You clicked Settings",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        new getActiveCourseListTask().execute();
    }

    //获取在线的课堂并在MainActivity中显示
    static class getActiveCourseListTask extends AsyncTask<Void,Course,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            courseList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/student/get-activecourselist").post(requestBody).addHeader("Connection", "close").build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                //LogUtil.d("RES",responseData);

                JSONArray jsonArray = new JSONArray(responseData);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Course course = new Course(jsonObject.getString("_id"),jsonObject.getString("coursename"),jsonObject.getBoolean("isactive"));
                    publishProgress(course);
                }

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Course... values) {
            super.onProgressUpdate(values);
            courseList.add(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }


    private Emitter.Listener onForceOffLine =  new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //发送广播
            Intent intent1 = new Intent("com.horizonshd.www.barrageclassstudent.FORCE_OFFLINE");
            sendBroadcast(intent1);
        }
    };

}
