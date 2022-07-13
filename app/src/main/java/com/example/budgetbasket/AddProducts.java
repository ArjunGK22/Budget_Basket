package com.example.budgetbasket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddProducts extends AppCompatActivity {

    ImageButton back;
    ImageView prodimg;
    EditText pname,pdesc,pquant,pprice,pstock;
    TextView pcat;
    Button addproduct;

    FirebaseAuth firebaseAuth;
    ProgressDialog p1;


    private static final int cam_req_code = 200;
    private static final int storage_req = 300;

    private static final int from_gallery = 400;
    private static final int from_camera = 500;


    private String[] cam_pms;
    private String[] storage_pms;

    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        back = findViewById(R.id.backbtn);
        prodimg = findViewById(R.id.imageSel);
        pname = findViewById(R.id.prodname);
        pquant = findViewById(R.id.etquantity);
        pprice = findViewById(R.id.etprice);
        pdesc = findViewById(R.id.etdescription);
        pcat = findViewById(R.id.etcategory);
        addproduct=findViewById(R.id.btnadditem);
        pstock = findViewById(R.id.etstock);

        /* --------------------------------*/
        cam_pms=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storage_pms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        /* --------------------------------*/


        /* --------------------------------*/
        firebaseAuth = FirebaseAuth.getInstance();
        p1 = new ProgressDialog(this,R.style.MyAlertDialog);
        p1.setTitle("Please Wait");
        p1.setCanceledOnTouchOutside(false);
        /* --------------------------------*/

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation); //Initialize the ui

        bottomNavigationView.setSelectedItemId(R.id.addproduct);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),SellerMainScreen.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.addproduct:
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


        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputdata();
            }
        });

        pcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryBox();
            }
        });

        prodimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog();
                System.out.println("clicked");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddProducts.this,SellerMainScreen.class));
                finish();
            }
        });




    }

    private void categoryBox() { //dialog for prod categories

        AlertDialog.Builder altb = new AlertDialog.Builder(this);
        altb.setTitle("Categories").setItems(Categories.fields, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cat = Categories.fields[which];

                pcat.setText(cat);
            }
        }).show();


    }



    public String productName,productDescription,productQuantity,productPrice,productCategory,productStock;
    private void inputdata() {

        productName = pname.getText().toString().trim();
        productDescription = pdesc.getText().toString().trim();
        productQuantity = pquant.getText().toString().trim();
        productCategory = pcat.getText().toString().trim();
        productPrice = pprice.getText().toString().trim();
        productStock = pstock.getText().toString().trim();

        addToFirebase();

    }

    private void addToFirebase() {
        p1.setMessage("Adding the Products");
        p1.show();

        final String tstamp = ""+System.currentTimeMillis();

        if(uri==null){

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("productid",""+tstamp);
            hashMap.put("productname",""+productName);
            hashMap.put("productcategory",""+productCategory);
            hashMap.put("productdescription",""+productDescription);
            hashMap.put("productquantity",""+productQuantity);
            hashMap.put("productprice",""+productPrice);
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("productstock",productStock); //stocks

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Products").child(tstamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            p1.dismiss();
                            Toast.makeText(AddProducts.this,"Product added Successfully",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProducts.this,""+e.getMessage(),Toast.LENGTH_LONG);

                        }
                    });
        }
        else {

            String filepathname = "product_img/" + "" +tstamp;
            StorageReference sref = FirebaseStorage.getInstance().getReference(filepathname);
            sref.putFile(uri);

            sref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                     Uri download_uri = uriTask.getResult();

                     if(uriTask.isSuccessful()){
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("productid",""+tstamp);
                            hashMap.put("productname",""+productName);
                            hashMap.put("productcategory",""+productCategory);
                            hashMap.put("productdescription",""+productDescription);
                            hashMap.put("productquantity",""+productQuantity);
                            hashMap.put("productImage",""+download_uri);
                            hashMap.put("productprice",""+productPrice);
                            hashMap.put("uid",""+firebaseAuth.getUid());
                            hashMap.put("productstock",productStock); //stocks

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                            ref.child(firebaseAuth.getUid()).child("Products").child(tstamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            p1.dismiss();
                                            Toast.makeText(AddProducts.this,"Product added Successfully",Toast.LENGTH_LONG).show();
                                            cleardata();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddProducts.this,""+e.getMessage(),Toast.LENGTH_LONG);
                                            finish();

                                        }
                                    });

                        }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddProducts.this,""+e.getMessage(),Toast.LENGTH_LONG);

                }
            });
            

        }
    }


    private void imageDialog(){
        String[] opt = {"Camera","Gallery"};
        AlertDialog.Builder agk = new AlertDialog.Builder(this);
        agk.setTitle("Choose Image").setItems(opt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0) {
                    if (cameraPermission()) {
                        camera();
                    } else {
                        reqCameraPermission();
                    }
                }
                else{
                    if(storagePermission()){
                        gallery();
                    }
                    else{reqStoragePermission();}
                }
            }
        }).show();
    }

    private void gallery(){
        Intent i1 = new Intent(Intent.ACTION_PICK);
        i1.setType("image/*");
        startActivityForResult(i1, from_gallery);

    }

    private void camera(){
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp_Img_title");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp_desc");

        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent i1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i1.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(i1,from_camera);
    }

    private boolean storagePermission(){
        boolean res = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return res;

    }

    private void reqStoragePermission(){
        ActivityCompat.requestPermissions(this,storage_pms,storage_req);

    }

    private boolean cameraPermission(){
        boolean r1 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean r2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return r1 && r2;

    }

    private void reqCameraPermission(){
        ActivityCompat.requestPermissions(this,cam_pms,cam_req_code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case cam_req_code:
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        camera();
                    }
                }
                else {
                    Toast.makeText(this,"Permissions Required",Toast.LENGTH_LONG).show();
                }
            case storage_req:
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        gallery();
                    }
                    else {
                        Toast.makeText(this,"Permissions Required",Toast.LENGTH_LONG).show();
                    }
                }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode == from_gallery){
                uri = data.getData();
                prodimg.setImageURI(uri);
            }

            else if (requestCode==from_camera){
                prodimg.setImageURI(uri);
            }
        }



        super.onActivityResult(requestCode, resultCode, data);
    }

    private void cleardata(){
        pname.setText("");
        pdesc.setText("");
        pquant.setText("");
        pcat.setText("");
        pprice.setText("");
        pstock.setText("");
        prodimg.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
    }

}