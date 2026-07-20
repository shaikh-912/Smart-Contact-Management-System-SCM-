package com.scm.project.helper;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {
    public static String getEmailOfLoggedUser(Authentication authentication){
            
            if(authentication instanceof OAuth2AuthenticationToken){
                var oAuth2AuthenticationToken=(OAuth2AuthenticationToken)authentication;
                var cId=oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

                var oAuth2User=(OAuth2User)authentication.getPrincipal();
                String username="";

                if(cId.equalsIgnoreCase("google")){
                    System.out.println("getting email form google");
                    username=oAuth2User.getAttribute("email").toString();
                }

                return username;
            }else{
                System.out.println("getting email from database");
                return authentication.getName();
            }
    }
    public static String getEmailVerificationLink(String emailToken) {
    	String link="http://localhost:8081/auth/verify-email?token="+emailToken;
    	return link;
    }
}
