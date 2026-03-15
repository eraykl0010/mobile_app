package com.pdks.mobile.personel;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pdks.mobile.R;
import com.pdks.mobile.constants.RequestStatus;
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
        android.content.Context ctx = holder.itemView.getContext();
        holder.tvDate.setText(ctx.getString(R.string.request_prefix,
                item.getRequestDate() != null ? item.getRequestDate() : "-"));

        // Durum badge
        String status = item.getStatus();
        String safeStatus = (status != null) ? status.trim().toLowerCase() : "";

        String statusText;
        int statusColor;
        switch (safeStatus) {
            case RequestStatus.APPROVED:
                statusText = ctx.getString(R.string.status_approved);
                statusColor = ContextCompat.getColor(ctx, R.color.status_success);
                break;
            case RequestStatus.REJECTED:
                statusText = ctx.getString(R.string.status_rejected);
                statusColor = ContextCompat.getColor(ctx, R.color.status_danger);
                break;
            default:
                statusText = ctx.getString(R.string.status_pending);
                statusColor = ContextCompat.getColor(ctx, R.color.status_warning);
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