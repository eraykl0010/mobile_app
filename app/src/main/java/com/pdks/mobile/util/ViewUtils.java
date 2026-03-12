package com.pdks.mobile.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewUtils {

    /**
     * statusBarSpacer id'li View'ı bulur ve yüksekliğini
     * status bar yüksekliğine ayarlar.
     *
     * Bu metodu her Activity'nin onCreate'inde, setContentView'dan sonra çağır.
     * Layout'ta fitsSystemWindows KULLANMA — bu metot onu halleder.
     */
    public static void applyStatusBarPadding(Activity activity) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);

        int spacerId = activity.getResources().getIdentifier(
                "statusBarSpacer", "id", activity.getPackageName());

        if (spacerId == 0) return;

        View spacer = activity.findViewById(spacerId);
        if (spacer == null) return;

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            ViewGroup.LayoutParams params = spacer.getLayoutParams();
            params.height = insets.top;
            spacer.setLayoutParams(params);
            return windowInsets;
        });
    }
}