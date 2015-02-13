package io.stalk.cloud.auth;

import io.stalk.cloud.auth.service.JdbcUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	@Override
	protected UserDetailsService userDetailsService() {
		return new JdbcUserDetailsService();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/js/**").antMatchers("/css/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
		.authorizeRequests()
			.antMatchers("/login", "/logout.do", "/management/**").permitAll()
			.antMatchers("/**").authenticated()
		.and()
			.formLogin()
			.loginProcessingUrl("/login.do")
			.usernameParameter("name")
			.loginPage("/login")
		.and()
			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout.do")) // AntPathRequestMatcher for GET request
		.and()
			.userDetailsService(userDetailsService());
		// @formatter:on
	}
}
