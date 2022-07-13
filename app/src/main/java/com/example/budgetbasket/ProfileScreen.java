package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileScreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ImageButton editbtn;
    Button logoutbtn;
    TextView pname,pemail,pphone,paddress;
    String address;

    ProgressDialog p1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

         pname = findViewById(R.id.pname);
         pemail = findViewById(R.id.pemail);
         pphone = findViewById(R.id.pphone);
         paddress = findViewById(R.id.paddress);

        editbtn = findViewById(R.id.editbtn);
        logoutbtn = findViewById(R.id.logoutbtn);

        p1 = new ProgressDialog(this,R.style.MyAlertDialog);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation); //Initialize the ui

        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),CustomerMainScreen.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.orders:
                        startActivity(new Intent(getApplicationContext(),UserOrders.class));
                        overridePendingTransition(0,0);
                        return true;


                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        checkuser();

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileScreen.this,R.style.customAlert);
                builder.setTitle("LOG OUT")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makemeoffline();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        }).show();



            }
        });


    }



    private void loadInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();;
                            String phone = ""+ds.child("phone").getValue();;
                            String email = ""+ds.child("email").getValue();;
                            address = ""+ds.child("address").getValue();;
                            String accountType = ""+ds.child("usertype").getValue();

                            pname.setText(name);
                            pphone.setText(phone);
                            pemail.setText(email);
                            paddress.setText(address);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private void makemeoffline() {

        p1.setMessage("Logging Out");

        HashMap<String,Object> hmap = new HashMap<>();
        hmap.put("online","false");

        //updating the database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(firebaseAuth.getUid()).updateChildren(hmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseAuth.signOut();
                        checkuser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        p1.dismiss();
                        Toast.makeText(ProfileScreen.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();



                    }
                });

    }
    private void checkuser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(ProfileScreen.this,LoginActivity.class));
            finish();
        }
        else{
            loadInfo();
        }

    }
}