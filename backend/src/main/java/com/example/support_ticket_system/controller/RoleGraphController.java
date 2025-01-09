package com.example.support_ticket_system.controller;


import com.example.support_ticket_system.model.Role;
import com.example.support_ticket_system.service.RoleGraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleGraphController {


    private final RoleGraphService roleGraphService;

    public RoleGraphController(RoleGraphService roleGraphService) {
        this.roleGraphService = roleGraphService;
    }

    // Endpoint to add a new role
    @PostMapping("/createRole")
    public ResponseEntity<String> addRole(@RequestBody Role role) {
        roleGraphService.addRole(role.getRole(), role.getInheritsFrom());
        return ResponseEntity.ok("Role added successfully!");
    }

    // Endpoint to get inherited roles for a specific role
    @GetMapping("/{roleName}/inherited-db")
    public ResponseEntity<List<Role>> getInheritedRoles(@PathVariable String roleName) {
        List<Role> inheritedRoles = roleGraphService.getInheritedRoles(roleName);
        return ResponseEntity.ok(inheritedRoles);
    }

    // Endpoint to get all roles
    @GetMapping("/getAllRoles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> allRoles = roleGraphService.getAllRoles();
        return ResponseEntity.ok(allRoles);
    }

    @GetMapping("/{roleName}/inherited-graph")
    public ResponseEntity<List<String>> getInheritedRolesInMemory(@PathVariable String roleName) {
        List<String> inheritedRoles = roleGraphService.getInheritedRolesInMemory(roleName);
        return ResponseEntity.ok(inheritedRoles);
    }


}
