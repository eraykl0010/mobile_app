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
import com.pdks.mobile.model.LeaveRequest;

import java.util.ArrayList;
import java.util.List;

public class LeaveHistoryAdapter extends ListAdapter<LeaveRequest, LeaveHistoryAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<LeaveRequest> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<LeaveRequest>() {
                @Override
                public boolean areItemsTheSame(@NonNull LeaveRequest oldItem,
                                               @NonNull LeaveRequest newItem) {
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull LeaveRequest oldItem,
                                                  @NonNull LeaveRequest newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public LeaveHistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    /** Geriye dönük uyumlu setItems — artık DiffUtil üzerinden çalışır. */
    public void setItems(List<LeaveRequest> items) {
        submitList(items != null ? new ArrayList<>(items) : null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leave_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaveRequest item = getItem(position);

        holder.tvType.setText(item.getLeaveTypeDisplay());

        // ── Tarih / Saat bilgisi ──
        if ("saatlik".equals(item.getLeaveType())) {
            String date = safe(item.getStartDate());
            String startTime = safe(item.getStartTime(), "--:--");
            String endTime   = safe(item.getEndTime(), "--:--");
            holder.tvDates.setText(date + "  " + startTime + " - " + endTime);
        } else if ("gunluk".equals(item.getLeaveType())) {
            holder.tvDates.setText(safe(item.getStartDate()));
        } else {
            holder.tvDates.setText(safe(item.getStartDate()) + " — " + safe(item.getEndDate()));
        }

        holder.tvRequestDate.setText("Talep: " + safe(item.getRequestDate()));

        String statusText;
        int statusColor;
        switch (item.getStatus() != null ? item.getStatus() : "") {
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

    private String safe(String value) {
        return (value != null && !value.isEmpty()) ? value : "-";
    }

    private String safe(String value, String fallback) {
        return (value != null && !value.isEmpty()) ? value : fallback;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDates, tvRequestDate, tvStatus;

        ViewHolder(@NonNull View v) {
            super(v);
            tvType        = v.findViewById(R.id.tvHistLeaveType);
            tvDates       = v.findViewById(R.id.tvHistDates);
            tvRequestDate = v.findViewById(R.id.tvHistRequestDate);
            tvStatus      = v.findViewById(R.id.tvHistStatus);
        }
    }
}