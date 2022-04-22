package com.example.eyedentify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private Button btnLogin;
    private Button btnCreate;
    private Button btnGuest;
    private FirebaseAuth mAuth;
    private final String TAG = "LOGIN DEBUG";


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
        btnLogin = findViewById(R.id.btnLogin);
        btnCreate = findViewById(R.id.btnCreate);
        //btnGuest = findViewById(R.id.btnGuest);




        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = etEmail.getText().toString();
                String str_pass = etPassword.getText().toString();

                if (str_email.equals("")) {
                    Toast.makeText(getApplicationContext(), "E-mail field is empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (str_pass.equals("")){
                    Toast.makeText(getApplicationContext(), "Password field is empty!",
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

                if (str_email.equals("")) {
                    Toast.makeText(getApplicationContext(), "E-mail field is empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (str_pass.equals("")){
                    Toast.makeText(getApplicationContext(), "Password field is empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                createAccount(str_email, str_pass);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // Check if user is signed in (non-null) and update UI accordingly.
           // reload();
        }

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
                            // If sign in fails, display a message to the user.
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
            Toast.makeText(getApplicationContext(), "Invalid email",
                    Toast.LENGTH_SHORT).show();
        }catch(FirebaseAuthWeakPasswordException e) {
            Toast.makeText(getApplicationContext(), "Password must be minimum 6 characters",
                    Toast.LENGTH_SHORT).show();
        } catch (FirebaseAuthInvalidCredentialsException e){
            Toast.makeText(getApplicationContext(), "Invalid login credentials",
                    Toast.LENGTH_SHORT).show();
        } catch (FirebaseNetworkException e){
            Toast.makeText(getApplicationContext(), "Not connected to internet",
                    Toast.LENGTH_SHORT).show();

        }catch (FirebaseAuthUserCollisionException e){
            Toast.makeText(getApplicationContext(), "Email already registered",
                    Toast.LENGTH_SHORT).show();

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Authentication failed unknown error.",
            Toast.LENGTH_SHORT).show();

        }

    }

    private void launch_main_activity(){
        Intent main_activity = new Intent(getApplicationContext(), MainActivity.class);
        //put user data in bundle here, if we do anything with user data
        startActivity(main_activity);
    }
}