package com.example.a7_kabale.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a7_kabale.Items.MoveItem;
import com.example.a7_kabale.R;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MoveAdapter extends RecyclerView.Adapter<MoveAdapter.MoveItemViewHolder> {

    private ArrayList<MoveItem> items;

    public static class MoveItemViewHolder extends RecyclerView.ViewHolder {
        public TextView pointsTV;
        public TextView moveTV;
        public MoveItemViewHolder(@NonNull View itemView) {
            super(itemView);
            pointsTV = itemView.findViewById(R.id.movePoints);
            moveTV = itemView.findViewById(R.id.moveInfo);
        }
    }

    public MoveAdapter(ArrayList<MoveItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MoveItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.move_item_layout, parent, false);
        return new MoveItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoveItemViewHolder holder, int position) {
        MoveItem currentItem = items.get(position);
        holder.pointsTV.setText(String.valueOf(currentItem.getPoint()));
        holder.moveTV.setText(currentItem.getMove());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
