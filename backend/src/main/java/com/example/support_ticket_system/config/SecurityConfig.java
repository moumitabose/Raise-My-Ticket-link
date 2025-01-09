package com.example.support_ticket_system.config;


import com.example.support_ticket_system.auth.JwtAuthenticationFilter;
import com.example.support_ticket_system.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }


//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .cors().configurationSource(corsConfigurationSource()) // Link CORS configuration
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless, no session
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/tickets/**").authenticated() // Require authentication for "/test"
//                .anyRequest().permitAll() // Allow other requests without authentication
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//    }





//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .cors().configurationSource(corsConfigurationSource()) // Link CORS configuration
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless, no session
//                .and()
//                .authorizeRequests()
//                // Admin-specific endpoints (require ROLE_ADMIN)
//                .antMatchers("/api/admin/tickets/**").hasRole("Admin") // Only admins can access
//                // User-specific endpoints (for authenticated users, adjust as per your app)
//                .antMatchers("/api/tickets/**").authenticated()  // Authenticated users can access general ticket APIs
//                .anyRequest().permitAll() // Allow other requests without authentication
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class);
//    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .cors().configurationSource(corsConfigurationSource()) // Link CORS configuration
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless, no session
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/**").permitAll() // Allow all requests to API
//                .anyRequest().permitAll() // Allow other requests without authentication
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class);
//    }


//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .cors().configurationSource(corsConfigurationSource()) // Link CORS configuration
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless, no session
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/admin/tickets/**").hasAuthority("Admin") // Use authority without ROLE_ prefix
//                .antMatchers("/api/admin/tickets/**").authenticated()  // Authenticated users can access general ticket APIs
//                .anyRequest().permitAll() // Allow other requests without authentication
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class);
//    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().configurationSource(corsConfigurationSource()) // Link CORS configuration
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless, no session
                .and()
                .authorizeRequests()
                .antMatchers("/api/admin/tickets/**").hasAuthority("Admin") // Use authority without ROLE_ prefix
                .antMatchers("/api/admin/tickets/**").authenticated()  // Authenticated users can access general ticket APIs
                .antMatchers("/api/tickets/resolved").hasAuthority("Agent") // Restrict access to resolved tickets for agents
                .antMatchers("/api/tickets/**").authenticated()
                .anyRequest().permitAll() // Allow other requests without authentication
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class);
    }




//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .cors().configurationSource(corsConfigurationSource()) // Link CORS configuration
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless, no session
//                .and()
//                .authorizeRequests()
//                // Admin-specific endpoints (require role fetched from UserRepository)
//                .antMatchers("/api/admin/tickets/**").access("@userSecurityService.hasAdminRole(authentication)") // Dynamic role check
//                // User-specific endpoints (for authenticated users, adjust as per your app)
//                .antMatchers("/api/tickets/**").authenticated()  // Authenticated users can access general ticket APIs
//                .anyRequest().permitAll() // Allow other requests without authentication
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class);
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Frontend origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "custom-header" // Allow your custom header
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // Expose additional headers if needed
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }





}
