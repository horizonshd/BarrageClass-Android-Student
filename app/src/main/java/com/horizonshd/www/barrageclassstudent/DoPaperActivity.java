package com.horizonshd.www.barrageclassstudent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

public class DoPaperActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private List<Question> questionList;
    private static QuestionAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionList = (List<Question>) getIntent().getSerializableExtra("Paper");

        setContentView(R.layout.activity_dopaper);

        initView();

    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("试卷 ["+getIntent().getStringExtra("PaperName")+"]");
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QuestionAdapter(questionList,MyApplication.getAuthenticatedId(),getIntent().getStringExtra("PaperID"),getIntent().getStringExtra("CourseID"));
        recyclerView.setAdapter(adapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DoPaperActivity.this);
                builder.setTitle("提醒");
                builder.setMessage("试卷提交后将不能再次作答！");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //处理试卷
                        adapter.setSubmitted();
                        adapter.notifyDataSetChanged();
                        adapter.submit();
                        //adapter.showAnswerList();
                        fab.setVisibility(View.GONE);//不再能提交
                        dialog.dismiss();
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
        });
    }
}
