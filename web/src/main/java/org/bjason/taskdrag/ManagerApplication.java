package org.bjason.taskdrag;

import org.bjason.taskdrag.common.CallBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@SpringBootApplication
public class ManagerApplication {

	@Autowired
	private ThymeleafProperties properties;

	@Value("${spring.thymeleaf.templates_root:}")
	private String templatesRoot;


	@Bean
	public CallBackend getCallBackEnd() {
		return new CallBackend();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	private ApplicationContext applicationContext;


	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}


	@Bean
	public ITemplateResolver defaultTemplateResolver() {

		if ( templatesRoot == null || templatesRoot.length() == 0 ) {
			SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
			templateResolver.setApplicationContext(this.applicationContext);
			templateResolver.setPrefix("classpath:/templates/");
			templateResolver.setSuffix(".html");
			templateResolver.setTemplateMode(TemplateMode.HTML);
			templateResolver.setCacheable(true);
			return templateResolver;
		}

		FileTemplateResolver resolver = new FileTemplateResolver();
		resolver.setSuffix(properties.getSuffix());
		resolver.setPrefix(templatesRoot);
		resolver.setTemplateMode(properties.getMode());
		resolver.setCacheable(properties.isCache());
		return resolver;
	}



	public static void main(String[] args) {
		SpringApplication.run( ManagerApplication.class, args);
	}



}
