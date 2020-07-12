package com.example.bookbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccountLink;
    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NeedNewAccountLink = (TextView) findViewById(R.id.register_account_link_id);
        UserEmail=(EditText) findViewById(R.id.login_email_id);
        UserPassword=(EditText) findViewById(R.id.login_password_id);
        LoginButton=(Button) findViewById(R.id.login_button_id);

        LoadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToRegisterActivity();

            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AllowingUserTologin();

            }
        });
    }

    //if user is already logged in
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void AllowingUserTologin()
    {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please write your Email: ",Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please write your password : ",Toast.LENGTH_SHORT).show();
        }
       else
        {
            LoadingBar.setTitle("Login..");
            LoadingBar.setMessage("Please wait, while we are allowing you to login into your new Account... ");
            LoadingBar.show();
            LoadingBar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this,"You are Logged In Succesfully ..",Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();

                    }
                    else
                    {
                        String message=task.getException().getMessage();
                        Toast.makeText(LoginActivity.this,"Error Occured : "+message,Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();

                    }


                }
            });

        }

    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void SendUserToRegisterActivity()
    {
        Intent RegisterIntent =new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(RegisterIntent);

    }
}
