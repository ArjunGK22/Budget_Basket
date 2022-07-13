package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class showProductsUser extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private RecyclerView itemList;

    private ArrayList<ModelProduct> productlist;
    private AdapterProductUser adapterProductUser;

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_products_user);

        itemList = findViewById(R.id.itemslist);


        firebaseAuth = FirebaseAuth.getInstance();
        loadProducts();

        back = findViewById(R.id.bkbtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadProducts() {

        productlist = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child("tJLy8nJuo6YS6TbVQn4XI2zYM1u2").child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productlist.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productlist.add(modelProduct);

                        }

                        adapterProductUser = new AdapterProductUser(showProductsUser.this,productlist);

                        itemList.setAdapter(adapterProductUser);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}