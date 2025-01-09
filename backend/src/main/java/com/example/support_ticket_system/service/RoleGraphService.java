package com.example.support_ticket_system.service;


import com.example.support_ticket_system.model.Role;
import com.example.support_ticket_system.repository.RoleGraphRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleGraphService {

    private final RoleGraphRepository roleGraphRepository;

    public RoleGraphService(RoleGraphRepository roleGraphRepository) {
        this.roleGraphRepository = roleGraphRepository;
    }

    // Add a new role
    public void addRole(String role, String inheritsFrom) {
        Role newRole = new Role(role, inheritsFrom);
        roleGraphRepository.addRole(newRole);
    }
//
    // Get roles inherited from a specific role
    public List<Role> getInheritedRoles(String roleName) {
        return roleGraphRepository.getInheritedRoles(roleName);
    }

    // Get all roles
    public List<Role> getAllRoles() {
        return roleGraphRepository.getAllRoles();
    }

    public List<String> getInheritedRolesInMemory(String roleName) {
        return roleGraphRepository.getAllInheritedRolesInMemory(roleName);
    }
}
