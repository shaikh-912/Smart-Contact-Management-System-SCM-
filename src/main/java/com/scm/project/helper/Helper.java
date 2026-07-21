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
    public static String getEmailVerificationLink(String emailToken, String baseUrl) {
        String finalBaseUrl = baseUrl;

        try {
            org.springframework.web.context.request.RequestAttributes attribs = 
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attribs instanceof org.springframework.web.context.request.ServletRequestAttributes) {
                jakarta.servlet.http.HttpServletRequest request = 
                    ((org.springframework.web.context.request.ServletRequestAttributes) attribs).getRequest();
                
                String scheme = request.getScheme();
                String serverName = request.getServerName();
                int serverPort = request.getServerPort();
                
                // Handle reverse proxy headers commonly set by Render, Railway, etc.
                String forwardedProto = request.getHeader("X-Forwarded-Proto");
                if (forwardedProto != null && !forwardedProto.isBlank()) {
                    scheme = forwardedProto;
                }
                String forwardedHost = request.getHeader("X-Forwarded-Host");
                if (forwardedHost != null && !forwardedHost.isBlank()) {
                    serverName = forwardedHost;
                    finalBaseUrl = scheme + "://" + serverName;
                } else {
                    if ((scheme.equalsIgnoreCase("http") && serverPort == 80) || (scheme.equalsIgnoreCase("https") && serverPort == 443)) {
                        finalBaseUrl = scheme + "://" + serverName;
                    } else {
                        finalBaseUrl = scheme + "://" + serverName + ":" + serverPort;
                    }
                }
            }
        } catch (Exception e) {
            // Fallback to baseUrl parameter if context resolution fails
        }

        // If dynamically resolved URL is local/empty, prefer non-local configured baseUrl if available
        if (finalBaseUrl == null || finalBaseUrl.isBlank() || finalBaseUrl.contains("localhost")) {
            if (baseUrl != null && !baseUrl.isBlank() && !baseUrl.contains("localhost")) {
                finalBaseUrl = baseUrl;
            }
        }

        if (finalBaseUrl == null || finalBaseUrl.isBlank()) {
            finalBaseUrl = "http://localhost:8081";
        }

        finalBaseUrl = finalBaseUrl.trim();
        while (finalBaseUrl.endsWith("/")) {
            finalBaseUrl = finalBaseUrl.substring(0, finalBaseUrl.length() - 1);
        }
        return finalBaseUrl + "/auth/verify-email?token=" + emailToken;
    }

    public static String getEmailVerificationLink(String emailToken) {
        return getEmailVerificationLink(emailToken, "http://localhost:8081");
    }
}
