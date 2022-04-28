package com.example.eyedentify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private FirebaseAuth mAuth;
    private final String TAG = "LOGIN DEBUG";
    private CardView btnLogin;
    private CardView btnCreate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if user already logged in before displaying login activity
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login2);


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.cardViewLogin);
        btnCreate = findViewById(R.id.cardViewCreate);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = etEmail.getText().toString();
                String str_pass = etPassword.getText().toString();

                //check blank fields
                if (str_email.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.email_empty,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (str_pass.equals("")){
                    Toast.makeText(getApplicationContext(), R.string.password_empty,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //login firebase
                signIn(str_email, str_pass);
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = etEmail.getText().toString();
                String str_pass = etPassword.getText().toString();

                //check blank fields
                if (str_email.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.email_empty,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (str_pass.equals("")){
                    Toast.makeText(getApplicationContext(), R.string.password_empty,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                createAccount(str_email, str_pass);
            }
        });

    }

    private void createAccount(@NonNull String email,@NonNull  String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            launch_main_activity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            auth_error_handler(task);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(@NonNull String email, @NonNull String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            launch_main_activity();
                        } else {
                            // If sign in fails, log a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            auth_error_handler(task);
                        }
                    }
                });
        // [END sign_in_with_email]
    }


    public void auth_error_handler(Task<AuthResult> task){
        try {
            throw task.getException();
        } catch (FirebaseAuthEmailException e){
            Toast.makeText(getApplicationContext(), R.string.invalid_email,
                    Toast.LENGTH_SHORT).show();
        }catch(FirebaseAuthWeakPasswordException e) {
            Toast.makeText(getApplicationContext(), R.string.password_min,
                    Toast.LENGTH_SHORT).show();
        } catch (FirebaseAuthInvalidCredentialsException e){
            Toast.makeText(getApplicationContext(), R.string.invalid_cred,
                    Toast.LENGTH_SHORT).show();
        } catch (FirebaseNetworkException e){
            Toast.makeText(getApplicationContext(), R.string.no_internet,
                    Toast.LENGTH_SHORT).show();

        }catch (FirebaseAuthUserCollisionException e){
            Toast.makeText(getApplicationContext(), R.string.email_used,
                    Toast.LENGTH_SHORT).show();

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.error_unknown,
            Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        launch_main_activity();
    }

    public void launch_main_activity(){
        Intent main_activity = new Intent(getApplicationContext(), MainActivity.class);
        //put user data in bundle here, if we do anything with user data
        startActivity(main_activity);
        finish();
    }
}