package com.mr.authsvc.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class PrincipalRestController {

  @RequestMapping("/user")
  Principal principal(Principal p) {
    return p;
  }
}
