package com.poc.proxy.interceptors;

import com.poc.proxy.configuration.RequestMappingsTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;

import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = RequestMappingsTestConfiguration.class)
public class TargetPathRewriteInterceptorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private TargetPathRewriteInterceptor targetPathRewriteInterceptor;

    @Mock
    ClientHttpRequestExecution execution;

    @Captor
    ArgumentCaptor<HttpRequest> httpRequestArgumentCaptor;

    @BeforeMethod
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        Mockito.when(execution.execute(any(), any())).thenReturn(null);
    }

    @Test
    public void testThatTargetHostIsUpdatedInTheURL() throws IOException {
        URI requestUri = URI.create("http://xyz/test1");
        HttpRequest httpRequest = new TestHttpRequest(requestUri);

        targetPathRewriteInterceptor.intercept(httpRequest, null, execution);
        // TODO - used mockito for writing the test quickly, but Ideally we would want to
        //  have test stub classes for all the classes/behavior we want to verify. That would ensure
        // better testability, maintainability and readability.
        Mockito.verify(execution).execute(httpRequestArgumentCaptor.capture(), any());
        Assertions.assertEquals("http://xyz/rewrite1/path2", httpRequestArgumentCaptor.getValue().getURI().toString());
    }
}
