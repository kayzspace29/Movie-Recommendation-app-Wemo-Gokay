package com.kayzsystems.wemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.SigningInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    TextView signupTextView;
    TextInputEditText emailEdit, passwordEdit;

    ProgressDialog progressDialog;
    Button submitButton;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();


        signupTextView = findViewById(R.id.signUpText);

        submitButton = findViewById(R.id.submitButton);

        emailEdit = findViewById(R.id.signInEmailEdit);
        passwordEdit = findViewById(R.id.signInPasswordEdit);




        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailEdit.getText().toString().trim().equals("")){

                }
                else if(emailEdit.getText().toString().trim().equals("")){

                }
                else{
                    progressDialog = new ProgressDialog(SignInActivity.this);
                    progressDialog.setMessage("Signing in...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String email = emailEdit.getText().toString();
                    String password = passwordEdit.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();


                                        // Read from the database
                                        myRef.child(user.getUid().toString()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                // This method is called once with the initial value and again
                                                // whenever data at this location is updated.
                                                String userName = dataSnapshot.child("name").getValue(String.class);
                                                String userPhone = dataSnapshot.child("phone").getValue(String.class);
                                                String userEmail = dataSnapshot.child("email").getValue(String.class);

                                                SharedPreferences.Editor editor = getSharedPreferences("profile", MODE_PRIVATE).edit();
                                                editor.putString("name", userName);
                                                editor.putString("phone", userPhone);
                                                editor.putString("email", userEmail);
                                                editor.apply();
                                                Snackbar.make(getWindow().getDecorView().getRootView(), "Sign in successful", Snackbar.LENGTH_LONG)
                                                        .show();
                                                progressDialog.dismiss();
                                                Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                // Failed to read value
                                                progressDialog.dismiss();
                                                Snackbar.make(getWindow().getDecorView().getRootView(), "Sign in failed", Snackbar.LENGTH_LONG)
                                                        .show();
                                            }
                                        });


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Snackbar.make(getWindow().getDecorView().getRootView(), "Sign in failed", Snackbar.LENGTH_LONG)
                                                .show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });


        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }
}