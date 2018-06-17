package com.horizonshd.www.barrageclassstudent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private  ForceOfflineReceiver receiver;//强制下线的广播接收器
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCollector.addActivity(this);
        LogUtil.d("BaseActivity",getClass().getSimpleName());
    }

    @Override
    protected void onPostResume() {
        //当Activity已经准备好和用户进行交互时，注册广播接收器
        super.onPostResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.horizonshd.www.barrageclassstudent.FORCE_OFFLINE");
        receiver = new ForceOfflineReceiver();
        registerReceiver(receiver,intentFilter);
    }

    @Override
    protected void onPause() {
        // 当Activity失去与用户交互时，取消广播接收器
        super.onPause();
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ActivityCollector.removeActivity(this);
    }

    class ForceOfflineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提醒");
            builder.setMessage("账号在别处登录，你将被强制下线！");
            builder.setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCollector.finishAll();//销毁所有活动
                    Intent intent1 = new Intent(context,LoginActivity.class);
                    context.startActivity(intent1);//转到登录界面
                }
            });
            builder.show();

        }
    }
}
