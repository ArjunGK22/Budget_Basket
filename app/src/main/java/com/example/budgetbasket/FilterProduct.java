package com.example.budgetbasket;

import android.view.Display;
import android.widget.Filter;

import java.util.ArrayList;

public class  FilterProduct extends Filter {
    private AdapterProductUser adapter;
    private ArrayList<ModelProduct> filterlist;

    public FilterProduct(AdapterProductUser adapter, ArrayList<ModelProduct> filterlist){
        this.adapter = adapter;
        this.filterlist = filterlist;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if(constraint!=null && constraint.length()>0){

            ArrayList<ModelProduct> filteredModel = new ArrayList<>();
            constraint = constraint.toString().toUpperCase();

            for(int i = 0;i<filterlist.size();i++ ){

                if(filterlist.get(i).getProductname().toUpperCase().contains(constraint) || filterlist.get(i).getProductcategory().toUpperCase().contains(constraint)){
                    filteredModel.add(filterlist.get(i));
                }
            }
            results.count = filteredModel.size();
            results.values = filteredModel;
        }
        else{
            results.count = filterlist.size();
            results.values = filterlist;
        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.productslist = (ArrayList<ModelProduct>) results.values;

        adapter.notifyDataSetChanged();

    }
}
