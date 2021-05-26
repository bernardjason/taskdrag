package org.bjason.taskdrag.javafx;

import javafx.application.Application;
import org.bjason.taskdrag.common.CallBackend;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

//@SpringBootApplication
// little quicker
@EnableAutoConfiguration
@ComponentScan
public class TheManagerApplication {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CallBackend getCallBackEnd() {
        return  new CallBackend();
    }

    public static void main(String[] args) {
        if ( System.getProperty("os.name","").compareToIgnoreCase("linux") == 0 ) {
            System.out.println("Setting jdk.gtk.version=2 https://bugs.openjdk.java.net/browse/JDK-8211302");
            System.setProperty("jdk.gtk.version","2") ;
        }
        Application.launch(Main.class,args);
    }
}

