package eu.execom.todolistgrouptwo.util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import eu.execom.todolistgrouptwo.R;


public class NetworkingUtils {

    public static LinkedMultiValueMap<String, String> packUserCredentials(String email, String password){
        final LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.set("grant_type", "password");
        map.set("username", email);
        map.set("password", password);

        return map;
    }

    public static boolean checkForConnection(final Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean result = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (!result) {

            AlertDialog.Builder alert = new AlertDialog.Builder(
                    context);
            alert.setTitle(R.string.info);
            alert.setMessage(R.string.wifi_problem);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            alert.show();

        }

        return result;
    }

    public static boolean isBadRequest(RestClientException e){
        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException exception = (HttpClientErrorException) e;

            if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
               return true;
            }
        }
        return false;
    }
}
