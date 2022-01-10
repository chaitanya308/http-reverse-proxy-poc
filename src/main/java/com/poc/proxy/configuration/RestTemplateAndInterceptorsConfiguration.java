package com.poc.proxy.configuration;

import com.poc.proxy.forward.RequestMappingResolver;
import com.poc.proxy.interceptors.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Import({RequestMappingsConfiguration.class,
        MetricInterceptorsConfiguration.class})
@Configuration
public class RestTemplateAndInterceptorsConfiguration {

    // Create all the interceptors
    @Bean
    public List<ProxyHttpRequestInterceptor> interceptorList(TargetHostRoutingInterceptor targetHostRoutingInterceptor,
                                                             TargetPathRewriteInterceptor targetPathRewriteInterceptor,
                                                             StatusCodeMetricInterceptor statusCodeMetricInterceptor,
                                                             LatencyMetricInterceptor latencyMetricInterceptor) {
        List<ProxyHttpRequestInterceptor> interceptorsList = new ArrayList<>();

        // Response time logging interceptor
        interceptorsList.add(latencyMetricInterceptor);

        // Status code metric interceptor
        interceptorsList.add(statusCodeMetricInterceptor);

        // path rewrite interceptor
        interceptorsList.add(targetPathRewriteInterceptor);

        // Route host url rewrite interceptor
        interceptorsList.add(targetHostRoutingInterceptor);

        return interceptorsList;
    }

    @Bean
    public TargetHostRoutingInterceptor targetHostRoutingInterceptor(RequestMappingResolver requestMappingResolver) {
        return new TargetHostRoutingInterceptor(requestMappingResolver);
    }

    @Bean
    public TargetPathRewriteInterceptor targetPathRewriteInterceptor(RequestMappingResolver requestMappingResolver) {
        return new TargetPathRewriteInterceptor(requestMappingResolver);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
                                     List<ProxyHttpRequestInterceptor> interceptorList) {
        RestTemplate restTemplate = restTemplateBuilder
                .requestFactory(this::createHttpRequestFactory)
                .additionalInterceptors(interceptorList)
                .build();
        return restTemplate;
    }

 /*   private ClientHttpRequestFactory createHttpRequestFactory() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();

        OkHttp3ClientHttpRequestFactory requestFactory = new OkHttp3ClientHttpRequestFactory(httpClient);
        // TODO - make these timeouts configurable, and take them as external properties
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);
        requestFactory.setWriteTimeout(500);
        return requestFactory;
    }
*/


    private ClientHttpRequestFactory createHttpRequestFactory() {
        int timeout = 1000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        CloseableHttpClient client = HttpClientBuilder
                .create()
                //.setDefaultRequestConfig(config)
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }

  /*  private ClientHttpRequestFactory createHttpRequestFactory() {
        return new FollowRedirectsCommonsClientHttpRequestFactory();
    }

    public class FollowRedirectsCommonsClientHttpRequestFactory extends CommonsClientHttpRequestFactory {

        @Override
        protected void postProcessCommonsHttpMethod(HttpMethodBase httpMethod) {
            httpMethod.setFollowRedirects(true);
        }
    }*/
}
