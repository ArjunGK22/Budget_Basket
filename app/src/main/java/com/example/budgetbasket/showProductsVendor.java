package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class showProductsVendor extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private RecyclerView itemList;
    private EditText search;

    ImageButton filterbtn;

    private ArrayList<ModelProduct> productlist;
    private AdapterProductSeller adapterProductSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_products_vendor);

        itemList = findViewById(R.id.itemslist);
        search = findViewById(R.id.searchproducts);

        filterbtn = findViewById(R.id.filterbtn);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation); //Initialize the ui

        bottomNavigationView.setSelectedItemId(R.id.viewproduct);

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

                    case R.id.viewproduct:
                        return true;

                    case R.id.orders:
                        startActivity(new Intent(getApplicationContext(),TrackOrdersVendor.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{
                    adapterProductSeller.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(showProductsVendor.this,R.style.customAlert);
                builder.setTitle("Categories")
                        .setItems(Categories.fields1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Categories.fields1[which];
                                if(selected.equals("All")){
                                    loadProducts();
                                }
                                else{
                                    adapterProductSeller.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();
        loadProducts();


    }

    private void loadFilteredProducts(String selected) {

        productlist = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productlist.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String prodcat = ""+ds.child("category").getValue();
                            if(selected.equals(prodcat)){

                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productlist.add(modelProduct);


                            }

                        }
                        adapterProductSeller = new AdapterProductSeller(showProductsVendor.this,productlist);
                        itemList.setAdapter(adapterProductSeller);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadProducts() {
        productlist = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productlist.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productlist.add(modelProduct);

                        }

                        adapterProductSeller = new AdapterProductSeller(showProductsVendor.this,productlist);

                        itemList.setAdapter(adapterProductSeller);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}