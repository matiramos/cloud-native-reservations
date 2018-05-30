package com.mr.reservationclient;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Profile("secure")
@Configuration
@EnableResourceServer
@EnableOAuth2Client
public class OAuthResourceConfiguration {
}
