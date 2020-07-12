package com.example.bookbazar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity
{

    private EditText userName, fullName, OccuPation;
    private Button SaveInfoButton;
    private CircleImageView ProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private String currentUserId;
    private ProgressDialog LoadingBar;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        userName = (EditText) findViewById(R.id.setup_username_id);
        fullName = (EditText) findViewById(R.id.setup_fullname_id);
        OccuPation = (EditText) findViewById(R.id.setup_occupation_id);
        SaveInfoButton = (Button) findViewById(R.id.setup_infobutton_id);
        ProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image_id);

        LoadingBar=new ProgressDialog(this);

        SaveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SaveAccountSetupInfo();

            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onChoosefile();

            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("profileimgurl"))
                    {
                        String image = dataSnapshot.child("profileimgurl").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(ProfileImage);
                    }
                    else
                    {
                        Toast.makeText(SetupActivity.this,"Please Select profile image first ..",Toast.LENGTH_SHORT).show();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }

    private void onChoosefile()
    {
        CropImage.activity().start(SetupActivity.this);

    }



 @Override
 protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
 {
     super.onActivityResult(requestCode, resultCode, data);

     if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
     {
         CropImage.ActivityResult result = CropImage.getActivityResult(data);

         if(resultCode==RESULT_OK)
         {
             LoadingBar.setTitle("Adding Profile Picture");
             LoadingBar.setMessage("Please wait, while we are updating your new Profile picture.. ");
             LoadingBar.show();
             LoadingBar.setCanceledOnTouchOutside(true);
             Uri resultUri = result.getUri();
             final StorageReference  Filepath = UserProfileImageRef.child(currentUserId + ".jpg");
             Filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                 {
                     Filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri)
                         {
                             HashMap<String,String>hashMap=new HashMap<>();
                             hashMap.put("profileimgurl", String.valueOf(uri));


                             UsersRef.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid)
                                 {

                                     Intent setUpIntent =new Intent(SetupActivity.this,SetupActivity.class);
                                     startActivity(setUpIntent);
                                     Toast.makeText(SetupActivity.this,"finally profile picture stored database successfully",Toast.LENGTH_SHORT).show();
                                     LoadingBar.dismiss();

                                 }
                             });



                         }
                     });

                 }
             });

         }

         else
         {
             Toast.makeText(SetupActivity.this,"Error Occured ..Image can't be cropped,try again",Toast.LENGTH_SHORT).show();

         }
     }

 }


    private void SaveAccountSetupInfo()
    {

        String username=userName.getText().toString();
        String fullname=fullName.getText().toString();
        String occupation=OccuPation.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Please write your Username..",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this,"Please write your Full Name..",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(occupation))
        {
            Toast.makeText(this,"Please write your Occupation..",Toast.LENGTH_SHORT).show();
        }

        else
        {
            LoadingBar.setTitle("Saving Information");
            LoadingBar.setMessage("Please wait, while we are creating your new Account... ");
            LoadingBar.show();
            LoadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap =new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("occupation",occupation);
            userMap.put("status","hello, i am Shimanta");
            userMap.put("gender","none");
            userMap.put("dob","none");
            userMap.put("relationshipstatus","none");

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this,"Your account is created successfully..",Toast.LENGTH_LONG).show();
                        LoadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this,"error Occured : "+message,Toast.LENGTH_SHORT ).show();
                        LoadingBar.dismiss();
                    }


                }
            });

        }



    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(SetupActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
