package io.njdi.example.spring.security.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

@Configuration
public class AclConfig {
  @Autowired
  private DataSource dataSource;

  @Autowired
  private CacheManager cacheManager;

  @Bean
  public AuditLogger createAuditLogger() {
    return new ConsoleAuditLogger();
  }

  @Bean
  public AclAuthorizationStrategy createAclAuthorizationStrategy() {
    String role = "ROLE_ADMIN";
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);

    return new AclAuthorizationStrategyImpl(grantedAuthority);
  }

  @Bean
  public PermissionGrantingStrategy createPermissionGrantingStrategy() {
    return new DefaultPermissionGrantingStrategy(createAuditLogger());
  }

  @Bean
  public AclCache createAclCache() {
    Cache cache = cacheManager.getCache("aclCache");

    return new SpringCacheBasedAclCache(cache, createPermissionGrantingStrategy(), createAclAuthorizationStrategy());
  }

  @Bean
  public LookupStrategy createLookupStrategy() {
    BasicLookupStrategy basicLookupStrategy = new BasicLookupStrategy(dataSource, createAclCache(),
            createAclAuthorizationStrategy(), createAuditLogger());

    basicLookupStrategy.setAclClassIdSupported(true);

    return basicLookupStrategy;
  }

  @Bean
  public AclService createAclService() {
    JdbcMutableAclService jdbcMutableAclService = new JdbcMutableAclService(dataSource, createLookupStrategy(),
            createAclCache());

    jdbcMutableAclService.setClassIdentityQuery("SELECT @@IDENTITY");
    jdbcMutableAclService.setSidIdentityQuery("SELECT @@IDENTITY");

    jdbcMutableAclService.setAclClassIdSupported(true);

    return jdbcMutableAclService;
  }

  @Bean
  public MethodSecurityExpressionHandler createMethodSecurityExpressionHandler() {
    PermissionEvaluator permissionEvaluator
            = new AclPermissionEvaluator(createAclService());

    DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler =
            new DefaultMethodSecurityExpressionHandler();
    methodSecurityExpressionHandler.setPermissionEvaluator(permissionEvaluator);

    return methodSecurityExpressionHandler;
  }
}
