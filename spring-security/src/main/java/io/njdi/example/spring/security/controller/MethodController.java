package io.njdi.example.spring.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/method")
public class MethodController {
  @GetMapping("/guest")
  @PreAuthorize("permitAll")
  public String helloGuest() {
    return "hello guest";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER')")
  public String helloUser() {
    return "hello user";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String helloAdmin() {
    return "hello admin";
  }
}
