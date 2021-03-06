package com.mr.authsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableDiscoveryClient
@SpringBootApplication
public class AuthSvcApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthSvcApplication.class, args);
  }
}

@Controller
class MainController {

  @GetMapping("/")
  public String index() {
    return "index";
  }
}

@RestController
class ProfileRestController {

  @GetMapping("/resources/userinfo")
  Map<String, String> profile(Principal principal) {
    return Collections.singletonMap("name", principal.getName());
  }
}

@Configuration
@EnableResourceServer
class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/resources/**")
        .authorizeRequests()
        .mvcMatchers("/resources/userinfo")
        .access("#oauth2.hasScope('profile')");
  }
}

@Configuration
@Order(1000)
class AuthenticationMananagerProvider extends WebSecurityConfigurerAdapter {

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

  private final AuthenticationManager authenticationManager;

  AuthorizationServerConfig(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients
        .inMemory()
        .withClient("client-1234")
        .secret("{noop}secret")
        .authorizedGrantTypes("authorization_code")
        .scopes("profile")
        .redirectUris("http://localhost:8001/login/oauth2/code/login-client");
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints
        .tokenStore(this.tokenStore())
        .accessTokenConverter(jwtAccessTokenConverter())
        .authenticationManager(this.authenticationManager);
  }

  @Bean
  JwtAccessTokenConverter jwtAccessTokenConverter() {
    KeyStoreKeyFactory factory =
        new KeyStoreKeyFactory(
            new ClassPathResource(".keystore-oauth2-demo"), "admin1234".toCharArray());
    JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
    jwtAccessTokenConverter.setKeyPair(factory.getKeyPair("oauth2-demo-key"));
    return jwtAccessTokenConverter;
  }

  @Bean
  TokenStore tokenStore() {
    return new JwtTokenStore(this.jwtAccessTokenConverter());
  }
}

@Service
class SimpleUserDetailsService implements UserDetailsService {

  private final Map<String, UserDetails> users = new ConcurrentHashMap<>();

  SimpleUserDetailsService() {
    Arrays.asList("mramos", "jperez")
        .forEach(
            username ->
                this.users.putIfAbsent(
                    username,
                    new User(
                        username,
                        "password",
                        true,
                        true,
                        true,
                        true,
                        AuthorityUtils.createAuthorityList("USER"))));
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return this.users.get(username);
  }
}

@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().anyRequest().authenticated().and().formLogin();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
            .withUser("user")
            .password("{noop}password")
            .roles("USER")
            .and()
            .withUser("sysuser")
            .password("{noop}password")
            .roles("SYSTEM");
  }
}
