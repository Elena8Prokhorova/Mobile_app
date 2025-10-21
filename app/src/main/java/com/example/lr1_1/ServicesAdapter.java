package com.example.lr1_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<String> services;
    private FragmentManager manager;

    ServicesAdapter(Context context, List<String> services) {
        this.services = services;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public ServicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_service, parent, false);
        return new ServicesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServicesAdapter.ViewHolder holder, int position) {
        String service = services.get(position);

        String nameService = service.substring(0,service.indexOf(":"));
        String price = service.substring(service.indexOf(":")+2);
        holder.nameView.setText(nameService);
        holder.priceView.setText(price);

    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, priceView;
        final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.textName);
            priceView = view.findViewById(R.id.textPrice);
            cardView = view.findViewById(R.id.card_view);
        }


    }
}
