package com.example.creationclientdebug.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregiste.R;
import com.example.loginregiste.activity_login;
import com.henu.entity.User;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.UserService;
import com.henu.types.ReturnType;

import java.io.File;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText passwordText,certainPasswordText,username;
    private CheckBox passwordEye,certainPasswordEye;
    private ImageView registerBack,btnClearPhoneNumber,btnCleanRegisterCode;
    private Button btnRegisterCode,btnSaveInfo;
    private EditText etPhoneNumber,etRegisterCode;
    private TextView tvTip;

    private int time = 60;
    private boolean flag = false;
    private String userName,psw,pswAgain;//获取用户名，密码
    private String iPhone;//手机号码
    private String iCord;//验证码
    private String imgString;//头像转化为字符串

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    public EventHandler eh; //事件接收器
    private RegisterActivity.TimeCount mTimeCount;//计时器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initData();
    }

    private void initView(){
        passwordEye = findViewById(R.id.password_eye);
        certainPasswordEye = findViewById(R.id.certain_password_eye);
        passwordText = findViewById(R.id.register_et_password);
        certainPasswordText = findViewById(R.id.register_et_repassword);
        username = findViewById(R.id.register_et_username);
        registerBack = findViewById(R.id.register_titleBar_iv_back);
        etPhoneNumber = findViewById(R.id.register_et_phoneNumber);
        btnClearPhoneNumber = findViewById(R.id.register_iv_clear_phoneNumber);
        etRegisterCode = findViewById(R.id.register_et_code);
        btnRegisterCode = findViewById(R.id.register_btn_getCode);
        btnCleanRegisterCode = findViewById(R.id.register_iv_clear_code);
        tvTip = findViewById(R.id.tip);
        btnSaveInfo = findViewById(R.id.save_info);
        mTimeCount = new RegisterActivity.TimeCount(60000, 1000);
    }

    private void initData(){
        registerBack.setOnClickListener(this);
        btnClearPhoneNumber.setOnClickListener(this);
        btnRegisterCode.setOnClickListener(this);
        btnCleanRegisterCode.setOnClickListener(this);
        btnSaveInfo.setOnClickListener(this);
        //切换明文密码与暗文密码
        passwordEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(passwordEye.isChecked()){
                    passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else{
                    passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        certainPasswordEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(certainPasswordEye.isChecked()){
                    certainPasswordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else{
                    certainPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //返回按钮
            case R.id.register_titleBar_iv_back:
                finish();
                break;
            //清空输入的手机号码
            case R.id.register_iv_clear_phoneNumber:
                etPhoneNumber.setText("");
                btnClearPhoneNumber.setVisibility(View.GONE);
                break;
            //短信注册码的发送
            case R.id.register_btn_getCode:
                if(!TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())){
                    if(etPhoneNumber.getText().toString().trim().length()==11){
                        iPhone = etPhoneNumber.getText().toString().trim();
                        SMSSDK.getVerificationCode("86",iPhone);
                        etRegisterCode.requestFocus();
                        btnRegisterCode.setVisibility(View.GONE);
                        tvTip.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(RegisterActivity.this, "请输入完整电话号码", Toast.LENGTH_LONG).show();
                        etPhoneNumber.requestFocus();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "请输入您的电话号码", Toast.LENGTH_LONG).show();
                    etPhoneNumber.requestFocus();
                }
                break;
            //清空输入的注册码
            case R.id.register_iv_clear_code:
                etRegisterCode.setText("");
                btnCleanRegisterCode.setVisibility(View.GONE);
                break;
            //注册按钮
            case R.id.save_info:
                register();
                break;
            default:
                break;
        }
    }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            tvTip.setClickable(false);
            tvTip.setText(l/1000 + "秒后重新获取");
        }

        @Override
        public void onFinish() {
            tvTip.setClickable(true);
            tvTip.setText("获取验证码");
        }
    }

    public void register(){
        UserService service = UserServicePoxy.getInstance();
        User user = new User();
        user.setAccount(username.getText().toString().trim());
        user.setPassword(passwordText.getText().toString().trim());
        user.setPhone(etPhoneNumber.getText().toString().trim());
        final String str = service.register(user);
        System.out.println(str);
        if(str.equals(ReturnType.USEREXISTS)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    username.setText("");
                    Toast.makeText(RegisterActivity.this, "该账号名已被注册，请重新输入！", Toast.LENGTH_SHORT).show();
                }
            });
        }else if(str.equals(ReturnType.PHONEEXITS)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etPhoneNumber.setText("");
                    Toast.makeText(RegisterActivity.this, "该手机号已被注册，请重新输入！", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, activity_login.class);
                    startActivity(intent);

                }
            });
        }

    }
}
