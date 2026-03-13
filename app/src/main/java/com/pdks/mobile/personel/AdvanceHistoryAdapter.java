package com.pdks.mobile.personel;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdks.mobile.R;
import com.pdks.mobile.model.AdvanceRequest;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdvanceHistoryAdapter extends RecyclerView.Adapter<AdvanceHistoryAdapter.ViewHolder> {

    private List<AdvanceRequest> items = new ArrayList<>();
    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));

    public void setItems(List<AdvanceRequest> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_advance_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdvanceRequest item = items.get(position);

        // Tutar
        holder.tvAmount.setText(currencyFormat.format(item.getAmount()));

        // Açıklama
        holder.tvReason.setText(
                item.getReason() != null && !item.getReason().isEmpty()
                        ? item.getReason() : "-");

        // Talep tarihi
        holder.tvDate.setText("Talep: " +
                (item.getRequestDate() != null ? item.getRequestDate() : "-"));

        // Durum badge
        String status = item.getStatus();
        String safeStatus = (status != null) ? status.trim().toLowerCase() : "";

        String statusText;
        int statusColor;
        switch (safeStatus) {
            case "approved":
                statusText = "Onaylandı";
                statusColor = Color.parseColor("#4CAF50");
                break;
            case "rejected":
                statusText = "Reddedildi";
                statusColor = Color.parseColor("#F44336");
                break;
            default:
                statusText = "Bekliyor";
                statusColor = Color.parseColor("#FFC107");
                break;
        }

        holder.tvStatus.setText(statusText);
        holder.tvStatus.setTextColor(statusColor);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(12f);
        bg.setColor(Color.argb(25,
                Color.red(statusColor), Color.green(statusColor), Color.blue(statusColor)));
        holder.tvStatus.setBackground(bg);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvReason, tvDate, tvStatus;

        ViewHolder(@NonNull View v) {
            super(v);
            tvAmount = v.findViewById(R.id.tvAdvHistAmount);
            tvReason = v.findViewById(R.id.tvAdvHistReason);
            tvDate   = v.findViewById(R.id.tvAdvHistDate);
            tvStatus = v.findViewById(R.id.tvAdvHistStatus);
        }
    }
}