package com.example.support_ticket_system.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Role {

    private String role;         // Partition Key
    private String inheritsFrom;

    // Constructor
    public Role(String role, String inheritsFrom) {
        this.role = role;
        this.inheritsFrom = inheritsFrom;
    }

    public Role() {
    }


    @DynamoDbPartitionKey
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @DynamoDbSortKey
    public String getInheritsFrom() {
        return inheritsFrom;
    }

    public void setInheritsFrom(String inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }
}
