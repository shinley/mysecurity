package com.shinley.mysecurity.repository;

import com.shinley.mysecurity.security.ldap.LDAPUser;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LDAPUserRepo extends LdapRepository<LDAPUser> {

    Optional<LDAPUser> findByUsernameAndPassword(String  username, String password);
}
