package com.github.longkerdandy.burger.backend.security;

import com.github.longkerdandy.burger.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * {@link UserDetailsService} interface has a method to load User by username and returns a
 * {@link UserDetails} object that Spring Security can use for authentication and validation.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository mongo;    // Mongo repository

  /**
   * Constructor.
   */
  @Autowired
  public UserDetailsServiceImpl(UserRepository mongo) {
    this.mongo = mongo;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return this.mongo.getUserByUsername(username);
  }
}
