package com.horizonshd.www.barrageclassstudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {
    private Toolbar toolbar;
    private EditText edtLoginAccount;
    private EditText edtLoginPwd;
    private Button btnToLogin;
    private TextView txtToRegister;
    private CheckBox rememberPwd;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private String id;
    private String account;
    private String password;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        //记住密码功能
        boolean isRemember = pref.getBoolean("remember_pwd",false);
        if(isRemember){
            id = pref.getString("id","");
            account = pref.getString("account","");
            password = pref.getString("password","");

            edtLoginAccount.setText(account);
            edtLoginPwd.setText(password);
            rememberPwd.setChecked(true);
        }

        //登录按钮
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginTask().execute();

            }
        });

        //注册文本
        txtToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        edtLoginAccount = (EditText) findViewById(R.id.edt_login_account);
        edtLoginPwd = (EditText) findViewById(R.id.edt_login_pwd);
        btnToLogin = (Button) findViewById(R.id.btn_login_click_to_login);
        txtToRegister = (TextView) findViewById(R.id.tx_login_click_to_register);
        rememberPwd = (CheckBox) findViewById(R.id.remember_pwd);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
    }





    class LoginTask extends AsyncTask<Void,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            account = edtLoginAccount.getText().toString().trim();
            password = edtLoginPwd.getText().toString().trim();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().add("account",account).add("password",password).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/student/login").post(requestBody).build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                String status = jsonObject.getString("status");
                if(status.equals("success")){
                    return jsonObject.getString("id");
                }else {
                    return null;
                }
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String id) {
            super.onPostExecute(id);
            LogUtil.d("ID",id);
            if(id == null || id.trim().length() == 0){//id为空""
                Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
            }else {
                //////
                editor = pref.edit();
                if(rememberPwd.isChecked()){
                    editor.putBoolean("remember_password",true);
                    editor.putString("id",id);//先修改sp中保存的值
                    editor.putString("account",account);
                    editor.putString("password",password);

                }else {
                    editor.clear();
                }

                editor.apply();
                MyApplication.setAuthenticatedAccount(account);//再改变程序中变量的值
                MyApplication.setAuthenticatedId(id);

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("account",account);
                startActivity(intent);
                finish();
                //////
            }

        }
    }

}
