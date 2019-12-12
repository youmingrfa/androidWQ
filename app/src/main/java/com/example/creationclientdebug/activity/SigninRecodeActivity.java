package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.loginregiste.R;
import com.henu.entity.Signin;
import com.henu.poxy.SigninServicePoxy;
import com.henu.service.SigninService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SigninRecodeActivity extends AppCompatActivity {

    public static void startActivity(Context c,int userId,int groupId){
        Intent i = new Intent(c,SigninRecodeActivity.class);
        i.putExtra("userid",userId);
        i.putExtra("groupid",groupId);
        c.startActivity(i);
    }

    private int userId;

    private int groupId;

    private List<Signin> signins = new ArrayList<>();

    private ListView lvSignin;

    private ArrayAdapter<String> lvAdapter;

    private List<String> signinStr = new ArrayList<>();

    private MyLvItemListener lvListener = new MyLvItemListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_recode);

        userId = getIntent().getIntExtra("userid",0);
        groupId = getIntent().getIntExtra("groupid",0);
        lvAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,signinStr);
        lvSignin = findViewById(R.id.lv_signins);
        lvSignin.setAdapter(lvAdapter);
        lvSignin.setOnItemClickListener(lvListener);
        loadSignins();
    }

    private void loadSignins(){
        new Thread(()->{
            SigninService signinService = SigninServicePoxy.getInstance();
            signins = signinService.getSigninByReceiverAndGroupId(userId,groupId);
            if(signins.size()!=0){
                runOnUiThread(()->updateListView());
            }
        }).start();
    }

    private void updateListView(){
        for (Signin signin:signins){
            long timeMilles = signin.getTime();
            Date date = new Date(timeMilles);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日-HH时mm分");
            String s = sdf.format(date);
            signinStr.add("签到发起日期："+s);
        }
        lvAdapter.notifyDataSetChanged();
    }

    class MyLvItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            SingleSigninInfoActivity.startActivity(SigninRecodeActivity.this,signins.get(i));
        }
    }
}
