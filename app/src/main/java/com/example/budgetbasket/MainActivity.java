package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                fbase = FirebaseAuth.getInstance();
                //startActivity(new Intent(MainActivity.this,WelcomeScreen.class));

               FirebaseUser user = fbase.getCurrentUser();
                if(user==null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
                else{
                    userType();
                }


            }
        },2000);

    }

    private void userType() {

        DatabaseReference r = FirebaseDatabase.getInstance().getReference("users");
        r.orderByChild("uid").equalTo(fbase.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String actype = ""+ds.child("usertype").getValue();
                            if(actype.equals("customer")){
                                startActivity(new Intent(MainActivity.this,CustomerMainScreen.class));
                                finish();
                            }
                            else{
                                startActivity(new Intent(MainActivity.this,SellerMainScreen.class));
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