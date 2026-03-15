package com.pdks.mobile.patron;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.model.AdvanceRequest;
import com.pdks.mobile.model.ApiResponse;
import com.pdks.mobile.model.ApprovalRequest;
import com.pdks.mobile.model.LeaveRequest;
import com.pdks.mobile.util.DateSortHelper;
import com.pdks.mobile.util.ViewUtils;

import java.util.List;

import retrofit2.Call;

public class ApprovalListActivity extends AppCompatActivity {

    private RecyclerView rvApprovals;
    private View progressBar;
    private TextView tvEmpty;
    private TabLayout tabStatus;
    private ApiService apiService;

    private String type;           // "yillik", "saatlik", "avans"
    private String currentStatus;  // "pending", "approved", "rejected"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_list);

        type = getIntent().getStringExtra("type");
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Toolbar
        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        TextView tvTitle = findViewById(R.id.tvToolbarTitle);

        switch (type) {
            case "yillik":  tvTitle.setText("Yıllık İzin Talepleri"); break;
            case "gunluk":  tvTitle.setText("Günlük İzin Talepleri"); break;
            case "saatlik": tvTitle.setText("Saatlik İzin Talepleri"); break;
            case "avans":   tvTitle.setText("Avans Talepleri"); break;
        }

        rvApprovals = findViewById(R.id.rvApprovals);
        progressBar = findViewById(R.id.progressApproval);
        tvEmpty = findViewById(R.id.tvEmptyApproval);
        tabStatus = findViewById(R.id.tabApprovalStatus);

        rvApprovals.setLayoutManager(new LinearLayoutManager(this));

        // 3 Tab
        tabStatus.addTab(tabStatus.newTab().setText("Bekleyen"));
        tabStatus.addTab(tabStatus.newTab().setText("Onaylanan"));
        tabStatus.addTab(tabStatus.newTab().setText("Reddedilen"));

        tabStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentStatus = "pending"; break;
                    case 1: currentStatus = "approved"; break;
                    case 2: currentStatus = "rejected"; break;
                }
                loadData();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        currentStatus = "pending";
        loadData();
        ViewUtils.applyStatusBarPadding(this);
    }

    private void loadData() {
        if ("avans".equals(type)) {
            loadAdvanceRequests();
        } else {
            loadLeaveRequests();
        }
    }

    // ── İzin Talepleri ──

    private void loadLeaveRequests() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvApprovals.setAdapter(null);

        apiService.getPendingLeaveRequests(type, currentStatus)
                .enqueue(new BaseApiCallback<List<LeaveRequest>>(this) {
                    @Override
                    public void onSuccess(@NonNull List<LeaveRequest> data) {
                        if (data.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            setEmptyMessage();
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            DateSortHelper.sortByDate(data, LeaveRequest::getRequestDate);
                            setupLeaveAdapter(data);
                        }
                    }

                    @Override
                    public void onFinally() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void setupLeaveAdapter(List<LeaveRequest> list) {
        boolean showButtons = "pending".equals(currentStatus);

        LeaveApprovalAdapter adapter = new LeaveApprovalAdapter(
                new LeaveApprovalAdapter.OnActionListener() {
                    @Override
                    public void onApprove(LeaveRequest item, int position) {
                        showConfirmDialog("Onay", "Bu izin talebini onaylamak istiyor musunuz?",
                                () -> sendApproval(String.valueOf(item.getId()), "leave", "approve", position));
                    }
                    @Override
                    public void onReject(LeaveRequest item, int position) {
                        showConfirmDialog("Red", "Bu izin talebini reddetmek istiyor musunuz?",
                                () -> sendApproval(String.valueOf(item.getId()), "leave", "reject", position));
                    }
                }, showButtons);
        adapter.setItems(list);
        rvApprovals.setAdapter(adapter);
    }

    // ── Avans Talepleri ──

    private void loadAdvanceRequests() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvApprovals.setAdapter(null);

        apiService.getPendingAdvanceRequests(currentStatus)
                .enqueue(new BaseApiCallback<List<AdvanceRequest>>(this) {
                    @Override
                    public void onSuccess(@NonNull List<AdvanceRequest> data) {
                        if (data.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            setEmptyMessage();
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            DateSortHelper.sortByDate(data, AdvanceRequest::getRequestDate);
                            setupAdvanceAdapter(data);
                        }
                    }

                    @Override
                    public void onFinally() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void setupAdvanceAdapter(List<AdvanceRequest> list) {
        boolean showButtons = "pending".equals(currentStatus);

        AdvanceApprovalAdapter adapter = new AdvanceApprovalAdapter(
                new AdvanceApprovalAdapter.OnActionListener() {
                    @Override
                    public void onApprove(AdvanceRequest item, int position) {
                        showConfirmDialog("Onay", "Bu avans talebini onaylamak istiyor musunuz?",
                                () -> sendApproval(item.getId(), "advance", "approve", position));
                    }
                    @Override
                    public void onReject(AdvanceRequest item, int position) {
                        showConfirmDialog("Red", "Bu avans talebini reddetmek istiyor musunuz?",
                                () -> sendApproval(item.getId(), "advance", "reject", position));
                    }
                }, showButtons);
        adapter.setItems(list);
        rvApprovals.setAdapter(adapter);
    }

    // ── Onay/Red İşlemi (String ID — leave: "5", advance: "EVR000023") ──

    private void sendApproval(String requestId, String reqType, String action, int position) {
        ApprovalRequest req = new ApprovalRequest(requestId, reqType, action, null);

        Call<ApiResponse> call = "approve".equals(action)
                ? apiService.approveRequest(req)
                : apiService.rejectRequest(req);

        call.enqueue(new BaseApiCallback<ApiResponse>(this) {
            @Override
            public void onSuccess(@NonNull ApiResponse data) {
                if (data.isSuccess()) {
                    String msg = "approve".equals(action) ? "Onaylandı" : "Reddedildi";
                    Toast.makeText(ApprovalListActivity.this, msg, Toast.LENGTH_SHORT).show();

                    RecyclerView.Adapter<?> adapter = rvApprovals.getAdapter();
                    if (adapter instanceof LeaveApprovalAdapter) {
                        ((LeaveApprovalAdapter) adapter).removeItem(position);
                    } else if (adapter instanceof AdvanceApprovalAdapter) {
                        ((AdvanceApprovalAdapter) adapter).removeItem(position);
                    }
                    if (adapter != null && adapter.getItemCount() == 0) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        setEmptyMessage();
                    }
                }
            }
        });
    }

    private void setEmptyMessage() {
        switch (currentStatus) {
            case "pending":  tvEmpty.setText("Bekleyen talep bulunmuyor"); break;
            case "approved": tvEmpty.setText("Onaylanan talep bulunmuyor"); break;
            case "rejected": tvEmpty.setText("Reddedilen talep bulunmuyor"); break;
        }
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Evet", (d, w) -> onConfirm.run())
                .setNegativeButton("İptal", null)
                .show();
    }
}