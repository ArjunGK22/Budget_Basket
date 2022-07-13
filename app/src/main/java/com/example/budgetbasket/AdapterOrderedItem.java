package com.example.budgetbasket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem> {

    private Context context;
    private ArrayList<ModelOrderedItem> orderedItemList;
    private ArrayList<ModelOrderUser> modelOrderUsers;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderedItem> orderedItemList) {
        this.context = context;
        this.orderedItemList = orderedItemList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.ordereditem_row,parent,false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {

        ModelOrderedItem modelOrderedItem = orderedItemList.get(position);
        String pId = modelOrderedItem.getpId();
        String name = modelOrderedItem.getName();
        String cost = modelOrderedItem.getCost();
        String price = modelOrderedItem.getPrice();
        String quantity = modelOrderedItem.getQuantity();




        holder.itemName.setText(name);
        holder.itemPrice.setText("₹"+cost);
        holder.itemPriceEach.setText("₹"+price);
        holder.itemQuantity.setText("["+quantity+"]");






    }



    @Override
    public int getItemCount() {
        return orderedItemList.size();
    }

    class HolderOrderedItem extends RecyclerView.ViewHolder {

        TextView itemName, itemPrice,itemPriceEach,itemQuantity;
        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.nametv);
            itemPrice=itemView.findViewById(R.id.pricetv);
            itemPriceEach=itemView.findViewById(R.id.itempriceeachtv);
            itemQuantity=itemView.findViewById(R.id.itemQuantiytv);

        }
    }
}
