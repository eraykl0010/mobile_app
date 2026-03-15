package com.pdks.mobile.patron;

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
import com.pdks.mobile.constants.PersonnelStatus;
import com.pdks.mobile.model.PersonnelInfo;

import java.util.ArrayList;
import java.util.List;

public class PersonnelListAdapter extends ListAdapter<PersonnelInfo, PersonnelListAdapter.ViewHolder> {

    // ── Long-press callback ──
    public interface OnItemLongClickListener {
        void onItemLongClick(PersonnelInfo item, int position);
    }

    private static final DiffUtil.ItemCallback<PersonnelInfo> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PersonnelInfo>() {
                @Override
                public boolean areItemsTheSame(@NonNull PersonnelInfo oldItem,
                                               @NonNull PersonnelInfo newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull PersonnelInfo oldItem,
                                                  @NonNull PersonnelInfo newItem) {
                    return oldItem.equals(newItem);
                }
            };

    /** Filtrelenmemiş tam liste — filtreleme bu kaynak üzerinden yapılır. */
    private List<PersonnelInfo> allItems = new ArrayList<>();
    private String currentDepartment = null;
    private String currentStatus = null;
    private OnItemLongClickListener longClickListener;

    public PersonnelListAdapter() {
        super(DIFF_CALLBACK);
    }

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
        List<PersonnelInfo> filtered = new ArrayList<>();
        for (PersonnelInfo p : allItems) {
            boolean deptMatch = (currentDepartment == null || currentDepartment.isEmpty()
                    || currentDepartment.equals(p.getDepartment()));
            boolean statusMatch = (currentStatus == null || currentStatus.isEmpty()
                    || currentStatus.equals(p.getStatus()));

            if (deptMatch && statusMatch) {
                filtered.add(p);
            }
        }
        submitList(filtered);
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
        PersonnelInfo item = getItem(position);

        h.tvName.setText(item.getName());
        h.tvDept.setText(item.getDepartment());
        String checkIn = item.getCheckIn() != null ? item.getCheckIn() : h.itemView.getContext().getString(R.string.label_no_time);
        String checkOut = item.getCheckOut() != null ? item.getCheckOut() : h.itemView.getContext().getString(R.string.label_no_time);
        h.tvCheckIn.setText(h.itemView.getContext().getString(R.string.label_checkin, checkIn));
        h.tvCheckOut.setText(h.itemView.getContext().getString(R.string.label_checkout, checkOut));
        h.tvStatus.setText(item.getStatusDisplay());

        int color;
        switch (item.getStatus() != null ? item.getStatus() : "") {
            case PersonnelStatus.ACTIVE:    color = ContextCompat.getColor(h.itemView.getContext(), R.color.status_success); break;
            case PersonnelStatus.LATE:      color = ContextCompat.getColor(h.itemView.getContext(), R.color.status_warning); break;
            case PersonnelStatus.EARLY:     color = ContextCompat.getColor(h.itemView.getContext(), R.color.status_early); break;
            case PersonnelStatus.ON_LEAVE:  color = ContextCompat.getColor(h.itemView.getContext(), R.color.status_info); break;
            case PersonnelStatus.ABSENT:    color = ContextCompat.getColor(h.itemView.getContext(), R.color.status_danger); break;
            case PersonnelStatus.NO_RECORD: color = ContextCompat.getColor(h.itemView.getContext(), R.color.status_neutral); break;
            default:                        color = ContextCompat.getColor(h.itemView.getContext(), R.color.status_neutral); break;
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