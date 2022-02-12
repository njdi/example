package io.njdi.example.spring.security.conf;

import io.njdi.example.spring.security.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
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

  @Bean
  AuthenticationFilter createAuthenticationFilter() {
    return new AuthenticationFilter(createJdbcUserDetailsManager());
  }

  @Bean
  AuthenticationEntryPoint createAuthenticationEntryPoint() {
    return (request, response, authException) -> response.getWriter().println("401");
  }

  @Bean
  AccessDeniedHandler createAccessDeniedHandler() {
    return (request, response, accessDeniedException) -> response.getWriter().println("403");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .csrf().disable()
            .httpBasic().disable()
            .logout().disable()
            .authorizeRequests(authorize -> authorize.mvcMatchers("/web/guest").permitAll()
                    .mvcMatchers("/web/user").hasRole("USER")
                    .mvcMatchers("/web/admin").hasRole("ADMIN"))
            .addFilterBefore(createAuthenticationFilter(), BasicAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(createAuthenticationEntryPoint())
            .accessDeniedHandler(createAccessDeniedHandler());
  }

  @Bean
  RoleHierarchy hierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

    return hierarchy;
  }
}
