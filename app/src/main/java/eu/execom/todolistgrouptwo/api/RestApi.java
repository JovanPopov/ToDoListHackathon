package eu.execom.todolistgrouptwo.api;

import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Header;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import eu.execom.todolistgrouptwo.api.interceptor.AuthenticationInterceptor;
import eu.execom.todolistgrouptwo.constant.ApiConstants;
import eu.execom.todolistgrouptwo.model.Task;
import eu.execom.todolistgrouptwo.model.UserRegister;
import eu.execom.todolistgrouptwo.model.dto.TokenContainerDTO;


@Rest(rootUrl = ApiConstants.ROOT_URL, converters= {GsonHttpMessageConverter.class, FormHttpMessageConverter.class},
interceptors = AuthenticationInterceptor.class)
public interface RestApi extends RestClientErrorHandling {

    @Header(name = "Content-Type", value = "application/x-www-form-urlencoded")
    @Post(value = ApiConstants.LOGIN_PATH)
    TokenContainerDTO login(@Body LinkedMultiValueMap<String, String> accountInfo);

    @Get(value = ApiConstants.TASK_PATH)
    List<Task> getAallTasks();

    @Post(value = ApiConstants.TASK_PATH)
    Task createTask(@Body Task task);

    @Post(value = ApiConstants.REGISTER_PATH)
    void register(@Body UserRegister user);

    @Post(value = ApiConstants.LOGOUT_PATH)
    void logout();

    @Get(value = ApiConstants.TASK_PATH + "/{id}")
    Task getTask(@Path int id);

    @Put(value = ApiConstants.TASK_PATH + "/{id}")
    Task updateTask(@Path int id, @Body Task task);
}
