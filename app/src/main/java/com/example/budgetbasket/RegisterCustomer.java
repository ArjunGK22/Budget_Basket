package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;

public class RegisterCustomer extends AppCompatActivity {


    EditText name, phone, emailid, address, password, confpass;
    Button regbtn;
    ImageButton backbtn;
    TextView regshop;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        name = findViewById(R.id.textname);
        phone = findViewById(R.id.phone);
        emailid = findViewById(R.id.textemailId);
        address = findViewById(R.id.textaddress);
        password = findViewById(R.id.textpassword);
        confpass = findViewById(R.id.textconfpass);
        regbtn = (Button)findViewById(R.id.regbtn);
        regshop = findViewById(R.id.regvendor);
        backbtn = findViewById(R.id.backbtn);

        //initializing permissions
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputdata();
            }
        });

        regshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterCustomer.this,RegisterShop.class));
            }
        });




    }
    private String uname, uphone, uemail, uaddress, upassword,confpassword;
    private void inputdata() {
        uname = name.getText().toString().trim();
        uphone = phone.getText().toString().trim();
        uemail = emailid.getText().toString().trim();
        uaddress = address.getText().toString().trim();
        upassword = password.getText().toString().trim();
        confpassword = confpass.getText().toString().trim();

        if(TextUtils.isEmpty(uname)){
            Toast.makeText(this,"Enter your name",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(uphone)){
            Toast.makeText(this,"Enter phone number",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(uemail)){
            Toast.makeText(this,"Enter email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(uaddress)){
            Toast.makeText(this,"Enter your address",Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length()<6){
            Toast.makeText(this,"Password is short....",Toast.LENGTH_SHORT).show();
            return;
        }
        if(upassword!=confpassword){
            Toast.makeText(this,"Password does not match",Toast.LENGTH_SHORT).show();
            return;
        }

        createAcc();

    }



    private void createAcc() {

        //Displaying Progress Dialog
        progressDialog.setMessage("Creating Account.........Please Wait!");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(uemail, upassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveToFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterCustomer.this,""+e.getMessage(), LENGTH_LONG).show();
                    }
                });
    }

    private void saveToFirebase() {
        progressDialog.setMessage("Saving Account Details");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("name",""+uname);
        hashMap.put("phone",""+uphone);
        hashMap.put("email",""+uemail);
        hashMap.put("address",""+uaddress);
        hashMap.put("password",""+upassword);
        hashMap.put("usertype","customer");
        hashMap.put("online","true");



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        startActivity(new Intent(RegisterCustomer.this,CustomerMainScreen.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterCustomer.this,""+e.getMessage(),Toast.LENGTH_SHORT);
                        finish();
                    }
                });


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}


