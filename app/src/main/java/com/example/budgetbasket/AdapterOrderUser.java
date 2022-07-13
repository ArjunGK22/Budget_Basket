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

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser>{

    private Context context;
    private ArrayList<ModelOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> orderUserList) {
        this.context = context;
        this.orderUserList = orderUserList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.order_row_details,parent,false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {
        ModelOrderUser modelOrderUser = orderUserList.get(position);

        String orderId = modelOrderUser.getOrderId();
        String ordercost = modelOrderUser.getOrderCost();
        String orderstatus = modelOrderUser.getOrderStatus();
        String ordertime = modelOrderUser.getOrderTime();

        holder.orderIdtv.setText(orderId);
        holder.totalamt.setText("Amount: ₹"+ordercost);


        if(orderstatus.equals("In Progress")){
            holder.orderstatustv.setTextColor(context.getResources().getColor(R.color.orange));
        }
        else if(orderstatus.equals("Delivered")){
            holder.orderstatustv.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }

        else if(orderstatus.equals("Cancelled")){
            holder.orderstatustv.setTextColor(context.getResources().getColor(R.color.red));

        }
        else if(orderstatus.equals("Cancelled by user")){
            holder.orderstatustv.setTextColor(context.getResources().getColor(R.color.red));
        }

        holder.orderstatustv.setText(orderstatus);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(ordertime));
        String dateformat = DateFormat.format("dd/MM/yyyy",calendar).toString();
        holder.orderdatetv.setText(dateformat);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,OrderedDetailsUser.class);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderUserList.size();
    }

    class HolderOrderUser extends RecyclerView.ViewHolder{


        public TextView orderIdtv, totalamt,orderdatetv,orderstatustv;
        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);
            orderIdtv = itemView.findViewById(R.id.ordertv);
            totalamt = itemView.findViewById(R.id.totalamt);
            orderdatetv = itemView.findViewById(R.id.orderdatetv);
            orderstatustv = itemView.findViewById(R.id.orderstatus);



        }
    }
}
