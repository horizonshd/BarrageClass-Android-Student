package com.horizonshd.www.barrageclassstudent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {


    private EditText edtRegisterAccount;
    private EditText edtRegisterPwd;
    private Button btnToRegister;
    private Toolbar toolbar;

    private  String account;
    private  String password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView(){
        edtRegisterAccount = (EditText)findViewById(R.id.edt_register_account);
        edtRegisterPwd = (EditText)findViewById(R.id.edt_register_pwd);
        btnToRegister = (Button) findViewById(R.id.btn_to_register);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = edtRegisterAccount.getText().toString().trim();
                password = edtRegisterPwd.getText().toString().trim();
                new LoginTask().execute();
            }
        });
    }



    class LoginTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            account = edtRegisterAccount.getText().toString().trim();
            password = edtRegisterPwd.getText().toString().trim();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().add("account",account).add("password",password).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/student/register").post(requestBody).build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                String status = jsonObject.getString("status");
                if(status.equals("success")){
                    return true;
                }else {
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(RegisterActivity.this,"注册失败，账号已存在",Toast.LENGTH_SHORT).show();
            }
        }
    }


}
