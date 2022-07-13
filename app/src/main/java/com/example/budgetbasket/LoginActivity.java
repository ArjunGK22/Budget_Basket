package com.example.budgetbasket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    TextView reglink;
    Button login;
    ImageButton back;
    private FirebaseAuth fbase;
    public ProgressDialog p1;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailId);
        password = findViewById(R.id.passtext);
        login = findViewById(R.id.loginbtn);

        reglink=findViewById(R.id.register);

        fbase = FirebaseAuth.getInstance();
        p1 = new ProgressDialog(this, R.style.MyAlertDialog);
        p1.setTitle("Please Wait");
        p1.setCanceledOnTouchOutside(false);



        reglink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterCustomer.class));
            }
        });

    //Login Button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateUser();
            }
        });

    }

    public String uemail,upassword;

    private void validateUser() {
        uemail = email.getText().toString().trim();
        upassword = password.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()){
            Toast.makeText(LoginActivity.this,"Invalid email format",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(upassword)){
            Toast.makeText(LoginActivity.this,"Enter the password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(uemail)){
            Toast.makeText(LoginActivity.this,"Enter the emailID",Toast.LENGTH_SHORT).show();
            return;
        }


        p1.setMessage("Signing In");
        p1.show();

        fbase.signInWithEmailAndPassword(uemail,upassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkStatus();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        p1.dismiss();
                        Toast.makeText(LoginActivity.this,"Invalid Username or Password",Toast.LENGTH_SHORT).show();


                    }
                });
    }


    private void checkStatus() {
        p1.setMessage("Verifying User");

        HashMap<String,Object> hmap = new HashMap<>();
        hmap.put("online","true");

        //updating the database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(fbase.getUid()).updateChildren(hmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userType();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        p1.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();



                    }
                });

    }

    private void userType() {

        DatabaseReference r = FirebaseDatabase.getInstance().getReference("users");
        r.orderByChild("uid").equalTo(fbase.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String actype = ""+ds.child("usertype").getValue();
                            if (actype.equals("customer")){
                                p1.dismiss();
                                startActivity(new Intent(LoginActivity.this,CustomerMainScreen.class));
                                finish();
                            }
                            else{
                                p1.dismiss();
                                startActivity(new Intent(LoginActivity.this,SellerMainScreen.class));
                                finish();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}