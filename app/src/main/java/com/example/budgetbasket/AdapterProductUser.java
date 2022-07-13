package com.example.budgetbasket;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
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

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

import static java.lang.Math.abs;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable
{

    private Context context;

    public ArrayList<ModelProduct> productslist,filterlist;
    private FilterProduct filter;

    TextView stocktxt, stocktv;
    Button prodtocart;


    public AdapterProductUser(Context context, ArrayList<ModelProduct> productslist) {
        this.context = context;
        this.productslist = productslist;
        this.filterlist = productslist;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_product_row,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        ModelProduct modelProduct = productslist.get(position);
        String id = modelProduct.getProductid();
        String uid = modelProduct.getUid();
        String name = modelProduct.getProductname();
        String description = modelProduct.getProductdescription();
        String category = modelProduct.getProductcategory();
        String quantity = modelProduct.getProductquantity();
        String pimg = modelProduct.getProductImage();
        String stock = modelProduct.getProductstock();

        final String price;
        price = modelProduct.getProductprice();

        holder.productname.setText(name);
        holder.productprice.setText("₹"+price);
        holder.productquantity.setText(quantity);
        holder.prod_desc.setText(description);

        try{
            Picasso.get().load(pimg).placeholder(R.drawable.ic_baseline_add_shopping_cart_24).into(holder.productimg);

        }
        catch (Exception e){
            holder.productimg.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        }

        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuantityDetails(modelProduct);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productslist.size();
    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;


    private void showQuantityDetails(ModelProduct modelProduct) {


        View view = LayoutInflater.from(context).inflate(R.layout.quantitydetails,null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);



        ImageView productimg = view.findViewById(R.id.productimg);
        TextView productname = view.findViewById(R.id.productname);
        TextView productquantity = view.findViewById(R.id.productquantity);
        ImageButton decrementbtn = view.findViewById(R.id.decrementbtn);
        ImageButton incrementbtn = view.findViewById(R.id.incrementbtn);
        TextView quantitytv = view.findViewById(R.id.quantitytv);
        TextView productprice = view.findViewById(R.id.productprice);
        TextView totalprice = view.findViewById(R.id.totalprice);
        stocktv = view.findViewById(R.id.avail_stock);
        stocktxt = view.findViewById(R.id.stocktv);


        prodtocart = view.findViewById(R.id.prodtocart);


        String id = modelProduct.getProductid();
        String name = modelProduct.getProductname();
        String description = modelProduct.getProductdescription();
        String pquantity = modelProduct.getProductquantity();
        String price = modelProduct.getProductprice();
        String pimg = modelProduct.getProductImage();
        String tprice = modelProduct.getProductprice();
        String stock = modelProduct.getProductstock();

        System.out.println(id);
        System.out.println(name);

        productname.setText(name);
        productquantity.setText(pquantity);
        productprice.setText("₹"+price);
        totalprice.setText("₹"+tprice);
        stocktv.setText(""+stock);





        try{
            Picasso.get().load(pimg).placeholder(R.drawable.ic_baseline_add_shopping_cart_24).into(productimg);

        }
        catch (Exception e){
            productimg.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        }

        cost = Double.parseDouble(price.replaceAll("₹",""));
        finalCost = Double.parseDouble(price.replaceAll("₹",""));
        quantity = 1;



        incrementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;

                totalprice.setText("₹" +finalCost);
                quantitytv.setText(""+quantity);

            }
        });

        decrementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1){
                    finalCost = finalCost - cost;
                    quantity--;

                    totalprice.setText("₹" + finalCost);
                    quantitytv.setText(""+quantity);


                }
            }
        });

        prodtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = productname.getText().toString().trim();
                String priceEach = price;
                String totprice = totalprice.getText().toString().trim().replace("₹","");
                String quantity = quantitytv.getText().toString().trim();
                String stock = stocktv.getText().toString().trim();



                int istock = Integer.parseInt(stock);
                int iquantity = Integer.parseInt(quantity);

                int ustock = abs(istock - iquantity);

                String updated_stock = Integer.toString(ustock);


                addToCart(id,title,priceEach,totprice,quantity,updated_stock);

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();


    }

    private int item_id = 1;
    private void addToCart(String id, String title, String priceEach, String totprice, String quantity,String stock) {
        item_id++;

        EasyDB easyDB = EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Stock",new String[]{"text","not null"}))
                .doneTableColumn();

        Boolean data = easyDB.addData("Item_Id",item_id)
                .addData("Item_PID",id)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",totprice)
                .addData("Item_Quantity",quantity)
                .addData("Item_Stock",stock)
                .doneDataAdding();

        Toast.makeText(context,"Product Added to cart......",Toast.LENGTH_SHORT).show();
    }


    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterProduct(this, filterlist);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        ImageView productimg;
        TextView productname,productquantity,productprice,addtocart,prod_desc,product_stock;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productimg = itemView.findViewById(R.id.productimg);
            productname = itemView.findViewById(R.id.productname);
            productquantity = itemView.findViewById(R.id.productquantity);
            productprice = itemView.findViewById(R.id.productprice);
            addtocart = itemView.findViewById(R.id.addtocart);
            prod_desc = itemView.findViewById(R.id.productdesc);
            product_stock = itemView.findViewById(R.id.stocktv);



        }
    }
}
