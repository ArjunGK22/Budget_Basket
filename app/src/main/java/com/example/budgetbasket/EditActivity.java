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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditActivity extends AppCompatActivity {

    ImageButton back;
    ImageView prodimg;
    EditText pname,pdesc,pquant,pprice,pstock;
    TextView pcat;
    Button updateproduct;

    FirebaseAuth firebaseAuth;
    ProgressDialog p1;


    private static final int cam_req_code = 200;
    private static final int storage_req = 300;

    private static final int from_gallery = 400;
    private static final int from_camera = 500;


    private String[] cam_pms;
    private String[] storage_pms;

    private Uri uri;

    private String productId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        productId = getIntent().getStringExtra("productId");


        back = findViewById(R.id.backbtn);
        prodimg = findViewById(R.id.imageSel);
        pname = findViewById(R.id.prodname);
        pquant = findViewById(R.id.etquantity);
        pprice = findViewById(R.id.etprice);
        pdesc = findViewById(R.id.etdescription);
        pcat = findViewById(R.id.etcategory);
        updateproduct=findViewById(R.id.btnupdateitem);
        pstock = findViewById(R.id.etstock);

        /* --------------------------------*/
        cam_pms=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storage_pms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        /* --------------------------------*/


        /* --------------------------------*/
        firebaseAuth = FirebaseAuth.getInstance();
        loadProductDetails();
        p1 = new ProgressDialog(this,R.style.MyAlertDialog);
        p1.setTitle("Please Wait");
        p1.setCanceledOnTouchOutside(false);
        /* --------------------------------*/


        updateproduct.setOnClickListener(new View.OnClickListener() {
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
                startActivity(new Intent(EditActivity.this,showProductsVendor.class));
                finish();
            }
        });

    }

    private void loadProductDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String productId = ""+snapshot.child("productId").getValue();
                        String productname = ""+snapshot.child("productname").getValue();
                        String productcategory = ""+snapshot.child("productcategory").getValue();
                        String productdescription = ""+snapshot.child("productdescription").getValue();
                        String productquantity = ""+snapshot.child("productquantity").getValue();
                        String productprice = ""+snapshot.child("productprice").getValue();
                        String productImage = ""+snapshot.child("productImage").getValue();
                        String uid = ""+snapshot.child("uid").getValue();


                        pname.setText(productname);
                        pdesc.setText(productdescription);
                        pquant.setText(productquantity);
                        pprice.setText(productprice);
//                        pstock.setText(productname);
                        pcat.setText(""+productcategory);

                        try{
                            Picasso.get().load(productImage).placeholder(R.drawable.ic_baseline_add_shopping_cart_24).into(prodimg);

                        }
                        catch (Exception e){
                            prodimg.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

        p1.setMessage("Updating the Product");
        p1.show();


        if(uri==null){

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("productid",""+productId);
            hashMap.put("productname",""+productName);
            hashMap.put("productcategory",""+productCategory);
            hashMap.put("productdescription",""+productDescription);
            hashMap.put("productquantity",""+productQuantity);
            hashMap.put("productprice",""+productPrice);
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("productstock",productStock); //stocks

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Products").child(productId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            p1.dismiss();
                            Toast.makeText(EditActivity.this,"Product updated Successfully",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditActivity.this,""+e.getMessage(),Toast.LENGTH_LONG);

                        }
                    });
        }
        else {

            String filepathname = "product_img/" + "" +productId ;
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
                                hashMap.put("productid",""+productId);
                                hashMap.put("productname",""+productName);
                                hashMap.put("productcategory",""+productCategory);
                                hashMap.put("productdescription",""+productDescription);
                                hashMap.put("productquantity",""+productQuantity);
                                hashMap.put("productImage",""+download_uri);
                                hashMap.put("productprice",""+productPrice);
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("productstock",productStock); //stocks

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                                ref.child(firebaseAuth.getUid()).child("Products").child(productId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                p1.dismiss();
                                                Toast.makeText(EditActivity.this,"Product Updated Successfully",Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditActivity.this,""+e.getMessage(),Toast.LENGTH_LONG);
                                                finish();

                                            }
                                        });

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditActivity.this,""+e.getMessage(),Toast.LENGTH_LONG);

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
}