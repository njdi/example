package io.njdi.example.spring.security.test;

import io.njdi.example.spring.security.controller.Entity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AclTestCase {
  @Autowired
  private JdbcMutableAclService aclService;

  @Test
  @WithMockUser
  @Transactional
  @Rollback(false)
  public void insert() {
    ObjectIdentity oi = new ObjectIdentityImpl(Entity.class, 1);
    MutableAcl acl;
    try {
      acl = (MutableAcl) aclService.readAclById(oi);
    } catch (NotFoundException nfe) {
      acl = aclService.createAcl(oi);
    }

    Sid sid = new GrantedAuthoritySid("ROLE_USER");
    Permission permission = BasePermission.READ;

    acl.insertAce(acl.getEntries().size(), permission, sid, true);

    aclService.updateAcl(acl);

    System.out.println("insert finish");
  }
}
