package com.horizonshd.www.barrageclassstudent;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_NAME = "course_name";

    private static Context mContext;
    private List<Course> mCourseList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView courseName;
        Button btnIn;

        public ViewHolder(View view){
            super(view);
            courseName = (TextView) view.findViewById(R.id.course_name);
            btnIn = (Button) view.findViewById(R.id.btn_in);
        }
    }

    // 构造函数
    CourseAdapter(List<Course> courseList){
        mCourseList = courseList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item,parent,false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Course course = mCourseList.get(position);

        holder.courseName.setText(course.getCoursename());

        if(course.getIsactive()){//在线（已经激活的）课堂，可以进入

            holder.btnIn.setOnClickListener(new View.OnClickListener() {//进入课堂--点击事件
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext,ClassroomActivity.class);
                    intent.putExtra(ClassroomActivity.COURSE_ID,course.getCourseid());
                    intent.putExtra(ClassroomActivity.COURSE_NAME,course.getCoursename());
                    mContext.startActivity(intent);

                }
            });

        }

    }


    public int getItemCount() {
        return mCourseList.size();
    }






}
