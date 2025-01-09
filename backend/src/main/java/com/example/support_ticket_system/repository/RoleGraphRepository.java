package com.example.support_ticket_system.repository;

import com.example.support_ticket_system.model.Role;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.*;

@Repository
public class RoleGraphRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbClient dynamoDbClient;  // Add DynamoDbClient for scan operations
    private final DynamoDbTable<Role> roleTable;
    private final Map<String, List<String>> roleGraph; // In-memory role graph


    public RoleGraphRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoDbClient dynamoDbClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.dynamoDbClient = dynamoDbClient;
        this.roleTable = dynamoDbEnhancedClient.table("RoleGraph", TableSchema.fromBean(Role.class));
        this.roleGraph = new HashMap<>();
        loadRolesIntoGraph(); // Populate the in-memory graph at initialization
    }

    //    // Add a new role to the table
    public void addRole(Role role) {
        roleTable.putItem(role);
        addRoleToGraph(role.getRole(), role.getInheritsFrom());
        System.out.println("Added role: " + role);
    }

    // Add a role relationship to the in-memory graph
    private void addRoleToGraph(String role, String inheritsFrom) {
        roleGraph.computeIfAbsent(role, k -> new ArrayList<>()).add(inheritsFrom);
    }

    // Load all roles from DynamoDB into the in-memory graph
    private void loadRolesIntoGraph() {
        List<Role> allRoles = getAllRoles();
        for (Role role : allRoles) {
            addRoleToGraph(role.getRole(), role.getInheritsFrom());
        }
    }

    // Query roles inherited from a specific role using DynamoDB
    public List<Role> getInheritedRoles(String roleName) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("RoleGraph")
                .keyConditionExpression("role = :role")
                .expressionAttributeValues(Map.of(
                        ":role", AttributeValue.builder().s(roleName).build()
                ))
                .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);

        List<Role> roles = new ArrayList<>();
        response.items().forEach(item -> {
            String role = item.get("role").s();
            String inheritsFrom = item.get("inheritsFrom").s();
            roles.add(new Role(role, inheritsFrom));
        });

        return roles;
    }

    // Retrieve all roles from DynamoDB
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        roleTable.scan().items().forEach(roles::add);
        return roles;
    }

    // Retrieve all roles inherited from a specific role using the in-memory graph
    public List<String> getAllInheritedRolesInMemory(String roleName) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        dfs(roleName, visited, result);
        return result;
    }

    // Helper method for DFS traversal in the in-memory graph
    private void dfs(String role, Set<String> visited, List<String> result) {
        if (visited.contains(role)) return;

        visited.add(role);
        result.add(role);

        List<String> inheritedRoles = roleGraph.get(role);
        if (inheritedRoles != null) {
            for (String inheritedRole : inheritedRoles) {
                dfs(inheritedRole, visited, result);
            }
        }
    }

}
