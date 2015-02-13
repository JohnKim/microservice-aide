package io.stalk.cloud.sample.resource;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@EnableResourceServer
public class OAuthConfiguration extends ResourceServerConfigurerAdapter{

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource_oauth")
    public DataSource oauthDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    	
    	// JDBC Token Store
        //TokenStore tokenStore = new JdbcTokenStore(oauthDataSource());
        //resources.resourceId("sample-resource").tokenStore(tokenStore);
        
        // Tocken Service
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setClientId("sample-client-cli");
        tokenService.setClientSecret("AAAAAA123456");
        tokenService.setCheckTokenEndpointUrl("http://localhost:8088/oauth/check_token");

        resources
                .resourceId("sample-resource")
                .tokenServices(tokenService);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            // For some reason we cant just "permitAll" OPTIONS requests which are needed for CORS support. Spring Security
            // will respond with an HTTP 401 nonetheless.
            // So we just put all other requests types under OAuth control and exclude OPTIONS.
            .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
                .antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
        .and()
            // Add headers required for CORS requests.
            .headers().addHeaderWriter((request, response) -> {
                response.addHeader("Access-Control-Allow-Origin", "*");
                if (request.getMethod().equals("OPTIONS")) {
                    response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Request-Method"));
                    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
                }
            });
    }
    
}
