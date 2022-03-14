package com.example.welcomepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {
    ArrayList<ViewModel> viewModels;
    ViewPager2 viewPager2;
   // Context context;

    public ViewAdapter(ArrayList<ViewModel> viewModels, ViewPager2 viewPager2) {
        this.viewModels = viewModels;
        this.viewPager2 = viewPager2;
    }

   /* public ViewAdapter(ArrayList<ViewModel> viewModels, Context context) {
        this.viewModels = viewModels;
        this.context = context;
    }*/


    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_pager,parent,false);
        return new ViewHolder(itemView);
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //  holder.imageView.setImageResource(mainModels.get(position).getLangLogo());
        ViewModel viewModel= viewModels.get(position);
        //holder.imageView.setImageResource(viewModels.get(position).getViewImage());
        Glide.with(viewPager2)
                .load(viewModel.getViewImage())
                .into(holder.imageView);

        /*Picasso.with(context)
                .load(viewModel.getViewImage())
                .into(holder.images);*/
    }

    // This Method returns the size of the Array
    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ViewHolder(@NonNull @NotNull View item) {
            super(item);
            imageView = item.findViewById(R.id.images);
        }
    }


























}
