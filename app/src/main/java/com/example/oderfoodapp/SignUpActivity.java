package com.example.oderfoodapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oderfoodapp.Common.Common;
import com.example.oderfoodapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity {
    MaterialEditText edtNAme,edtPhone, edtPassword, edtSecureCode;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPassword = findViewById(R.id.edtPassword);
        edtNAme     = findViewById(R.id.edtName);
        edtPhone    = findViewById(R.id.edtphone);
        btnSignUp   = findViewById(R.id.btn_SignUp);
        edtSecureCode =  findViewById(R.id.edtSecureCode);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("Please waiting...");
                    mDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Phone number already register", Toast.LENGTH_SHORT).show();


                            } else {
                                mDialog.dismiss();
                                User user = new User(edtNAme.getText().toString(), edtPassword.getText().toString(),edtSecureCode.getText().toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                Toast.makeText(SignUpActivity.this, "Sign up successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    Toast.makeText(SignUpActivity.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
