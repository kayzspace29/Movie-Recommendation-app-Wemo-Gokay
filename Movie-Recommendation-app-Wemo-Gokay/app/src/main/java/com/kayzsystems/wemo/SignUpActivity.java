package com.kayzsystems.wemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");   //Creating a database reference

    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    TextView signInText;
    TextInputEditText signupName, signupPhone, signupEmail, signupPassword, signupPassword1;
    Button signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        signUpButton = findViewById(R.id.signUpButton);

        signInText = findViewById(R.id.signInText);

        //Attaching the edit texts to the front end
        signupName = findViewById(R.id.signUpNameEdit);
        signupEmail = findViewById(R.id.signUpEmailEdit);
        signupPhone = findViewById(R.id.signUpPhoneEdit);
        signupPassword = findViewById(R.id.signUpPasswordEdit);
        signupPassword1 = findViewById(R.id.signUpPasswordEdit1);

        //Handling what happens when the sign in text is clicked
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(signInIntent);
            }
        });

        //Handling what happens when signup button is clicked
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signupName.getText().toString().trim().equals("")){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter your name", Snackbar.LENGTH_LONG)
                            .show();
                }
                else if(signupEmail.getText().toString().trim().equals("")){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter your email address", Snackbar.LENGTH_LONG)
                            .show();
                }
                else if(signupPhone.getText().toString().trim().equals("")){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter your phone number", Snackbar.LENGTH_LONG)
                            .show();
                }
                else if(signupPassword.getText().toString().trim().length() < 6){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter a minimum of 6 characters", Snackbar.LENGTH_LONG)
                            .show();
                }
                else if(signupPassword1.getText().toString().trim().equals("")){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please verify your password", Snackbar.LENGTH_LONG)
                            .show();
                }
                else if(!(signupPassword.getText().toString().trim().equals(signupPassword1.getText().toString()))){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter your name", Snackbar.LENGTH_LONG)
                            .show();
                }
                //When all details seem valid
                else{
                    progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.setMessage("Signing up...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    String email = signupEmail.getText().toString();
                    String password = signupPassword.getText().toString();

                    //Create auser using firebase
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        progressDialog.dismiss();
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        //Write user details to Firebase
                                        myRef.child(user.getUid().toString()).child("name").setValue(signupName.getText().toString());
                                        myRef.child(user.getUid().toString()).child("email").setValue(signupEmail.getText().toString());
                                        myRef.child(user.getUid().toString()).child("phone").setValue(signupPhone.getText().toString());

                                        //Write user details to device
                                        SharedPreferences.Editor editor = getSharedPreferences("profile", MODE_PRIVATE).edit();
                                        editor.putString("name", signupName.getText().toString());
                                        editor.putString("phone", signupPhone.getText().toString());
                                        editor.putString("email", signupEmail.getText().toString());
                                        editor.apply();

                                        //Go back to MainActivity after sign up
                                        Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                    } else {
                                        // If sign up fails, display a message to the user.
                                        Snackbar.make(getWindow().getDecorView().getRootView(), "Sign up failed", Snackbar.LENGTH_LONG)
                                                .show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
    }
}