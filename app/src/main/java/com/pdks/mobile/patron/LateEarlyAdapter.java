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
import com.pdks.mobile.model.LateEarlyRecord;

import java.util.ArrayList;
import java.util.List;

public class LateEarlyAdapter extends RecyclerView.Adapter<LateEarlyAdapter.ViewHolder> {

    private List<LateEarlyRecord> items = new ArrayList<>();

    public void setItems(List<LateEarlyRecord> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_late_early, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LateEarlyRecord item = items.get(position);

        holder.tvName.setText(item.getPersonnelName());
        holder.tvDepartment.setText(item.getDepartment());
        holder.tvTime.setText(item.getActualTime());

        boolean isOvertime = "overtime".equals(item.getType());

        // Badge
        holder.tvBadge.setText(isOvertime ? "FAZLA" : "EKSİK");
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setCornerRadius(8f);
        badgeBg.setColor(isOvertime ? Color.parseColor("#4CAF50") : Color.parseColor("#FF7043"));
        holder.tvBadge.setBackground(badgeBg);

        // Fark
        String diffText = (isOvertime ? "+" : "-") + item.getDifferenceMinutes() + " dk";
        holder.tvDiff.setText(diffText);
        holder.tvDiff.setTextColor(isOvertime ?
                Color.parseColor("#4CAF50") : Color.parseColor("#FF7043"));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBadge, tvName, tvDepartment, tvTime, tvDiff;

        ViewHolder(@NonNull View v) {
            super(v);
            tvBadge = v.findViewById(R.id.tvTypeBadge);
            tvName = v.findViewById(R.id.tvLeName);
            tvDepartment = v.findViewById(R.id.tvLeDepartment);
            tvTime = v.findViewById(R.id.tvLeTime);
            tvDiff = v.findViewById(R.id.tvLeDiff);
        }
    }
}