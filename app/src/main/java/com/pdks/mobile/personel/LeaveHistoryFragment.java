package com.pdks.mobile.personel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.model.LeaveRequest;
import com.pdks.mobile.util.DateSortHelper;
import com.pdks.mobile.util.SessionManager;

import java.util.List;

public class LeaveHistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private LeaveHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle saved) {
        View view = inflater.inflate(R.layout.fragment_leave_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        progressBar = view.findViewById(R.id.progressHistory);
        tvEmpty = view.findViewById(R.id.tvEmptyHistory);

        adapter = new LeaveHistoryAdapter();
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistory.setAdapter(adapter);

        loadHistory();
        return view;
    }

    private void loadHistory() {
        progressBar.setVisibility(View.VISIBLE);
        int personnelId = new SessionManager(requireContext()).getPersonnelId();
        ApiService api = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        api.getLeaveHistory(personnelId).enqueue(
                new BaseApiCallback<List<LeaveRequest>>(getContext()) {
                    @Override
                    public void onSuccess(@NonNull List<LeaveRequest> data) {
                        if (data.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            DateSortHelper.sortByDate(data, LeaveRequest::getRequestDate);
                            adapter.setItems(data);
                        }
                    }

                    @Override
                    public void onEmpty() {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinally() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }
}