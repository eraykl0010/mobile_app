package com.pdks.mobile.patron;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdks.mobile.R;
import com.pdks.mobile.model.LeaveRequest;

import java.util.ArrayList;
import java.util.List;

public class LeaveApprovalAdapter extends RecyclerView.Adapter<LeaveApprovalAdapter.ViewHolder> {

    public interface OnActionListener {
        void onApprove(LeaveRequest item, int position);
        void onReject(LeaveRequest item, int position);
    }

    private List<LeaveRequest> items = new ArrayList<>();
    private final OnActionListener listener;
    private final boolean showButtons;

    public LeaveApprovalAdapter(OnActionListener listener, boolean showButtons) {
        this.listener = listener;
        this.showButtons = showButtons;
    }

    public void setItems(List<LeaveRequest> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leave_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaveRequest item = items.get(position);

        holder.tvPersonnelName.setText(item.getPersonnelName());

        String leaveType = item.getLeaveType();
        boolean isSaatlik = "saatlik".equals(leaveType);
        boolean isGunluk  = "gunluk".equals(leaveType);

        holder.tvLeaveType.setText(item.getLeaveTypeDisplay());

        // ── Departman satırı ──
        if (isSaatlik) {
            // Saatlik izinde departman + tarih birlikte gösterilir
            String dateInfo = safe(item.getStartDate());
            holder.tvDepartment.setText(item.getDepartment() + " • " + dateInfo);
        } else {
            holder.tvDepartment.setText(item.getDepartment());
        }

        // ── Etiketler + Değerler (RecyclerView recycling güvenliği) ──
        if (isSaatlik) {
            // Saatlik izin: tarih alanlarında SAAT göster, etiketleri değiştir
            holder.tvLabelStart.setText("Başlangıç Saati");
            holder.tvLabelEnd.setText("Bitiş Saati");
            holder.tvStartDate.setText(safe(item.getStartTime(), "-"));
            holder.tvEndDate.setText(safe(item.getEndTime(), "-"));
        } else if (isGunluk) {
            // Günlük izin: tek tarih
            holder.tvLabelStart.setText("Tarih");
            holder.tvLabelEnd.setText("Bitiş");
            holder.tvStartDate.setText(safe(item.getStartDate()));
            holder.tvEndDate.setText(safe(item.getStartDate()));
        } else {
            // Yıllık izin: başlangıç - bitiş tarihi
            holder.tvLabelStart.setText("Başlangıç");
            holder.tvLabelEnd.setText("Bitiş");
            holder.tvStartDate.setText(safe(item.getStartDate()));
            holder.tvEndDate.setText(safe(item.getEndDate()));
        }

        // ── Kalan Hak ──
        if (isSaatlik || isGunluk) {
            holder.layoutRemainingDays.setVisibility(View.GONE);
        } else {
            holder.layoutRemainingDays.setVisibility(View.VISIBLE);
            holder.tvRemainingDays.setText(String.format("%.0f gün", item.getRemainingDays()));
        }

        // ── Açıklama ──
        String reason = item.getReason();
        if (reason != null && !reason.isEmpty() && !"-".equals(reason)) {
            holder.tvReason.setVisibility(View.VISIBLE);
            holder.tvReason.setText(reason);
        } else {
            holder.tvReason.setVisibility(View.GONE);
        }

        // ── Butonlar (sadece "Bekleyen" tabında) ──
        if (showButtons) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.btnApprove.setOnClickListener(v ->
                    listener.onApprove(item, holder.getAdapterPosition()));
            holder.btnReject.setOnClickListener(v ->
                    listener.onReject(item, holder.getAdapterPosition()));
        } else {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    /** Null/boş string koruması — boş yerine fallback döner */
    private String safe(String value) {
        return (value != null && !value.isEmpty()) ? value : "-";
    }

    private String safe(String value, String fallback) {
        return (value != null && !value.isEmpty()) ? value : fallback;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPersonnelName, tvDepartment, tvLeaveType;
        TextView tvStartDate, tvEndDate, tvRemainingDays, tvReason;
        TextView tvLabelStart, tvLabelEnd;
        LinearLayout layoutRemainingDays;
        Button btnApprove, btnReject;

        ViewHolder(@NonNull View v) {
            super(v);
            tvPersonnelName   = v.findViewById(R.id.tvPersonnelName);
            tvDepartment      = v.findViewById(R.id.tvDepartment);
            tvLeaveType       = v.findViewById(R.id.tvLeaveType);
            tvStartDate       = v.findViewById(R.id.tvStartDate);
            tvEndDate         = v.findViewById(R.id.tvEndDate);
            tvRemainingDays   = v.findViewById(R.id.tvRemainingDays);
            tvLabelStart      = v.findViewById(R.id.tvLabelStart);
            tvLabelEnd        = v.findViewById(R.id.tvLabelEnd);
            layoutRemainingDays = v.findViewById(R.id.layoutRemainingDays);
            tvReason          = v.findViewById(R.id.tvReason);
            btnApprove        = v.findViewById(R.id.btnApprove);
            btnReject         = v.findViewById(R.id.btnReject);
        }
    }
}