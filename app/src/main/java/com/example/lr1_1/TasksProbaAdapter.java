package com.example.lr1_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TasksProbaAdapter extends RecyclerView.Adapter<TasksProbaAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<TaskProbaInfo> tasks;
    //private final List<String> tasks;

    TasksProbaAdapter(Context context, List<TaskProbaInfo> tasks) {
        //TasksAdapter(Context context, List<String> tasks) {
        this.tasks = tasks;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public TasksProbaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.card_item, parent, false);
        return new TasksProbaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksProbaAdapter.ViewHolder holder, int position) {
        TaskProbaInfo task = tasks.get(position);
        if (task.getName().isEmpty() || task.getAddress().isEmpty() ||
                task.getTime().isEmpty()) {
            holder.nameView.setText("newName");
            holder.addressView.setText("newAddress");
            holder.timeView.setText("00:00");
        }
        else {
            holder.nameView.setText(task.getName());
            holder.addressView.setText(task.getAddress());
            holder.timeView.setText(task.getTime());
        }

    }

    @Override
    public int getItemCount() {
        if (tasks == null) {
            return 0;
        }
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
