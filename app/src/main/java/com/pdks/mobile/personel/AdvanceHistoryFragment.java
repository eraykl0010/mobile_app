package com.pdks.mobile.personel;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.model.AdvanceRequest;
import com.pdks.mobile.util.DateSortHelper;
import com.pdks.mobile.util.SessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvanceHistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle saved) {
        View view = inflater.inflate(R.layout.fragment_leave_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        progressBar = view.findViewById(R.id.progressHistory);
        tvEmpty = view.findViewById(R.id.tvEmptyHistory);
        tvEmpty.setText("Henüz avans talebi bulunmuyor");

        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadHistory();

        return view;
    }

    private void loadHistory() {
        progressBar.setVisibility(View.VISIBLE);
        int personnelId = new SessionManager(requireContext()).getPersonnelId();
        ApiService api = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        api.getAdvanceHistory(personnelId).enqueue(new Callback<List<AdvanceRequest>>() {
            @Override
            public void onResponse(Call<List<AdvanceRequest>> call, Response<List<AdvanceRequest>> resp) {
                progressBar.setVisibility(View.GONE);
                if (resp.isSuccessful() && resp.body() != null) {
                    if (resp.body().isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        List<AdvanceRequest> list = resp.body();
                        DateSortHelper.sortByDate(list, AdvanceRequest::getRequestDate);
                        setupAdapter(list);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AdvanceRequest>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<AdvanceRequest> list) {
        NumberFormat cf = NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));

        rvHistory.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int vt) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_leave_history, parent, false);
                return new RecyclerView.ViewHolder(v) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
                AdvanceRequest item = list.get(pos);
                View v = holder.itemView;

                ((TextView) v.findViewById(R.id.tvHistLeaveType)).setText(cf.format(item.getAmount()));
                ((TextView) v.findViewById(R.id.tvHistDates)).setText(
                        item.getReason() != null ? item.getReason() : "");
                ((TextView) v.findViewById(R.id.tvHistRequestDate)).setText(
                        "Talep: " + (item.getRequestDate() != null ? item.getRequestDate() : "-"));

                // ═══ FIX: Null-safe status kontrolü ═══
                TextView tvStatus = v.findViewById(R.id.tvHistStatus);
                String status = item.getStatus(); // getStatus() null-safe, "" döner
                String statusText;
                int color;
                String safeStatus = (status != null) ? status.trim().toLowerCase() : "";
                switch (safeStatus) {
                    case "approved":
                        statusText = "Onaylandı";
                        color = Color.parseColor("#4CAF50");
                        break;
                    case "rejected":
                        statusText = "Reddedildi";
                        color = Color.parseColor("#F44336");
                        break;
                    case "pending": // JSON'da küçük 'pending' yolladığın için artık buraya girecek
                        statusText = "Bekliyor";
                        color = Color.parseColor("#FFC107");
                        break;
                    default:
                        statusText = "Bekliyor"; // Bilinmeyen bir durum gelirse yine sarı kalsın
                        color = Color.parseColor("#FFC107");
                        break;
                }
                tvStatus.setText(statusText);
                tvStatus.setTextColor(color);
                GradientDrawable bg = new GradientDrawable();
                bg.setCornerRadius(12f);
                bg.setColor(Color.argb(25, Color.red(color), Color.green(color), Color.blue(color)));
                tvStatus.setBackground(bg);
            }

            @Override
            public int getItemCount() { return list.size(); }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }
}