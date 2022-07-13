package com.example.budgetbasket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productlist, filterlist;
    private FilterProductsSeller filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productlist = productList;
        this.filterlist = productlist;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //putting the elements as a view
        View view = LayoutInflater.from(context).inflate(R.layout.product_display_row,parent,false);
        return new HolderProductSeller(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {

        ModelProduct modelProduct = productlist.get(position);

        String id = modelProduct.getProductid();
        String uid = modelProduct.getUid();
        String name = modelProduct.getProductname();
        String description = modelProduct.getProductdescription();
        String category = modelProduct.getProductcategory();
        String quantity = modelProduct.getProductquantity();
        String price = modelProduct.getProductprice();
        String pimg = modelProduct.getProductImage();

        //Setting the data
        holder.productname.setText(name);
        holder.productdecription.setText(description);
        holder.productprice.setText("₹"+price);
        holder.productquantity.setText(quantity);
        holder.productname.setText(name);

        try{
            Picasso.get().load(pimg).placeholder(R.drawable.ic_baseline_add_shopping_cart_24).into(holder.productimg);

        }
        catch (Exception e){
            holder.productimg.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDetails(modelProduct);

            }
        });


    }

    @Override
    public int getItemCount() {
        return productlist.size();

    }

    private void showDetails(ModelProduct modelProduct) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        View view = LayoutInflater.from(context).inflate(R.layout.product_details_seller,null);
        bottomSheetDialog.setContentView(view);



        //Initialize the views

        ImageButton backbtn = view.findViewById(R.id.backbtn);
        Button deletebtn = view.findViewById(R.id.delbtn);
        Button updatebtn = view.findViewById(R.id.updatebtn);


        ImageView productimg = view.findViewById(R.id.productimg);

        TextView productname = (TextView)view.findViewById(R.id.prodname);
        TextView productdecription = (TextView)view.findViewById(R.id.productdecription);
        TextView productquantity = (TextView)view.findViewById(R.id.productquantity);
        TextView productprice = (TextView)view.findViewById(R.id.productprice);

        //Getting data
        String id = modelProduct.getProductid();
        String uid = modelProduct.getUid();
        String name = modelProduct.getProductname();
        String description = modelProduct.getProductdescription();
        String category = modelProduct.getProductcategory();
        String quantity = modelProduct.getProductquantity();
        String price = modelProduct.getProductprice();
        String pimg = modelProduct.getProductImage();

        //Setting up the data
        productname.setText(name);
        productdecription.setText(description);
        productprice.setText("₹"+price);
        productquantity.setText(quantity);
        productname.setText(name);

        try{
            Picasso.get().load(pimg).placeholder(R.drawable.ic_baseline_add_shopping_cart_24).into(productimg);

        }
        catch (Exception e){
            productimg.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        }

        bottomSheetDialog.show();


        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete the product?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProduct(id);
                                dialog.dismiss();
                                bottomSheetDialog.dismiss();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                bottomSheetDialog.dismiss();

                            }
                        }).show();
            }
        });

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("productId", id);
                context.startActivity(intent);
            }
        });

    }

    private void deleteProduct(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Product has been deleted successfully",Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Error occured while deleting.",Toast.LENGTH_SHORT);

            }
        });
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterProductsSeller(this,filterlist);

        }

        return filter;
    }


    class HolderProductSeller extends RecyclerView.ViewHolder{

        //Holding all the views of recycler view

        private ImageView productimg;
        private TextView productname,productdecription,productquantity,productprice;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);
            productimg = itemView.findViewById(R.id.productimg);
            productname = itemView.findViewById(R.id.productname);
            productdecription = itemView.findViewById(R.id.productdecription);
            productquantity = itemView.findViewById(R.id.productquantity);
            productprice = itemView.findViewById(R.id.productprice);




        }
    }
}
