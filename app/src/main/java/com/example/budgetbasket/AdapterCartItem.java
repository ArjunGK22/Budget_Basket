package com.example.budgetbasket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCardItem>{

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCardItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_row,parent,false);
        return new HolderCardItem(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderCardItem holder, int position) {

        ModelCartItem modelCartItem = cartItems.get(position);
        String id = modelCartItem.getId();
        String pId = modelCartItem.getpId();
        String title = modelCartItem.getName();
        String price = modelCartItem.getPrice();
        String cost = modelCartItem.getCost();
        String quantity = modelCartItem.getQuantity();

        holder.nametv.setText(""+title);
        holder.pricetv.setText(""+cost);
        holder.itempriceeachtv.setText(""+price);
        holder.itemQuantiytv.setText("["+quantity+"]");

        holder.removeitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                easyDB.deleteRow(1,id);
                Toast.makeText(context,"Product Deleted Successfully",Toast.LENGTH_SHORT).show();

                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                double tx = Double.parseDouble((((CartItems)context).allTotalPriceTv.getText().toString().trim().replace("₹","")));
                double totalprice = tx - Double.parseDouble(cost.replace("₹",""));
                ((CartItems)context).allTotalPrice = 0.00;
                ((CartItems)context).allTotalPriceTv.setText("₹"+String.format("%.2f",Double.parseDouble(String.format("%.2f",totalprice))));

            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    class HolderCardItem extends RecyclerView.ViewHolder{

        //views of order_row
        TextView nametv,pricetv,itempriceeachtv,itemQuantiytv,removeitem;



        public HolderCardItem(@NonNull View itemView) {
            super(itemView);

            nametv = itemView.findViewById(R.id.nametv);
            pricetv = itemView.findViewById(R.id.pricetv);
            itempriceeachtv = itemView.findViewById(R.id.itempriceeachtv);
            itemQuantiytv = itemView.findViewById(R.id.itemQuantiytv);
            removeitem = itemView.findViewById(R.id.removeitem);
        }
    }
}
