package com.jo.api.security.service;

import com.jo.api.security.models.ERole;
import com.jo.api.security.models.Role;
import com.jo.api.security.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public void createRole() {
        Role oldRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElse(null);
        if (oldRole == null) {
            Role role = new Role();
            role.setName(ERole.ROLE_CUSTOMER);
            this.roleRepository.save(role);
        }
    }

    public void createRoleAdmin() {
        Role oldRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElse(null);
        if (oldRole == null) {
            Role role = new Role();
            role.setName(ERole.ROLE_ADMIN);
            this.roleRepository.save(role);
        }
    }
}
