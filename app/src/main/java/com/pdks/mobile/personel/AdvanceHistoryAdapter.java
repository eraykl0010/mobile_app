package com.pdks.mobile.personel;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pdks.mobile.R;
import com.pdks.mobile.model.AdvanceRequest;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdvanceHistoryAdapter extends ListAdapter<AdvanceRequest, AdvanceHistoryAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<AdvanceRequest> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<AdvanceRequest>() {
                @Override
                public boolean areItemsTheSame(@NonNull AdvanceRequest oldItem,
                                               @NonNull AdvanceRequest newItem) {
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull AdvanceRequest oldItem,
                                                  @NonNull AdvanceRequest newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));

    public AdvanceHistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    /** Geriye dönük uyumlu setItems — artık DiffUtil üzerinden çalışır. */
    public void setItems(List<AdvanceRequest> items) {
        submitList(items != null ? new ArrayList<>(items) : null);
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
        AdvanceRequest item = getItem(position);

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