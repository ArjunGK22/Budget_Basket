package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderedDetailsVendor extends AppCompatActivity {

    TextView orderidtv,orderdatetv,orderstatustv,orderamttv,paddress,phonetv;
    RecyclerView ordereditems;

    FirebaseAuth firebaseAuth;

    private String orderId;

    private ArrayList<ModelOrderedItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    ImageButton editbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_details_vendor);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        orderidtv = findViewById(R.id.orderidtv);
        orderdatetv = findViewById(R.id.orderdatetv);
        orderstatustv = findViewById(R.id.orderstatus);
        orderamttv = findViewById(R.id.orderamount);
        ordereditems = findViewById(R.id.orderitems);
        paddress = findViewById(R.id.paddress);
        phonetv = findViewById(R.id.pphone);

        editbtn = findViewById(R.id.editbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        loadOrderedItems();
        loadOrderedDetails();

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrderStatusDialog();
            }
        });


    }

    private void editOrderStatusDialog() {
        String[] options = {"In Progress","Delivered","Cancelled"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.customAlert);
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedOption = options[which];
                        editOrderStatus(selectedOption);
                    }
                }).show();

    }

    private void editOrderStatus(String selectedOption) {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("OrderStatus",""+selectedOption);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderedDetailsVendor.this,"Order Status Updated",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderedDetailsVendor.this,"Order Status Not Updated",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadOrderedItems() {
        orderedItemArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(firebaseAuth.getUid()).child("orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderedItemArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);

                            orderedItemArrayList.add(modelOrderedItem);

                        }

                        adapterOrderedItem = new AdapterOrderedItem(OrderedDetailsVendor.this,orderedItemArrayList);
                        ordereditems.setAdapter(adapterOrderedItem); //set the items from adapter to recycler view
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderedDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String orderBy = ""+snapshot.child("OrderBy").getValue();
                        String oId = ""+snapshot.child("OrderId").getValue();
                        String orderTime = ""+snapshot.child("OrderTime").getValue();
                        String orderStatus = ""+snapshot.child("OrderStatus").getValue();
                        String OrderCost = ""+snapshot.child("OrderCost").getValue();
                        String address = ""+snapshot.child("Address").getValue();
                        String phone = ""+snapshot.child("Phone").getValue();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String dateformat = DateFormat.format("dd/MM/yyyy hh:mm",calendar).toString();

                        if(orderStatus.equals("In Progress")){
                            orderstatustv.setTextColor(getResources().getColor(R.color.orange));
                        }
                        else if(orderStatus.equals("Delivered")){
                            orderstatustv.setTextColor(getResources().getColor(R.color.colorAccent));
                        }

                        else if(orderStatus.equals("Cancelled")){
                            orderstatustv.setTextColor(getResources().getColor(R.color.red));

                        }

                        orderidtv.setText(oId);
                        orderdatetv.setText(dateformat);
                        orderamttv.setText("₹"+OrderCost);
                        orderstatustv.setText(orderStatus);
                        paddress.setText(address);
                        phonetv.setText(phone);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}