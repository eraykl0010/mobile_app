package com.pdks.mobile.patron;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdks.mobile.R;
import com.pdks.mobile.model.PersonnelInfo;

import java.util.ArrayList;
import java.util.List;

public class PersonnelListAdapter extends RecyclerView.Adapter<PersonnelListAdapter.ViewHolder> {

    // ── Long-press callback ──
    public interface OnItemLongClickListener {
        void onItemLongClick(PersonnelInfo item, int position);
    }

    private List<PersonnelInfo> allItems = new ArrayList<>();
    private List<PersonnelInfo> filteredItems = new ArrayList<>();
    private String currentDepartment = null;
    private String currentStatus = null;
    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setItems(List<PersonnelInfo> items) {
        // Patron'u listeden çıkar (SQL zaten filtreliyor, bu ekstra güvenlik)
        this.allItems = new ArrayList<>();
        for (PersonnelInfo p : items) {
            if (!p.isPatron()) {
                this.allItems.add(p);
            }
        }
        applyFilters();
    }

    public void filterByDepartment(String departmentName) {
        this.currentDepartment = departmentName;
        this.currentStatus = null;
        applyFilters();
    }

    public void filterByStatus(String status) {
        this.currentStatus = status;
        applyFilters();
    }

    public void clearStatusFilter() {
        this.currentStatus = null;
        applyFilters();
    }

    private void applyFilters() {
        filteredItems = new ArrayList<>();
        for (PersonnelInfo p : allItems) {
            boolean deptMatch = (currentDepartment == null || currentDepartment.isEmpty()
                    || currentDepartment.equals(p.getDepartment()));
            boolean statusMatch = (currentStatus == null || currentStatus.isEmpty()
                    || currentStatus.equals(p.getStatus()));

            if (deptMatch && statusMatch) {
                filteredItems.add(p);
            }
        }
        notifyDataSetChanged();
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_personnel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        PersonnelInfo item = filteredItems.get(position);

        h.tvName.setText(item.getName());
        h.tvDept.setText(item.getDepartment());
        h.tvCheckIn.setText("G: " + (item.getCheckIn() != null ? item.getCheckIn() : "--:--"));
        h.tvCheckOut.setText("Ç: " + (item.getCheckOut() != null ? item.getCheckOut() : "--:--"));
        h.tvStatus.setText(item.getStatusDisplay());

        int color;
        switch (item.getStatus() != null ? item.getStatus() : "") {
            case "active":    color = Color.parseColor("#4CAF50"); break;
            case "late":      color = Color.parseColor("#FFC107"); break;
            case "early":     color = Color.parseColor("#FF7043"); break;
            case "on_leave":  color = Color.parseColor("#2196F3"); break;
            case "absent":    color = Color.parseColor("#F44336"); break;
            case "no_record": color = Color.parseColor("#9E9E9E"); break;
            default:          color = Color.parseColor("#9E9E9E"); break;
        }

        h.viewIndicator.setBackgroundColor(color);
        h.tvStatus.setTextColor(color);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(12f);
        bg.setColor(Color.argb(25, Color.red(color), Color.green(color), Color.blue(color)));
        h.tvStatus.setBackground(bg);

        // ── Long-press: Cihaz sıfırlama seçeneği ──
        h.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(item, h.getAdapterPosition());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() { return filteredItems.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewIndicator;
        TextView tvName, tvDept, tvCheckIn, tvCheckOut, tvStatus;

        ViewHolder(@NonNull View v) {
            super(v);
            viewIndicator = v.findViewById(R.id.viewStatusIndicator);
            tvName = v.findViewById(R.id.tvPersName);
            tvDept = v.findViewById(R.id.tvPersDept);
            tvCheckIn = v.findViewById(R.id.tvPersCheckIn);
            tvCheckOut = v.findViewById(R.id.tvPersCheckOut);
            tvStatus = v.findViewById(R.id.tvPersStatus);
        }
    }
}