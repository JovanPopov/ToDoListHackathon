package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.database.wrapper.UserDAOWrapper;
import eu.execom.todolistgrouptwo.model.User;
import eu.execom.todolistgrouptwo.model.UserRegister;
import eu.execom.todolistgrouptwo.model.dto.TokenContainerDTO;
import eu.execom.todolistgrouptwo.util.NetworkingUtils;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = RegisterActivity.class.getSimpleName();
    @Bean
    UserDAOWrapper userDAOWrapper;

    @ViewById
    EditText email;

    @ViewById
    EditText password;

    @ViewById
    EditText confirmPassword;

    @RestService
    RestApi restApi;

    @EditorAction(R.id.password)
    @Click
    void register() {
        final String name = this.email.getText().toString();
        final String password = this.password.getText().toString();
        final String confirmPassword = this.confirmPassword.getText().toString();
        final UserRegister userRegister = new UserRegister(name, password, confirmPassword);

        registerUser(userRegister);
    }

    @Background
    void registerUser(UserRegister userRegister) {

        //final boolean userCreated = userDAOWrapper.create(user);


        try {
              restApi.register(userRegister);


            //login(tokenContainerDTO.getAccessToken());
            login();


        } catch (Exception e) {
            showRegisterError();
            Log.e(TAG, e.getMessage(),e);
        }


       /* if (userCreated) {
            login(user);
        } else {
            showRegisterError();
        }*/
    }

    @UiThread
    void login() {
        final Intent intent = new Intent();
        //intent.putExtra("token", accessToken);

        setResult(RESULT_OK, intent);
        finish();
    }

    @UiThread
    void showRegisterError() {
        email.setError("Email already exists.");
    }

}
