package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackOrdersVendor extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderVendor> orderlist;
    private AdapterOrderVendor adapterOrderVendor;

    RecyclerView custorders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_orders_vendor);

        custorders = findViewById(R.id.custorders);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation); //Initialize the ui

        bottomNavigationView.setSelectedItemId(R.id.orders);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),SellerMainScreen.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.addproduct:
                        startActivity(new Intent(getApplicationContext(),AddProducts.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.orders:
                        return true;

                    case R.id.viewproduct:
                        startActivity(new Intent(getApplicationContext(),showProductsVendor.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        loadOrders();
    }

    private void loadOrders() {

        orderlist = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderlist.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                    reference.child(firebaseAuth.getUid()).child("orders")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    orderlist.clear();

                                    if(snapshot.exists()){
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            ModelOrderVendor modelOrderVendor = ds.getValue(ModelOrderVendor.class);

                                            orderlist.add(modelOrderVendor);

                                        }

                                        adapterOrderVendor = new AdapterOrderVendor(TrackOrdersVendor.this,orderlist);
                                        custorders.setAdapter(adapterOrderVendor);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}