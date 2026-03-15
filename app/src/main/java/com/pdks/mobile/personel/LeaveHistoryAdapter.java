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
import com.pdks.mobile.constants.LeaveType;
import com.pdks.mobile.constants.RequestStatus;
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
        if (LeaveType.HOURLY.equals(item.getLeaveType())) {
            String date = safe(item.getStartDate());
            String startTime = safe(item.getStartTime(), "--:--");
            String endTime   = safe(item.getEndTime(), "--:--");
            holder.tvDates.setText(date + "  " + startTime + " - " + endTime);
        } else if (LeaveType.DAILY.equals(item.getLeaveType())) {
            holder.tvDates.setText(safe(item.getStartDate()));
        } else {
            holder.tvDates.setText(safe(item.getStartDate()) + " — " + safe(item.getEndDate()));
        }

        holder.tvRequestDate.setText(holder.itemView.getContext().getString(R.string.request_prefix, safe(item.getRequestDate())));

        String statusText;
        int statusColor;
        switch (item.getStatus() != null ? item.getStatus() : "") {
            case RequestStatus.APPROVED:
                statusText = holder.itemView.getContext().getString(R.string.status_approved);
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success);
                break;
            case RequestStatus.REJECTED:
                statusText = holder.itemView.getContext().getString(R.string.status_rejected);
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_danger);
                break;
            default:
                statusText = holder.itemView.getContext().getString(R.string.status_pending);
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_warning);
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