package io.njdi.example.spring.security.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

  private final JdbcUserDetailsManager manager;

  public AuthenticationFilter(JdbcUserDetailsManager manager) {
    this.manager = manager;
  }

  private void authenticate(String user, String password) {
    if (!StringUtils.hasLength(user) || !StringUtils.hasLength(password)) {
      // 用户名或密码为空
      return;
    }

    if (!manager.userExists(user)) {
      // 用户不存在
      return;
    }

    UserDetails userDetails = manager.loadUserByUsername(user);
    String encodedPassword = userDetails.getPassword();

    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    if (!encoder.matches(password, encodedPassword)) {
      // 密码不匹配
      return;
    }

    /*
      用户认证通过
     */
    UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    // 用户角色
                    userDetails.getAuthorities());

    SecurityContext context =
            SecurityContextHolder.createEmptyContext();
    context.setAuthentication(token);

    SecurityContextHolder.setContext(context);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String user = request.getHeader("spring.security.user");
    String password = request.getHeader("spring.security.password");
    LOGGER.info("user: {}, password: {}", user, password);

    authenticate(user, password);

    filterChain.doFilter(request, response);
  }
}
