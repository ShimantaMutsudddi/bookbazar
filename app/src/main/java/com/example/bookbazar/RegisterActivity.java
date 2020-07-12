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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail,userPassword,userConfirmPassword;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();

        userEmail = (EditText) findViewById(R.id.register_email_id);
        userPassword = (EditText) findViewById(R.id.register_password_id);
        userConfirmPassword = (EditText) findViewById(R.id.register_confirmpass_id);
        createAccountButton = (Button) findViewById(R.id.register_create_account_id);

        LoadingBar=new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateNewAccount();

            }
        });
    }

    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void CreateNewAccount()
    {

        String user_email=userEmail.getText().toString();
        String user_password=userPassword.getText().toString();
        String confirm_password=userConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(user_email))
        {
            Toast.makeText(RegisterActivity.this,"Please Write Your Email...",Toast.LENGTH_SHORT).show();
        }

        else  if(TextUtils.isEmpty(user_password))
        {
            Toast.makeText(RegisterActivity.this,"Please Write Your Password...",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(confirm_password))
        {
            Toast.makeText(RegisterActivity.this,"Please Confirm Your Password...",Toast.LENGTH_SHORT).show();
        }
        else if(!user_password.equals(confirm_password))
        {
            Toast.makeText(RegisterActivity.this,"Your Password Do Not Match with Your Confirm Password...",Toast.LENGTH_SHORT).show();
        }
        else
        {

            LoadingBar.setTitle("Creating New account");
            LoadingBar.setMessage("Please wait, while we are creating your new Account... ");
            LoadingBar.show();
            LoadingBar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {

                        SendUserToSetupactivity();
                        Toast.makeText(RegisterActivity.this,"You areauthenticated Succesfully..",Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();
                    }

                    else
                    {
                        String message=task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this,"Error occured: "+message,Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();

                    }

                }
            });

        }



    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }


    private void SendUserToSetupactivity()
    {
        Intent setupIntent=new Intent(RegisterActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
