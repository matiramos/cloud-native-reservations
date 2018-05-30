package com.mr.authsvc.clients;

import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Client {

  @Id
  @GeneratedValue
  private Long id;

  private String clientId;
  private String secret;
  private String scopes = StringUtils.arrayToCommaDelimitedString(new String[]{"openid"});
  private String authorizedGrantTypes = StringUtils.arrayToCommaDelimitedString(new String[]{"authorization_code", "refresh_token", "password"});
  private String authorities = StringUtils.arrayToCommaDelimitedString(new String[]{"ROLE_USER", "ROLE_ADMIN"});
  private String autoApproveScopes = StringUtils.arrayToCommaDelimitedString(new String[]{".*"});

  public Client() {
  }

  public Client(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.secret = clientSecret;
  }

  public Long getId() {
    return id;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getScopes() {
    return scopes;
  }

  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  public String getAuthorizedGrantTypes() {
    return authorizedGrantTypes;
  }

  public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
    this.authorizedGrantTypes = authorizedGrantTypes;
  }

  public String getAuthorities() {
    return authorities;
  }

  public void setAuthorities(String authorities) {
    this.authorities = authorities;
  }

  public String getAutoApproveScopes() {
    return autoApproveScopes;
  }

  public void setAutoApproveScopes(String autoApproveScopes) {
    this.autoApproveScopes = autoApproveScopes;
  }
}
