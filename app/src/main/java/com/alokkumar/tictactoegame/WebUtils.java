package com.alokkumar.tictactoegame;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;

public class WebUtils {

    // Method to check for internet connectivity
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    public static void openWebPage(Context context, View view, String url) {
        if (isInternetAvailable(context)) {
            // Internet is available, open the web page
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } else {
            Snackbar.make(view, "Internet not available", Snackbar.LENGTH_LONG).show();
        }
    }
}