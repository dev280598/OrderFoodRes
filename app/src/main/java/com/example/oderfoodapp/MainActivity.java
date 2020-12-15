package com.example.oderfoodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oderfoodapp.Common.Common;
import com.example.oderfoodapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button btnSignIn, btnSignUp;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignup);



        txtSlogan = findViewById(R.id.txtSlogan);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(typeface);

        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(i);

            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PASSWORD_KEY);
        if (user != null && pwd != null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
                login(user,pwd);
        }
    }

    private void login(final String phone, final String pwd) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())) {
            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please waiting...");
            mDialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(phone).exists()) {
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)){
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Common.currentUser = user;
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
