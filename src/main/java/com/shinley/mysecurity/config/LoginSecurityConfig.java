package com.shinley.mysecurity.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(100)
public class LoginSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                formLogin(form -> form.loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                        .failureUrl("/login?error")
                .defaultSuccessUrl("/")
                .permitAll())
                .logout(logout -> logout.logoutUrl("/perform_logout"))
                .rememberMe(remberMe -> remberMe.tokenValiditySeconds(30 * 24 * 3600)
                        .key("someSecret"))
                .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated());

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // public下的资源，都不启用过滤器链
        web.ignoring().mvcMatchers("/pulbic/**", "/error")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
