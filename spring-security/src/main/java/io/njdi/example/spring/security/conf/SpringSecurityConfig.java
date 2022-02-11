package io.njdi.example.spring.security.conf;

import io.njdi.example.spring.security.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private DataSource dataSource;

  @Bean
  public JdbcUserDetailsManager createJdbcUserDetailsManager() {
    return new JdbcUserDetailsManager(dataSource);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests(authorize -> authorize.mvcMatchers("/hello/guest").permitAll()
                    .mvcMatchers("/hello/user").hasRole("USER")
                    .mvcMatchers("/hello/admin").hasRole("ADMIN"))
            .addFilterBefore(new AuthenticationFilter(createJdbcUserDetailsManager()), BasicAuthenticationFilter.class);
  }

  @Bean
  RoleHierarchy hierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

    return hierarchy;
  }
}
