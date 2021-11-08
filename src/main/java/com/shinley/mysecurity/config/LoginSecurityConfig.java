package com.shinley.mysecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinley.mysecurity.security.ldap.LDAPMultiAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@Order(100)
public class LoginSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;

    private final DataSource dataSource;

    private final LDAPMultiAuthenticationProvider ldapMultiAuthenticationProvider;
    private final DaoAuthenticationProvider daoAuthenticationProvider;

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

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .usersByUsernameQuery("select username, password, enabled from mooc_users where username=?")
//                .authoritiesByUsernameQuery("select username, authority from mooc_authorities where username=?")
//                .passwordEncoder(passwordEncoder());
//        ;
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ldapMultiAuthenticationProvider);
        auth.authenticationProvider(daoAuthenticationProvider);
    }

    public PasswordEncoder passwordEncoder() {
        val idForDefault = "bcrypt";
        val encoders = Map.of(
                idForDefault, new BCryptPasswordEncoder(),
                "SHA-1", new MessageDigestPasswordEncoder("SHA-1")
        );
        return new DelegatingPasswordEncoder(idForDefault, encoders);
    }
}
