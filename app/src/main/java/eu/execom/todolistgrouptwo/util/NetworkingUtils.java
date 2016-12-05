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

            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Internet not available, you need to turn on your wifi");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));

                }
            });

            alertDialog.show();

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
