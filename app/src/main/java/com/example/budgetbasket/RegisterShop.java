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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;

public class RegisterShop extends AppCompatActivity {

    ImageButton backbtn;
    EditText name, phone, emailid, address, password, confpass,shopname,city;
    Button regbtn;

    private FirebaseAuth fbase;
    private ProgressDialog pgbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shop);

        backbtn = findViewById(R.id.bkbtn);

        /* Edit Text */
        name = (EditText)findViewById(R.id.ownername);
        shopname = (EditText)findViewById(R.id.textshopname);
        phone = (EditText)findViewById(R.id.textphone);
        emailid = (EditText)findViewById(R.id.textemailId);
        address = (EditText)findViewById(R.id.textaddress);
        city = (EditText)findViewById(R.id.textcity);
        password = (EditText)findViewById(R.id.textpassword);
        confpass = (EditText)findViewById(R.id.textconfpass);
        /* Edit Text */

        regbtn = (Button)findViewById(R.id.regshop);

        //FireBase
        fbase = FirebaseAuth.getInstance();
        pgbox = new ProgressDialog(this);
        pgbox.setTitle("Please Wait");
        pgbox.setCanceledOnTouchOutside(false);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterShop.this,WelcomeScreen.class));
            }
        });

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipdata();
            }
        });

    }

    private String oname, shpname, sphone, semail, saddress, scity,spass,sconfpass;
    private void ipdata() {
        oname = name.getText().toString().trim();
        shpname = shopname.getText().toString().trim();
        sphone = phone.getText().toString().trim();
        semail = emailid.getText().toString().trim();
        saddress = address.getText().toString().trim();
        scity = city.getText().toString().trim();
        spass = password.getText().toString().trim();
        sconfpass = confpass.getText().toString().trim();

        if(TextUtils.isEmpty(oname)){
            Toast.makeText(this,"Enter your name",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(shpname)){
            Toast.makeText(this,"Enter the shopname",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(semail)){
            Toast.makeText(this,"Enter email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(saddress)){
            Toast.makeText(this,"Enter your address",Toast.LENGTH_SHORT).show();
            return;
        }

        if(spass.length()<6){
            Toast.makeText(this,"Password is short....",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!spass.equals(sconfpass)){
            Toast.makeText(this,"Password does not match",Toast.LENGTH_SHORT).show();
            return;
        }

        shopacc();

    }

    private void shopacc() {
        pgbox.setMessage("Creating Account....");
        pgbox.show();

        fbase.createUserWithEmailAndPassword(semail, spass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveToFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pgbox.dismiss();
                        Toast.makeText(RegisterShop.this,""+e.getMessage(), LENGTH_LONG).show();
                    }
                });
    }

    private void saveToFirebase() {
        pgbox.setMessage("Saving Account Details");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+fbase.getUid());
        hashMap.put("name",""+oname);
        hashMap.put("shopname",""+shpname);
        hashMap.put("phone",""+sphone);
        hashMap.put("email",""+semail);
        hashMap.put("address",""+saddress);
        hashMap.put("city",""+scity);
        hashMap.put("password",""+spass);
        hashMap.put("usertype","vendor");
        hashMap.put("online","true");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(fbase.getUid()).setValue(hashMap) //reference.child(fbase.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pgbox.dismiss();
                        startActivity(new Intent(RegisterShop.this,SellerMainScreen.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pgbox.dismiss();
                        Toast.makeText(RegisterShop.this,""+e.getMessage(),Toast.LENGTH_SHORT);
                        finish();
                    }
                });
    }
}