package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.api.errorHandler.MyErrorHandler;
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

    @ViewById
    TextInputLayout inputError;

    @ViewById
    TextInputLayout passwordsError;
    @ViewById
    TextInputLayout passLengthError;

    @RestService
    RestApi restApi;

    @Bean
    MyErrorHandler myErrorHandler;

    private boolean emailValid = false;
    private boolean passwordValid = false;
    private boolean passwordsValid = false;

    @AfterInject
    void setUpErrorHandler(){
        restApi.setRestErrorHandler(myErrorHandler);
    }

    @EditorAction(R.id.password)
    @Click
    void register() {
        final String name = this.email.getText().toString();
        final String password = this.password.getText().toString();
        final String confirmPassword = this.confirmPassword.getText().toString();
        final UserRegister userRegister = new UserRegister(name, password, confirmPassword);

        if(emailValid && passwordsValid && passwordsValid) {
            registerUser(userRegister);
        }else{
            if(!emailValid){
                inputError.setErrorEnabled(true);
                inputError.setError("Enter valid email address");
            }
            if(!passwordValid) {
                passLengthError.setErrorEnabled(true);
                passLengthError.setError("Password must be between 6 and 20 characters");
            }
            if(!passwordsValid) {
                passwordsError.setErrorEnabled(true);
                passwordsError.setError("Passwords must match");
            }
        }
    }

    @Background
    void registerUser(UserRegister userRegister) {

        //final boolean userCreated = userDAOWrapper.create(user);


        try {
                restApi.register(userRegister);


                //login(tokenContainerDTO.getAccessToken());
                login();


            } catch (RestClientException e) {
                if (NetworkingUtils.isBadRequest(e)) {
                    showRegisterError();
                }
            HttpClientErrorException ee = (HttpClientErrorException)e;
            Log.e(TAG, ee.getLocalizedMessage(), ee);
            }



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
        Toast.makeText(this, "Registration failed, email already in use", Toast.LENGTH_SHORT).show();
    }

    @AfterTextChange(R.id.email)
    void emailError() {

        if(emailValid = isValidEmail(email.getText().toString())){
            inputError.setErrorEnabled(false);
        }
    }

    @AfterTextChange(R.id.password)
    void passError() {
        int length = password.getText().toString().length();
        if(passwordValid = length >= 6 && length<20) {
            passLengthError.setErrorEnabled(false);
        }
    }

    @AfterTextChange(R.id.confirmPassword)
    void passwordsError() {
        if(passwordsValid = password.getText().toString().equals(confirmPassword.getText().toString())) {
            passwordsError.setErrorEnabled(false);
        }
    }

    public final boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


}
