package com.example.bookbazar;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.bookbazar.LoginActivity;
import com.example.bookbazar.R;
import com.example.bookbazar.SetupActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;




public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private RecyclerView postlist;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostsRef;
    private CircleImageView navProfileImage;
    private TextView NavProfileUserName,info;
    private String CurrentUserId;
    private ImageButton AddNewPostButton;
    private FirebaseUser CurrentUser;





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //Toolbar declaration
        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout= (DrawerLayout) findViewById(R.id.drawable_layout_id);
        //Actionbar toggle
        actionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



       /* //Recycler View
        postlist=(RecyclerView) findViewById(R.id.alluserpostlistRVid);
        postlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postlist.setLayoutManager(linearLayoutManager);*/



        //firebase auth

        mAuth=FirebaseAuth.getInstance();

       CurrentUser=mAuth.getCurrentUser();

      CurrentUserId= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();





        //Check realtime database

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");





        navigationView=(NavigationView) findViewById(R.id.navigation_view_id);

        //Navigation Header
        View navView =navigationView.inflateHeaderView(R.layout.navigation_header);

        navProfileImage=(CircleImageView) navView.findViewById(R.id.nav_profilepic_ID);
        NavProfileUserName=(TextView) navView.findViewById(R.id.nav_user_nameId);

        AddNewPostButton=(ImageButton) findViewById(R.id.new_post_buttonid);

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPostActivity();

            }
        });
      /*  FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(PostsRef, Posts.class)
                        .build();
        adapter =new PostAdapter(options);
        postlist.setAdapter(adapter);*/






       UsersRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {

                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname= dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }

                    if(dataSnapshot.hasChild("profileimgurl"))
                    {
                        String PImage= dataSnapshot.child("profileimgurl").getValue().toString();
                        Picasso.get().load(PImage).placeholder(R.drawable.profile).into(navProfileImage);
                    }

                    else
                    {
                        Toast.makeText(MainActivity.this,"profile Name do not exist",Toast.LENGTH_SHORT).show();

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Navigation View
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                UserMenuSelector(item);
                return false;
            }
        });




    }




    private void SendUserToPostActivity()
    {
        Intent addNewpostIntent=new Intent(MainActivity.this,PostActivity.class);
        startActivity(addNewpostIntent);

    }

    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
       // adapter.startListening();

        if (currentUser == null)
        {

            SendUserToLoginActivity();
        }
       else
       {
            CheckUserExistence();
       }
    }

    private void CheckUserExistence()
    {
       // String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {


                if(!dataSnapshot.hasChild(CurrentUserId))
                {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(setupIntent);
        finish();

    }

    private void SendUserToLoginActivity()
    {
        //to go login activity
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);

        //if user press back button then not to allow in main activity
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_profile_id:
               // SendUserToProfileActivity();
                break;
            case R.id.nav_post_id:
               SendUserToPostActivity();
                break;

            case R.id.nav_homeid:
                Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friendsid:
                Toast.makeText(this,"Friends",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_findfriendsid:
                //SendUserToFFActivity();
                Toast.makeText(this,"Find Friends",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_messagesid:
                Toast.makeText(this,"Messages",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settingsid:
               // SendUserToSettingsActivity();
                break;

            case R.id.nav_logoutid:

                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }
   /* private void SendUserToSettingsActivity()
    {
        //to go login activity
        Intent loginIntent = new Intent(MainActivity.this,SettingsActivity.class);

        startActivity(loginIntent);


    }
    private void SendUserToFFActivity()
    {
        //to go login activity
        Intent loginIntent = new Intent(MainActivity.this,FindFriendActivity.class);

        startActivity(loginIntent);


    }*/
   /* private void SendUserToProfileActivity()
    {

        Intent loginIntent = new Intent(MainActivity.this,ProfileActivity.class);

        startActivity(loginIntent);


    }*/

}

