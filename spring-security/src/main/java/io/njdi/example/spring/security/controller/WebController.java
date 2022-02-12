package io.njdi.example.spring.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web")
public class WebController {
  @GetMapping("/guest")
  public String helloGuest() {
    return "hello guest";
  }

  @GetMapping("/user")
  public String helloUser() {
    return "hello user";
  }

  @GetMapping("/admin")
  public String helloAdmin() {
    return "hello admin";
  }
}
