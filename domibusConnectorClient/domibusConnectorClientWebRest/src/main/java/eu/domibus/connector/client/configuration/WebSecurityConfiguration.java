package eu.domibus.connector.client.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
//@EnableWebSecurity
public class WebSecurityConfiguration { //extends WebSecurityConfigurerAdapter {
////
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        http
////                .httpBasic().realmName("basic-auth")
////                ;
//
//        http.authorizeRequests()
//                .antMatchers("/services/**").permitAll()
////                .antMatchers("/", "/home").permitAll()
//                .anyRequest().permitAll();
//    }
//
//
//
////    @Bean
////    @Override
////    public UserDetailsService userDetailsService() {
////        UserDetails user =
////                User.builder()
////                        .username("user")
////                        .password("user")
////                        .roles("USER")
////                        .build();
////
////        UserDetails admin = User.builder()
////                .username("admin")
////                .password("admin")
////                .roles("ADMIN", "USER")
////                .build();
////
////        return new InMemoryUserDetailsManager(user, admin);
////    }

}
