package org.bjason.taskdrag.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.ArrayList;

public class AuthHelper implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthHelper.class);

    String token;
    static String csrf;

    public AuthHelper(String token) {
        this.token = token;
        log.debug("create AuthHelper" + token);
    }
    public AuthHelper(String token,String csrf) {
        this.token = token;
        AuthHelper.csrf = csrf;
        log.debug("create AuthHelper " + token+" "+csrf);
    }

    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        if (!headers.containsKey("Cookie")) {
            request.getHeaders().add("Cookie", "JSESSIONID=" + this.token + "; Path=/; HttpOnly");
        } else {
            ArrayList<String> cookies = new ArrayList<>(request.getHeaders().get("Cookie"));
            String first = cookies.get(0);
            first = "JSESSIONID=" + this.token + ";" + first;
            cookies.set(0, first);
        }

        if ( request.getMethod().matches("POST") || request.getMethod().matches("PUT") && csrf != null ) {
            request.getHeaders().add("X-CSRF-TOKEN", csrf);
        }
        log.trace("Headers for "+request.getMethod()+"  "+request.getURI());
        for(String key : request.getHeaders().keySet()){
            for(String x : request.getHeaders().get(key)) {
                log.trace(key+" "+x);
            }
        }
        log.trace("----------------------------------------------");


        ClientHttpResponse response = execution.execute(request, body);
        log.trace(response.getStatusCode()+" "+response.getStatusText());
        return response;
    }

    public String getToken() {
        return token;
    }
}
