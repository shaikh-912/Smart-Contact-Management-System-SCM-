package com.scm.project.config;

import java.util.List;
import java.util.UUID;

import javax.security.auth.login.AppConfigurationEntry;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.scm.project.Entity.Provider;
import com.scm.project.helper.AppConstants;
import com.scm.project.repository.UserRepo;
import com.scm.project.services.serviceImplement.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    // @Bean
    // public UserDetailsManager userDetailsManager(){
        
    //     UserDetails user1= User
    //     .withDefaultPasswordEncoder()
    //     .username("shahid")
    //     .password("1234")
    //     .build();

    //     var inMemoryUserDetailsManager= new InMemoryUserDetailsManager(user1);
    //     return inMemoryUserDetailsManager;
    // }

    @Autowired
    private SecurityCustomUserDetailService customUserDetailService;

    @Autowired
    private UserRepo userRepo;

    @SuppressWarnings("deprecation")
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests(authorize -> {
            // authorize.requestMatchers("/home").permitAll();
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        httpSecurity.authenticationProvider(authenticationProvider());
        httpSecurity.formLogin(formlogin->{
            formlogin.loginPage("/login");
            formlogin.loginProcessingUrl("/authenticate");
            formlogin.successForwardUrl("/user/profile");
            // formlogin.failureForwardUrl("/login?error=true");
            formlogin.usernameParameter("email");
            formlogin.passwordParameter("password");
        });

        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.logout(logout->{
            logout.logoutUrl("/logout");
            logout.logoutSuccessUrl("/login?logout=true");
        });

        Logger log=org.slf4j.LoggerFactory.getLogger(SecurityConfig.class);
        // httpSecurity.oauth2Login(Customizer.withDefaults());
        httpSecurity.oauth2Login(auth2->{
            auth2.loginPage("/login");

            auth2.successHandler((request, response, authentication) ->{
                
                DefaultOAuth2User user=(DefaultOAuth2User)authentication.getPrincipal();
                System.out.println(user.getName());
                user.getAttributes().forEach((key,value)->{
                    log.info("{} => {}",key,value);
                });
                log.info(user.getAuthorities().toString());
                String email=user.getAttribute("email").toString();
                String name=user.getAttribute("name").toString();
                String picture=user.getAttribute("picture").toString();

                com.scm.project.Entity.User user1=new com.scm.project.Entity.User();
                user1.setEmail(email);
                user1.setName(name);
                user1.setProfilePic(picture);
                user1.setPassword("password");
                user1.setId(UUID.randomUUID().toString());
                user1.setProvider(Provider.GOOGLE);
                user1.setEnable(true);
                user1.setEmailVarified(true);
                user1.setRoleList(List.of(AppConstants.ROLE_USER));
                user1.setAbout("this account is created using google..");
                user1.setProviderId(user.getName());

                com.scm.project.Entity.User ExistUser=userRepo.findByEmail(email).orElse(null);
                if(ExistUser==null){
                    userRepo.save(user1);
                    log.info("user Saved "+email);
                }
                     response.sendRedirect("/user/dashboard");  
            } );
        });

        return httpSecurity.build();
    }
}
