package com.example.a7_kabale.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a7_kabale.Logic.Card;
import com.example.a7_kabale.R;

import java.util.LinkedList;

/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
 **/
public class MoveAdapter extends RecyclerView.Adapter<MoveAdapter.MoveItemViewHolder> {

    private LinkedList<Card> items;

    public static class MoveItemViewHolder extends RecyclerView.ViewHolder {
        public TextView card1;
        public TextView card2;
        public TextView moveIndex;
        public MoveItemViewHolder(@NonNull View itemView) {
            super(itemView);
            card1 = itemView.findViewById(R.id.card1TV);
            card2 = itemView.findViewById(R.id.card2TV);
            moveIndex = itemView.findViewById(R.id.moveIndex);
        }
    }

    public MoveAdapter(LinkedList<Card> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MoveItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.move_item_layout, parent, false);
        return new MoveItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MoveItemViewHolder holder, int position) {
        Card currentItem;
        try {
            int offset = 2;
            holder.moveIndex.setText("Move " + (position+1));
            currentItem = items.get(position*offset);
            holder.card1.setText(currentItem.toString());
            currentItem = items.get(position*offset+1);
            holder.card2.setText(currentItem.toString());
        } catch (IndexOutOfBoundsException e) {

        }
    }

    @Override
    public int getItemCount() {
        return items.size()/2;
    }
}
