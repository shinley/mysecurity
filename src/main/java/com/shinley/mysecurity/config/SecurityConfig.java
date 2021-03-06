package com.shinley.mysecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinley.mysecurity.repository.LDAPUserRepo;
import com.shinley.mysecurity.security.filter.RestAuthenticationFilter;
import com.shinley.mysecurity.security.jwt.JwtFilter;
import com.shinley.mysecurity.security.ldap.LDAPMultiAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;
import java.util.Map;


@Order(99)
@RequiredArgsConstructor
@Slf4j
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;

    private final DataSource dataSource;

    private final UserDetailsService userDetailsService;

    private final UserDetailsPasswordService userDetailsPasswordService;

    private final LDAPUserRepo ldapUserRepo;

    private final JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers(req -> req.mvcMatchers("/authorize/**", "/api/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(req -> req.antMatchers("/authorize/**").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/api/**").hasRole("USER"))
                .addFilterAt(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.ignoringAntMatchers("/authorize/**", "/admin/**", "/api/**"))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
        ;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/error/**", "/h2-console/**");
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////        auth.inMemoryAuthentication().withUser("user")
//        auth.jdbcAuthentication()
//                .withDefaultSchema() // ????????????????????????
//                .dataSource(dataSource)
//                .withUser("user")
//                .password(passwordEncoder().encode("12345678"))
//                .roles("USER", "ADMIN")
//                .and()
//                .withUser("zhangsan")
////                .password(new MessageDigestPasswordEncoder("SHA-1").encode("abc123"))
//                .password("{SHA-1}{V+M6G5s38TVSgzNseMHGDrduLjN06mJ3btCmcMDC8b4=}600f76a9d83a495b426e4507bd5decaca0b826e4")
//                .roles("USER", "ADMIN")
//        ;
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .passwordEncoder(passwordEncoder());
//        ;
//    }


//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .usersByUsernameQuery("select username, password, enabled from mooc_users where username=?")
//                .authoritiesByUsernameQuery("select username, authority from mooc_authorities where username=?")
//                .passwordEncoder(passwordEncoder());
//        ;
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder());
//        ;
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ldapMultiAuthenticationProvider());
        auth.authenticationProvider(daoAuthenticationProvider());
    }


    private AuthenticationFailureHandler jsonAuthenticationFailureHandler() {
        return (req, res, exception) -> {
            val objectMapper = new ObjectMapper();
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setCharacterEncoding("UTF-8");
            val errData = Map.of(
                    "title", "????????????",
                    "details", exception.getMessage()
            );
            res.getWriter().println(objectMapper.writeValueAsString(errData));
        };
    }

    @Bean
    LDAPMultiAuthenticationProvider ldapMultiAuthenticationProvider() {
        LDAPMultiAuthenticationProvider daoAuthenticationProvider = new LDAPMultiAuthenticationProvider(ldapUserRepo);
        return daoAuthenticationProvider;
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsPasswordService(userDetailsPasswordService);
        return daoAuthenticationProvider;
    }

    private AuthenticationSuccessHandler jsonAuthenticationSuccessHandler() {
        return (req, res, auth) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            res.setStatus(HttpStatus.OK.value());
            res.getWriter().println(objectMapper.writeValueAsString(auth));
            log.debug("????????????");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        val idForDefault = "bcrypt";
        val encoders = Map.of(
                idForDefault, new BCryptPasswordEncoder(),
                "SHA-1", new MessageDigestPasswordEncoder("SHA-1")
        );
        return new DelegatingPasswordEncoder(idForDefault, encoders);
    }

    private RestAuthenticationFilter restAuthenticationFilter() throws Exception{
        RestAuthenticationFilter filter = new RestAuthenticationFilter(objectMapper);
        filter.setAuthenticationSuccessHandler(jsonAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(jsonAuthenticationFailureHandler());

        // ?????????????????? authenticationManager()
        filter.setAuthenticationManager(authenticationManager());
        // ????????????url
        filter.setFilterProcessesUrl("/authorize/login");
        return filter;
    }

}
