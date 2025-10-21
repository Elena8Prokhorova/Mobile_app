package com.example.lr1_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProbaAdapter extends RecyclerView.Adapter<ProbaAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<String> tasks;
    private final int element;

    ProbaAdapter(Context context, int element, List<String> tasks) {
        this.tasks = tasks;
        this.element = element;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public ProbaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.card_item, parent, false);
        return new ProbaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProbaAdapter.ViewHolder holder, int position) {
        String task = tasks.get(position);
        if (task.isEmpty()) {
            holder.nameView.setText("newName");
            holder.addressView.setText("newAddress");
            holder.timeView.setText("00:00");
        }
        else {
            holder.nameView.setText("Name");
            holder.addressView.setText("Address");
            holder.timeView.setText("24:00");
        }

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, addressView, timeView;
        ViewHolder(View view){
            super(view);
            nameView = view.findViewById(R.id.textName);
            addressView = view.findViewById(R.id.textPrice);
            timeView = view.findViewById(R.id.textTime);
        }
    }
}
