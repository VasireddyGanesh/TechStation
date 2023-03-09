package com.example.loadimages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerView_Adapter  extends RecyclerView.Adapter<RecyclerView_Adapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    List<ApiResponse> responses;

    public RecyclerView_Adapter(Context context, List<ApiResponse> responses,RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.responses = responses;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public RecyclerView_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row,parent,false);
        return new RecyclerView_Adapter.MyViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView_Adapter.MyViewHolder holder, int position) {
        Glide.with(context)
                .load(responses.get(position).getImage_url())
                .into(holder.imageView);
        holder.textView.setText(responses.get(position).getImage_headline());
        holder.likecount.setText(String.valueOf(responses.get(position).getImage_likes()));
        holder.sharecount.setText(String.valueOf(responses.get(position).getImage_shares()));
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        TextView sharecount;
        TextView likecount;
        public MyViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            textView=itemView.findViewById(R.id.tv_headline);
            likecount = itemView.findViewById(R.id.likeCount);
            sharecount = itemView.findViewById(R.id.shareCount);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }

    }
}

