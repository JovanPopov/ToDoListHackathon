package eu.execom.todolistgrouptwo.api.errorHandler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.api.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.activity.HomeActivity_;
import eu.execom.todolistgrouptwo.activity.LoginActivity_;
import eu.execom.todolistgrouptwo.preference.UserPreferences_;
import eu.execom.todolistgrouptwo.util.NetworkingUtils;


@EBean
public class MyErrorHandler implements RestErrorHandler {

    public static final String TAG = MyErrorHandler.class.getSimpleName();

    @Pref
    UserPreferences_ userPreferences;

    @RootContext
    Context context;

    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        Log.e(TAG, e.getMessage(), e);
        check();

        //Toast.makeText(context, "Network problem", Toast.LENGTH_SHORT).show();


              if (e instanceof HttpClientErrorException) {
            HttpClientErrorException exception = (HttpClientErrorException) e;

            if (exception.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                if (!userPreferences.accessToken().exists()) {
                    userPreferences.accessToken().remove();
                }
                LoginActivity_.intent(context).startForResult(HomeActivity_.LOGIN_REQUEST_CODE);

            } else if (exception.getStatusCode().equals(HttpStatus.BAD_GATEWAY)) {
            }
        }

        throw e;
    }

    @UiThread
    public void check(){
        NetworkingUtils.checkForConnection(context);
    }

    @UiThread
    public void unautorized(){
        LoginActivity_.intent(context).startForResult(HomeActivity_.LOGIN_REQUEST_CODE);
    }

}
