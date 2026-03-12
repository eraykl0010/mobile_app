package com.pdks.mobile.patron;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdks.mobile.R;
import com.pdks.mobile.model.AdvanceRequest;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdvanceApprovalAdapter extends RecyclerView.Adapter<AdvanceApprovalAdapter.ViewHolder> {

    public interface OnActionListener {
        void onApprove(AdvanceRequest item, int position);
        void onReject(AdvanceRequest item, int position);
    }

    private List<AdvanceRequest> items = new ArrayList<>();
    private final OnActionListener listener;
    private final boolean showButtons;
    private final NumberFormat currencyFormat;

    public AdvanceApprovalAdapter(OnActionListener listener, boolean showButtons) {
        this.listener = listener;
        this.showButtons = showButtons;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));
    }

    public void setItems(List<AdvanceRequest> items) {
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
                .inflate(R.layout.item_advance_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdvanceRequest item = items.get(position);

        holder.tvName.setText(item.getPersonnelName());
        holder.tvDepartment.setText(item.getDepartment());
        holder.tvAmount.setText(currencyFormat.format(item.getAmount()));
        holder.tvDate.setText("Talep: " + item.getRequestDate());

        if (item.getReason() != null && !item.getReason().isEmpty()) {
            holder.tvReason.setVisibility(View.VISIBLE);
            holder.tvReason.setText(item.getReason());
        } else {
            holder.tvReason.setVisibility(View.GONE);
        }

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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDepartment, tvAmount, tvDate, tvReason;
        Button btnApprove, btnReject;

        ViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvAdvPersonnelName);
            tvDepartment = v.findViewById(R.id.tvAdvDepartment);
            tvAmount = v.findViewById(R.id.tvAdvAmount);
            tvDate = v.findViewById(R.id.tvAdvDate);
            tvReason = v.findViewById(R.id.tvAdvReason);
            btnApprove = v.findViewById(R.id.btnAdvApprove);
            btnReject = v.findViewById(R.id.btnAdvReject);
        }
    }
}