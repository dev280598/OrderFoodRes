package com.example.oderfoodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oderfoodapp.Common.Common;
import com.example.oderfoodapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    Button btnSignIn;
    CheckBox cb_remember;
    TextView txtForgotPwd;

    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtphone);
        btnSignIn = findViewById(R.id.btnSignIn);

        cb_remember = findViewById(R.id.cb_remember);
        txtForgotPwd =(TextView) findViewById(R.id.txtForgotPwd);

        Paper.init(this);

         database = FirebaseDatabase.getInstance();
         table_user = database.getReference("User");
        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    if (cb_remember.isChecked()){
                        Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                        Paper.book().write(Common.PASSWORD_KEY,edtPassword.getText().toString());
                    }


                    final android.app.AlertDialog mDialog = new SpotsDialog(SignInActivity.this);
                    mDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());
                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                    Common.currentUser = user;
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignInActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignInActivity.this, "User not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(SignInActivity.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });

    }

    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater  = this.getLayoutInflater();
        View forgot_view =  inflater.inflate(R.layout.forgot_password_layout, null);
        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = (MaterialEditText)forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCode = (MaterialEditText)forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtPhone.getText()   .toString())
                                .getValue(User.class);

                        if(user.getSecureCode().equals(edtSecureCode.getText().toString()))
                            Toast.makeText(SignInActivity.this, "Your password "+user.getPassword(), Toast.LENGTH_LONG).show();
                        else {
                            Toast.makeText(SignInActivity.this, "Wrong secure code", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }
}

