package com.example.budgetbasket;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderVendor extends RecyclerView.Adapter<AdapterOrderVendor.HolderOrderVendor>{

    private Context context;
    private ArrayList<ModelOrderVendor> orderVendorArrayList;

    public AdapterOrderVendor(Context context, ArrayList<ModelOrderVendor> orderVendorArrayList) {
        this.context = context;
        this.orderVendorArrayList = orderVendorArrayList;
    }

    @NonNull
    @Override
    public HolderOrderVendor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_details_vendor,parent,false);

        return new HolderOrderVendor(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderVendor holder, int position) {

        ModelOrderVendor modelOrderVendor = orderVendorArrayList.get(position);

        String orderId = modelOrderVendor.getOrderId();
        String ordercost = modelOrderVendor.getOrderCost();
        String orderstatus = modelOrderVendor.getOrderStatus();
        String ordertime = modelOrderVendor.getOrderTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(ordertime));
        String dateformat = DateFormat.format("dd/MM/yyyy",calendar).toString();
        holder.orderdatetv.setText(dateformat);
        holder.orderIdtv.setText(orderId);
        holder.totalamt.setText("Amount: ₹"+ordercost);


        if(orderstatus.equals("In Progress")){
            holder.orderstatus.setTextColor(context.getResources().getColor(R.color.orange));
        }
        else if(orderstatus.equals("Delivered")){
            holder.orderstatus.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }

        else if(orderstatus.equals("Cancelled")){
            holder.orderstatus.setTextColor(context.getResources().getColor(R.color.red));

        }
        else if(orderstatus.equals("Cancelled by user")){
            holder.orderstatus.setTextColor(context.getResources().getColor(R.color.red));
        }

        holder.orderstatus.setText(orderstatus);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,OrderedDetailsVendor.class);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return orderVendorArrayList.size();
    }

    class HolderOrderVendor extends RecyclerView.ViewHolder {


        TextView orderIdtv, totalamt, orderdatetv, orderstatus;

        public HolderOrderVendor(@NonNull View itemView) {
            super(itemView);
            orderIdtv = itemView.findViewById(R.id.ordertv);
            totalamt = itemView.findViewById(R.id.totalamt);
            orderdatetv = itemView.findViewById(R.id.orderdatetv);
            orderstatus = itemView.findViewById(R.id.orderstatus);


        }
    }
}