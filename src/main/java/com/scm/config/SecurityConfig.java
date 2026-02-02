package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import com.scm.services.UserServiceDao;

@Component
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private OAuthAuthenicationSuccessHandler handler;

    @Autowired
    private AuthFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(Customizer -> Customizer.disable())

                // Permit Url
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/privacy", "/terms", "/", "/about",
                                "/service", "/home", "/contactus", "/signup", "/do-signup", "/signin", "/do-signin",
                                "/oauth2/**", "/auth/**", "/error_page", "/success_page")
                        .permitAll()
                        .anyRequest().authenticated())

                // SignIn Authantication
                .formLogin(signinPage -> signinPage
                        .loginPage("/signin")
                        .loginProcessingUrl("/do-signin")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard")
                        // .failureUrl("/signin?error=true")
                        .failureHandler(failureHandler)
                        .permitAll())

                // SignOut Authantication
                .logout(signoutPage -> signoutPage
                        .logoutUrl("/signout")
                        .logoutSuccessUrl("/signin?signout=true"))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // AOuth 2.0 Authantication
                .oauth2Login(oauthPage -> oauthPage
                        .loginPage("/signin")
                        .successHandler(handler))
                .build();
    }

    @Autowired
    public UserServiceDao userServiceDao;

    @Bean
    public UserDetailsService userdetailsService() {
        return this.userServiceDao;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userdetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }
}