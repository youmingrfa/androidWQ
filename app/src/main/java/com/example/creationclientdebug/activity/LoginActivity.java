package com.example.creationclientdebug.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregiste.MainActivity;
import com.example.loginregiste.R;
import com.example.loginregiste.acticity_forget;
import com.henu.entity.User;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.UserService;
import com.henu.types.ReturnType;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mForgetPsdView,mRegisterView;//注册账号和忘记密码
    private EditText mAccountView,mPasswordView;
    private ImageView mClearAccountView, mClearPasswordView;
    private CheckBox mEyeView;
    private Button mLoginView;
    private String password,iPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
        initListener();
    }

    public void initView(){
        mAccountView = findViewById(R.id.et_input_account);
        mPasswordView = findViewById(R.id.et_input_password);
        mClearAccountView = findViewById(R.id.iv_clear_account);
        mClearPasswordView = findViewById(R.id.iv_clear_password);
        mEyeView = findViewById(R.id.iv_login_open_eye);
        mLoginView = findViewById(R.id.btn_login);
        mForgetPsdView = findViewById(R.id.tv_forget_password);
        mRegisterView = findViewById(R.id.tv_register_account);
    }

    public void initData(){
//        mPasswordView.setLetterSpacing(0.2f);

        mEyeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

    }

    public void initListener(){
        mClearAccountView.setOnClickListener(this);
        mClearPasswordView.setOnClickListener(this);
        mForgetPsdView.setOnClickListener(this);
        mRegisterView.setOnClickListener(this);
        mLoginView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.iv_clear_account:
                mAccountView.setText("");
                break;
            case R.id.iv_clear_password:
                mPasswordView.setText("");
                break;
            case R.id.tv_forget_password:
                intent = new Intent(LoginActivity.this,acticity_forget.class);
                startActivity(intent);
                break;
            case R.id.tv_register_account:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                iPhone=mAccountView.getText().toString().trim();
                password=mPasswordView.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Login();
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    public void Login(){

        UserService service = UserServicePoxy.getInstance();
        User user = new User();
        user.setPhone(iPhone);
        user.setPassword(password);
        final String str = service.login(user);

        if(ReturnType.USERNOTFOUND.equals(str)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "该用户不存在!", Toast.LENGTH_LONG).show();
                }
            });
        }else if(ReturnType.SERVERERR.equals(str)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "服务器异常，请稍后重试!", Toast.LENGTH_LONG).show();
                }
            });
        }else if(ReturnType.ERRORPSD.equals(str)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "密码错误!", Toast.LENGTH_LONG).show();
                }
            });
        }else if(ReturnType.RIGHTPSD.equals(str)){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("phoneNum",iPhone);
            startActivity(intent);
            User u = service.queryByPhone(user.getPhone());
            MainApp.startActivity(this,u);
            finish();
        }
    }
}
