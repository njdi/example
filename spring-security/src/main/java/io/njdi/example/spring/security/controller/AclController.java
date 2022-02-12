package io.njdi.example.spring.security.controller;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/acl")
public class AclController {
  private final List<Entity> entities;

  {
    entities = new ArrayList<>();

    entities.add(new Entity(1));
    entities.add(new Entity(2));
    entities.add(new Entity(3));
  }

  @GetMapping("/get")
  @PreAuthorize("hasPermission(#id, 'io.njdi.example.spring.security.controller.Entity', 'read')")
  public Entity get(@RequestParam int id) {
    return entities.stream().filter(entity -> entity.getId() == id).findFirst().orElse(null);
  }

  @GetMapping("/get2")
  @PostAuthorize("hasPermission(returnObject, 'read')")
  public Entity get2(@RequestParam int id) {
    return entities.stream().filter(entity -> entity.getId() == id).findFirst().orElse(null);
  }

  @GetMapping("/gets")
  @PreAuthorize("isAuthenticated()")
  @PostFilter("hasPermission(filterObject, 'read')")
  public List<Entity> gets() {
    return entities;
  }
}
