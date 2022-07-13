package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

public class SellerMainScreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ImageButton addprod, editbtn,logoutbtn;
    FirebaseAuth fbase;
    TextView owner_name, shop_name, shop_email,shop_phone;
    Button nav_to_show;

    ProgressDialog p1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main_screen);

        logoutbtn = findViewById(R.id.logoutbtn);
        editbtn = findViewById(R.id.editbtn);
        owner_name = (TextView)findViewById(R.id.ownername) ;
        shop_name = (TextView)findViewById(R.id.shopnmae) ;
        shop_email = (TextView)findViewById(R.id.shopmail) ;
        shop_phone = (TextView)findViewById(R.id.shophone) ;

        p1 = new ProgressDialog(this,R.style.MyAlertDialog);

       BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation); //Initialize the ui

        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboard:
                        return true;

                    case R.id.addproduct:
                        startActivity(new Intent(getApplicationContext(),AddProducts.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.orders:
                        startActivity(new Intent(getApplicationContext(),TrackOrdersVendor.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.viewproduct:
                        startActivity(new Intent(getApplicationContext(),showProductsVendor.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });




       fbase = FirebaseAuth.getInstance();
       checkUser();

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makemeoffline();
            }
        });

    }

    private void makemeoffline() {

        p1.setMessage("Logging Out");

        HashMap<String,Object> hmap = new HashMap<>();
        hmap.put("online","false");

        //updating the database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(fbase.getUid()).updateChildren(hmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fbase.signOut();
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        p1.dismiss();
                        Toast.makeText(SellerMainScreen.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();



                    }
                });

    }


    private void checkUser(){
        FirebaseUser user = fbase.getCurrentUser();
        if(user==null){
            startActivity(new Intent(SellerMainScreen.this,LoginActivity.class));
            finish();
        }
        else{
            sellerDetails();
        }

    }

    private void sellerDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("uid").equalTo(fbase.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String ownername = ""+ds.child("name").getValue();
                            String shopname = ""+ds.child("shopname").getValue();
                            String emailid = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();

                            owner_name.setText(ownername);
                            shop_name.setText(shopname);
                            shop_email.setText(emailid);
                            shop_phone.setText(phone);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}