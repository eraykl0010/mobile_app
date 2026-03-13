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
import com.pdks.mobile.model.AttendanceRecord;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private List<AttendanceRecord> items = new ArrayList<>();

    public void setItems(List<AttendanceRecord> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceRecord item = items.get(position);

        // Tarihten gün numarasını çıkar — format: "dd.MM.yyyy"
        String date = item.getDate();
        if (date != null && date.contains(".")) {
            // "21.02.2026" → split(".") → ["21", "02", "2026"] → ilk eleman gün
            String day = date.split("\\.")[0];
            holder.tvDate.setText(day);
        } else if (date != null && date.length() >= 2) {
            holder.tvDate.setText(date.substring(0, 2));
        } else {
            holder.tvDate.setText("-");
        }
        holder.tvDay.setText(item.getDayName());

        holder.tvCheckIn.setText(item.getCheckIn() != null ? item.getCheckIn() : "--:--");
        holder.tvCheckOut.setText(item.getCheckOut() != null ? item.getCheckOut() : "--:--");

        String workInfo = "Çalışma: " + (item.getWorkHours() != null ? item.getWorkHours() : "-");
        if (item.getOvertimeHours() != null && !item.getOvertimeHours().equals("0")) {
            workInfo += " | Mesai: " + item.getOvertimeHours();
        }
        holder.tvWorkHours.setText(workInfo);

        // Durum badge — null-safe kontrol
        holder.tvStatus.setText(item.getStatusDisplay());
        String status = item.getStatus() != null ? item.getStatus() : "";
        int statusColor;
        switch (status) {
            case "late": statusColor = Color.parseColor("#FFC107"); break;
            case "early": statusColor = Color.parseColor("#FF7043"); break;
            case "absent": statusColor = Color.parseColor("#F44336"); break;
            case "leave": statusColor = Color.parseColor("#2196F3"); break;
            default: statusColor = Color.parseColor("#4CAF50"); break;
        }
        holder.tvStatus.setTextColor(statusColor);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(16f);
        bg.setColor(Color.argb(25,
                Color.red(statusColor), Color.green(statusColor), Color.blue(statusColor)));
        holder.tvStatus.setBackground(bg);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDay, tvCheckIn, tvCheckOut, tvWorkHours, tvStatus;

        ViewHolder(@NonNull View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvAttDate);
            tvDay = v.findViewById(R.id.tvAttDay);
            tvCheckIn = v.findViewById(R.id.tvCheckIn);
            tvCheckOut = v.findViewById(R.id.tvCheckOut);
            tvWorkHours = v.findViewById(R.id.tvWorkHours);
            tvStatus = v.findViewById(R.id.tvAttStatus);
        }
    }
}