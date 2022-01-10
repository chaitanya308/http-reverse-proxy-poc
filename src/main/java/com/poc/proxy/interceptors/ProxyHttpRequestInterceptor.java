package com.poc.proxy.interceptors;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public abstract class ProxyHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    // TODO - Have implementations for HttpRequest and ClientHttpResponse, and make all the interceptors
    // in the application deal with these custom implementations so that manipulation of the request and response
    // is going to be easy. Interceptors are going to implement myIntercept() method.
    /*
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // transform request to MyHttpRequest, and pass it to the interceptors.
        MyHttpRequest httpRequest = request instanceof MyHttpRequest
                ? (MyHttpRequest) request
                : new MyHttpRequest(request, body);
        return myIntercept(myRequest, body, execution);
    }

    public abstract MyHttpResponse myIntercept(MyHttpRequest myRequest, byte[] body, ClientHttpRequestExecution execution) throws IOException;
    */
}
