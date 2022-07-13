package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class CartItems extends AppCompatActivity {

    public ArrayList<ModelCartItem> cartItemList;
    public AdapterCartItem adapterCartItem;

   TextView allTotalPriceTv;

    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    public String address1,phoneno;

    ImageButton backbtn;

    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_items);

        Button checkoutBtn = findViewById(R.id.confirmbtn);
        backbtn = findViewById(R.id.backbtn);

        progressDialog = new ProgressDialog(this,R.style.MyAlertDialog);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        allTotalPriceTv = findViewById(R.id.pricetotal);

        firebaseAuth = FirebaseAuth.getInstance();

        showCartDialog();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment();

            }
        });
    }

    private void payment() {

        if(cartItemList.size()==0){
            Toast.makeText(CartItems.this,"Cart is Empty",Toast.LENGTH_SHORT).show();

        }

        else{
            String cost = allTotalPriceTv.getText().toString().trim().replace("₹","");

            Uri uri =
                    new Uri.Builder()
                            .scheme("upi")
                            .authority("pay")
                            .appendQueryParameter("pa", "arjungkanikeri@okicici")
                            .appendQueryParameter("pn", "Arjun G K")
                            .appendQueryParameter("mc", "")
                            .appendQueryParameter("tr", "202105051735")
                            .appendQueryParameter("tn", "Budget Basket")
                            .appendQueryParameter("am", cost)
                            .appendQueryParameter("cu", "INR")
                            .appendQueryParameter("url", "your-transaction-url")
                            .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            Intent choice = Intent.createChooser(intent,"Pay With");
            startActivityForResult(choice, GOOGLE_PAY_REQUEST_CODE);

            startOrder();

        }


    }

    private void startOrder() {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("users");
        ref1.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        address1 = "" + snapshot.child("address").getValue();
                        phoneno = ""+snapshot.child("phone").getValue();
                        EasyDB easyDB = EasyDB.init(CartItems.this,"ITEMS_DB")
                                .setTableName("ITEMS_TABLE")
                                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                                .addColumn(new Column("Item_Stock",new String[]{"text","not null"}))
                                .doneTableColumn();
                        easyDB.deleteAllDataFromTable();

                        orderItem(address1,phoneno);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void orderItem(String address,String phone) {
        progressDialog.setMessage("Order Placing");
        progressDialog.show();


        String ts = ""+System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("₹","");
        HashMap<String,String> hashMap = new HashMap<>();

        hashMap.put("OrderId",""+ts);
        hashMap.put("OrderTime",""+ts);
        hashMap.put("OrderStatus","In Progress");
        hashMap.put("OrderCost",""+cost);
        hashMap.put("OrderBy",firebaseAuth.getUid());
        hashMap.put("Address",address);
        hashMap.put("Phone",phone);

        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference("users").child("K2DurlCBHBQx4ViFWEETcfdGZIY2").child("orders");
        ref.child(ts).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                for(int i = 0;i<cartItemList.size();i++){
                    String pId = cartItemList.get(i).getpId();
                    String id = cartItemList.get(i).getId();
                    String cost = cartItemList.get(i).getCost();
                    String name = cartItemList.get(i).getName();
                    String price = cartItemList.get(i).getPrice();
                    String quantity = cartItemList.get(i).getQuantity();
                    String stock = cartItemList.get(i).getStock();

                    HashMap<String,String> hashMap1 = new HashMap<>();
                    hashMap1.put("pId",pId);
                    hashMap1.put("name",name);
                    hashMap1.put("cost",cost);
                    hashMap1.put("price",price);
                    hashMap1.put("quantity",quantity);

                    ref.child(ts).child("Items").child(pId).setValue(hashMap1);

                    HashMap<String,Object> hashMap2 = new HashMap<>();
                    hashMap2.put("productstock",stock);

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("users");
                    ref2.child("K2DurlCBHBQx4ViFWEETcfdGZIY2").child("Products").child(pId).updateChildren(hashMap2);


                }
                progressDialog.dismiss();
                Toast.makeText(CartItems.this,"Order Placed Successfully",Toast.LENGTH_SHORT).show();
                onBackPressed();


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CartItems.this,"Order Failed",Toast.LENGTH_SHORT).show();

                    }
                });

    }


    public double allTotalPrice = 0.00;

    public RecyclerView cartItemsRv;
    private void showCartDialog() {

        System.out.println("Button Clicked......");

        cartItemList = new ArrayList<>();


        cartItemsRv = findViewById(R.id.orderlist1);

        EasyDB easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Stock",new String[]{"text","not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String id = res.getString(1);
            String pID = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);
            String stock = res.getString(7);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem("" + id, "" + pID, "" + name, "" + price, "" + cost, "" + quantity,""+stock);

            cartItemList.add(modelCartItem);

        }

        adapterCartItem = new AdapterCartItem(this,cartItemList);
        cartItemsRv.setAdapter(adapterCartItem);

        allTotalPriceTv.setText(""+allTotalPrice);






    }

}