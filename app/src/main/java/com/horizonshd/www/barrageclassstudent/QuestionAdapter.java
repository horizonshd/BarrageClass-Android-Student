package com.horizonshd.www.barrageclassstudent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private Context mContext;
    private List<Question> mQuestionList;


    private String studentID;
    private String paperID;
    private String courseID;
    private String[] answerSubmitList;
    private int[] checkedID;
    JSONArray jsonArray = new JSONArray();

    private boolean submitted = false;

    QuestionAdapter(List<Question> questionList,String _studentID,String _paperID,String _couresID){
        mQuestionList = questionList;
        studentID = _studentID;
        paperID = _paperID;
        courseID = _couresID;

        answerSubmitList = new String[questionList.size()];
        checkedID = new int[questionList.size()];
        //LogUtil.d("S",String.valueOf(questionList.size()));
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_description;
        RadioGroup radioGroup;
        RadioButton radioButton_a;
        RadioButton radioButton_b;
        RadioButton radioButton_c;
        RadioButton radioButton_d;
        TextView txt_answer;
        String selectedAnswer;
        public ViewHolder(View view){
            super(view);
            txt_description = (TextView) view.findViewById(R.id.txt_description);
            radioGroup = view.findViewById(R.id.radio_group);
            radioButton_a = view.findViewById(R.id.rbtn_a);
            radioButton_b = view.findViewById(R.id.rbtn_b);
            radioButton_c = view.findViewById(R.id.rbtn_c);
            radioButton_d = view.findViewById(R.id.rbtn_d);
            txt_answer = (TextView) view.findViewById(R.id.txt_answer);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Question question = mQuestionList.get(position);
        holder.txt_description.setText(question.getDescription());
        holder.radioButton_a.setText("A."+question.getOptiona());
        holder.radioButton_b.setText("B."+question.getOptionb());
        holder.radioButton_c.setText("C."+question.getOptionc());
        holder.radioButton_d.setText("D."+question.getOptiond());

        if(submitted){
            //holder.radioGroup.check(checkedID[position]);
            holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                }
            });
            for(int i =0;i<holder.radioGroup.getChildCount();i++){
                if(((RadioButton)holder.radioGroup.getChildAt(i)).getId() == checkedID[position]){
                    //LogUtil.d("xxx-po",String.valueOf(position)+"----"+String.valueOf(checkedID[position]));
                    ((RadioButton)holder.radioGroup.getChildAt(i)).setChecked(true);
                }
            }
            disableRadioGroup(holder.radioGroup);
            if(answerSubmitList[position] != null && answerSubmitList[position].equals(question.getAnswer())){
                holder.txt_answer.setText("回答正确：【"+question.getAnswer()+"】");
            }else {
                holder.txt_answer.setText("回答错误：正确答案为【"+question.getAnswer()+"】");
            }

            //LogUtil.d("xxx-p",String.valueOf(position)+answerSubmitList[position]);

        }else{
            holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    checkedID[position] = checkedId;
                    if(holder.radioButton_a.getId() == checkedId){
                        answerSubmitList[position] = "A";
                        //LogUtil.d("xxx-x",String.valueOf(position)+"A");
                    }else if(holder.radioButton_b.getId() == checkedId){
                        answerSubmitList[position] = "B";
                        //LogUtil.d("xxx-x",String.valueOf(position)+"B");
                    }else if(holder.radioButton_c.getId() == checkedId){
                        answerSubmitList[position] = "C";
                        //LogUtil.d("xxx-x",String.valueOf(position)+"C");
                    }else if(holder.radioButton_d.getId() == checkedId){
                        answerSubmitList[position] = "D";
                        //LogUtil.d("xxx-x",String.valueOf(position)+"D");
                    }
                }
            });
        }


//        if (submitted){
//            LogUtil.d("xxx-p",String.valueOf(position));
//            disableRadioGroup(holder.radioGroup);
//
//            if(holder.radioButton_a.isChecked()){
//                holder.selectedAnswer = "A";
//                answerSubmitList[position] = "A";
//            }else if(holder.radioButton_b.isChecked()){
//                holder.selectedAnswer = "B";
//                answerSubmitList[position] = "B";
//            }else if(holder.radioButton_c.isChecked()){
//                holder.selectedAnswer = "C";
//                answerSubmitList[position] = "C";
//            }else if(holder.radioButton_d.isChecked()){
//                holder.selectedAnswer = "D";
//                answerSubmitList[position] = "D";
//            }
//
//            if(holder.selectedAnswer!=null && holder.selectedAnswer.equals(question.getAnswer())){
//                holder.txt_answer.setText("回答正确：【"+question.getAnswer()+"】");
//            }else {
//                holder.txt_answer.setText("回答错误：正确答案为【"+question.getAnswer()+"】");
//            }
//
//            if(position == mQuestionList.size()-1){
//                try{
//                    for(int i=0;i<answerSubmitList.length;i++){
//                        //JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        LogUtil.d("xxx",String.valueOf(i)+answerSubmitList[i]);
//                        jsonArray.put(i,answerSubmitList[i]);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                MyApplication.getSocket().emit("submit_paper",jsonArray,studentID,paperID,courseID);
//            }
//        }
    }



    @Override
    public int getItemCount() {
        return mQuestionList.size();
    }


    private void disableRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setClickable(false);
        }
    }

    public void setSubmitted(){
        submitted = true;
    }

    public void showAnswerList(){
        for(int i=0;i<mQuestionList.size();i++){
            LogUtil.d("xxx-y","["+String.valueOf(i)+"]="+answerSubmitList[i]+"----"+String.valueOf(checkedID[i]));
        }
    }

    public void submit(){
        try{
            for(int i=0;i<answerSubmitList.length;i++){
                //LogUtil.d("xxx",String.valueOf(i)+answerSubmitList[i]);
                jsonArray.put(i,answerSubmitList[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        MyApplication.getSocket().emit("submit_paper",jsonArray,studentID,paperID,courseID);
    }

}
