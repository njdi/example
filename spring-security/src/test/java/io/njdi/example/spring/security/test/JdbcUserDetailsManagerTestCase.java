package io.njdi.example.spring.security.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JdbcUserDetailsManagerTestCase {
  @Autowired
  private JdbcUserDetailsManager manager;

  @Test
  public void add() {
    UserDetails user = User.builder()
            .username("user")
            // 123456
            .password("{bcrypt}$2a$10$Z3/1/TTZsraq.9jWiXfkTumjy1XTwMk9Q.Pb8mUd83c/eSaviSuRC")
            .roles("USER")
            .build();

    UserDetails admin = User.builder()
            .username("admin")
            // adcdef
            .password("{bcrypt}$2a$10$vlDmj4YMosNAa59rLEmLqOiruJIqDdOKXZxa83ai/YGsm2sgVg58e")
            .roles("ADMIN")
            .build();

    manager.createUser(user);
    manager.createUser(admin);
  }
}
