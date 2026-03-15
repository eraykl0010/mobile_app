package com.pdks.mobile.patron;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pdks.mobile.R;
import com.pdks.mobile.constants.OvertimeType;
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

        boolean isOvertime = OvertimeType.OVERTIME.equals(item.getType());

        // Badge
        holder.tvBadge.setText(isOvertime
                ? holder.itemView.getContext().getString(R.string.badge_overtime)
                : holder.itemView.getContext().getString(R.string.badge_undertime));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setCornerRadius(8f);
        int badgeColor = ContextCompat.getColor(holder.itemView.getContext(),
                isOvertime ? R.color.status_success : R.color.status_early);
        badgeBg.setColor(badgeColor);
        holder.tvBadge.setBackground(badgeBg);

        // Fark
        String diffText = (isOvertime ? "+" : "-") + item.getDifferenceMinutes() + " dk";
        holder.tvDiff.setText(diffText);
        holder.tvDiff.setTextColor(badgeColor);
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