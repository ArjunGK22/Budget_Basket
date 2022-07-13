package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class CustomerMainScreen extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    TextView username,categtv;

    ImageButton addtocart,filterbtn;
    EditText searchbar;

    //Displaying the products

    private RecyclerView itemList;

    private ArrayList<ModelProduct> productlist;
    private AdapterProductUser adapterProductUser;

    //Cart Det
    public ArrayList<ModelCartItem> cartItemList;
    public AdapterCartItem adapterCartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main_screen);

       // username = findViewById(R.id.username);
       addtocart = findViewById(R.id.addcart);
       filterbtn = findViewById(R.id.filterbtn);
       searchbar = findViewById(R.id.searchbar);

       categtv = findViewById(R.id.categ);

        //Displaying the products
        itemList = findViewById(R.id.itemslist);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation); //Initialize the ui

        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboard:
                        return true;

                    case R.id.orders:
                        startActivity(new Intent(getApplicationContext(),UserOrders.class));
                        overridePendingTransition(0,0);
                        return true;


                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileScreen.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //showCartDialog();
                startActivity(new Intent(CustomerMainScreen.this,CartItems.class));
            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{
                    adapterProductUser.getFilter().filter(s);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMainScreen.this,R.style.customAlert);
                builder.setTitle("Categories")
                        .setItems(Categories.fields1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Categories.fields1[which];
                                categtv.setText(selected);
                                if(selected.equals("All")){
                                    loadproducts();
                                }
                                else{
                                    //loadFilteredProducts(selected);
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();
        checkuser();
        loadproducts();
    }

    private void loadFilteredProducts(String selected) {

        productlist = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child("K2DurlCBHBQx4ViFWEETcfdGZIY2").child("Products")
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
                        adapterProductUser = new AdapterProductUser(CustomerMainScreen.this,productlist);
                        itemList.setAdapter(adapterProductUser);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void loadproducts() {
        productlist = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child("K2DurlCBHBQx4ViFWEETcfdGZIY2").child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productlist.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productlist.add(modelProduct);
                            System.out.println(modelProduct);

                        }

                        adapterProductUser = new AdapterProductUser(CustomerMainScreen.this,productlist);

                        itemList.setAdapter(adapterProductUser);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(CustomerMainScreen.this,"Error Loading the products",Toast.LENGTH_SHORT);

                    }
                });
    }

    private void checkuser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(CustomerMainScreen.this,WelcomeScreen.class));
            finish();
        }
        else{
            loadInfo();
        }

    }

    private void loadInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();;
                            String accountType = ""+ds.child("usertype").getValue();

                           // username.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }




}




