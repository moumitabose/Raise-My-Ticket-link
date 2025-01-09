package com.example.support_ticket_system.repository;


import com.example.support_ticket_system.model.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;

@Repository
public class UserRepository {


    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbClient dynamoDbClient;  // Add DynamoDbClient for scan operations
    private final DynamoDbTable<User> userTable;

    public UserRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoDbClient dynamoDbClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.dynamoDbClient = dynamoDbClient;  // Initialize DynamoDbClient
        this.userTable = dynamoDbEnhancedClient.table("Users", TableSchema.fromBean(User.class));
    }

    // Create or update a user
    public void saveUser(User user) {
        userTable.putItem(user);
    }

    // Fetch user by email and role
    public User getUser(String email, String role) {
        return userTable.getItem(r -> r.key(k -> k.partitionValue(email).sortValue(role)));
    }

    // Delete user
    public void deleteUser(String email, String role) {
        userTable.deleteItem(r -> r.key(k -> k.partitionValue(email).sortValue(role)));
    }

    public User getUserByEmail(String email) {
        return userTable.getItem(r -> r.key(k -> k.partitionValue(email)));
    }

//    public User getUserByUsername(String username) {
//        System.out.println("Starting getUserByUsername with username: " + username);
//
//        ScanRequest scanRequest = ScanRequest.builder()
//                .tableName("Users")  // Ensure this matches your actual DynamoDB table name
//                .filterExpression("username = :username")
//                .expressionAttributeValues(Map.of(":username", AttributeValue.builder().s(username).build()))
//                .build();
//
//        System.out.println("ScanRequest created: " + scanRequest);
//
//        ScanResponse result = dynamoDbClient.scan(scanRequest);  // Use DynamoDbClient for scan operation
//
//        // Debug the scan response
//        System.out.println("ScanResponse received: " + result);
//
//        if (result.hasItems() && !result.items().isEmpty()) {
//            System.out.println("Items found: " + result.items().size());
//
//            Map<String, AttributeValue> item = result.items().get(0);  // Assuming only one item with the given username
//            System.out.println("Found item: " + item);
//
//            User user = new User();
//
//            // Check if attribute is not null before calling .s()
//            user.setEmail(item.containsKey("email") ? item.get("email").s() : null);
//            user.setRole(item.containsKey("role") ? item.get("role").s() : null);
//            user.setUsername(item.containsKey("username") ? item.get("username").s() : null);
//            user.setPassword(item.containsKey("password") ? item.get("password").s() : null);
//            user.setCreatedAt(item.containsKey("createdAt") ? item.get("createdAt").s() : null);
//            user.setUpdatedAt(item.containsKey("updatedAt") ? item.get("updatedAt").s() : null);
//
//            System.out.println("Returning user: " + user);
//            return user;
//        } else {
//            System.out.println("No user found with username: " + username);
//            return null;  // No user found with the given username
//        }
//    }


    public String getNameByEmail(String email) {
        System.out.println("Starting getNameByEmail with email: " + email);

        // Create the QueryRequest for querying the table using the partition key (email)
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Users")  // Ensure this matches your DynamoDB table name
                .keyConditionExpression("email = :email")  // Query condition on the partition key
                .expressionAttributeValues(Map.of(":email", AttributeValue.builder().s(email).build()))
                .build();

        System.out.println("QueryRequest created: " + queryRequest);

        // Perform the query using DynamoDbClient
        QueryResponse result = dynamoDbClient.query(queryRequest);  // Use Query method instead of Scan

        // Debug the query response
        System.out.println("QueryResponse received: " + result);

        if (!result.items().isEmpty()) {
            System.out.println("Items found: " + result.items().size());

            Map<String, AttributeValue> item = result.items().get(0);  // Assuming only one item with the given email
            System.out.println("Found item: " + item);

            // Return the name value
            return item.containsKey("name") ? item.get("name").s() : null;
        } else {
            System.out.println("No user found with email: " + email);
            return null;  // No user found with the given email
        }
    }



    public User getUserByUsername(String username) {
        System.out.println("Starting getUserByUsername with username: " + username);

        // Use QueryRequest for querying the GSI (username-index)
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Users")  // Ensure this matches your DynamoDB table name
                .indexName("username-index")  // Specify the name of your GSI
                .keyConditionExpression("username = :username")  // Query condition
                .expressionAttributeValues(Map.of(":username", AttributeValue.builder().s(username).build()))
                .build();

        System.out.println("QueryRequest created: " + queryRequest);

        // Perform the query using DynamoDbClient
        QueryResponse result = dynamoDbClient.query(queryRequest);  // Use Query method instead of Scan

        // Debug the query response
        System.out.println("QueryResponse received: " + result);

        if (!result.items().isEmpty()) {
            System.out.println("Items found: " + result.items().size());

            Map<String, AttributeValue> item = result.items().get(0);  // Assuming only one item with the given username
            System.out.println("Found item: " + item);

            User user = new User();

            // Map DynamoDB attributes to User object
            user.setEmail(item.containsKey("email") ? item.get("email").s() : null);
            user.setRole(item.containsKey("role") ? item.get("role").s() : null);
            user.setUsername(item.containsKey("username") ? item.get("username").s() : null);
            user.setPassword(item.containsKey("password") ? item.get("password").s() : null);
            user.setCreatedAt(item.containsKey("createdAt") ? item.get("createdAt").s() : null);
            user.setUpdatedAt(item.containsKey("updatedAt") ? item.get("updatedAt").s() : null);

            System.out.println("Returning user: " + user);
            return user;
        } else {
            System.out.println("No user found with username: " + username);
            return null;  // No user found with the given username
        }
    }


    public String getEmailByUsername(String username) {
        System.out.println("Starting getEmailByUsername with username: " + username);

        // Create the QueryRequest for querying the GSI (username-index)
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Users")  // Ensure this matches your DynamoDB table name
                .indexName("username-index")  // Specify the name of your GSI
                .keyConditionExpression("username = :username")  // Query condition
                .expressionAttributeValues(Map.of(":username", AttributeValue.builder().s(username).build()))
                .build();

        System.out.println("QueryRequest created: " + queryRequest);

        // Perform the query using DynamoDbClient
        QueryResponse result = dynamoDbClient.query(queryRequest);  // Use Query method instead of Scan

        // Debug the query response
        System.out.println("QueryResponse received: " + result);

        if (!result.items().isEmpty()) {
            System.out.println("Items found: " + result.items().size());

            Map<String, AttributeValue> item = result.items().get(0);  // Assuming only one item with the given username
            System.out.println("Found item: " + item);

            // Return the email value
            return item.containsKey("email") ? item.get("email").s() : null;
        } else {
            System.out.println("No user found with username: " + username);
            return null;  // No user found with the given username
        }
    }

    // Method to fetch role by email using QueryRequest
    public String getRoleByEmail(String email) {
        System.out.println("Starting getRoleByEmail with email: " + email);

        // Create the QueryRequest for querying the table using the partition key (email)
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Users")  // Ensure this matches your DynamoDB table name
                .keyConditionExpression("email = :email")  // Query condition on the partition key
                .expressionAttributeValues(Map.of(":email", AttributeValue.builder().s(email).build()))
                .build();

        System.out.println("QueryRequest created: " + queryRequest);

        // Perform the query using DynamoDbClient
        QueryResponse result = dynamoDbClient.query(queryRequest);  // Use Query method instead of Scan

        // Debug the query response
        System.out.println("QueryResponse received: " + result);

        if (!result.items().isEmpty()) {
            System.out.println("Items found: " + result.items().size());

            Map<String, AttributeValue> item = result.items().get(0);  // Assuming only one item with the given email
            System.out.println("Found item: " + item);

            // Return the role value
            return item.containsKey("role") ? item.get("role").s() : null;
        } else {
            System.out.println("No user found with email: " + email);
            return null;  // No user found with the given email
        }


    }

    public String getUserCodeByUsername(String username) {
        System.out.println("Starting getUserCodeByUsername with username: " + username);

        // Create the QueryRequest for querying the GSI (username-index)
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Users")  // Ensure this matches your DynamoDB table name
                .indexName("username-index")  // Specify the name of your GSI
                .keyConditionExpression("username = :username")  // Query condition
                .expressionAttributeValues(Map.of(":username", AttributeValue.builder().s(username).build()))
                .build();

        System.out.println("QueryRequest created: " + queryRequest);

        // Perform the query using DynamoDbClient
        QueryResponse result = dynamoDbClient.query(queryRequest);  // Use Query method instead of Scan

        // Debug the query response
        System.out.println("QueryResponse received: " + result);

        if (!result.items().isEmpty()) {
            System.out.println("Items found: " + result.items().size());

            Map<String, AttributeValue> item = result.items().get(0);  // Assuming only one item with the given username
            System.out.println("Found item: " + item);

            // Return the userCode value
            return item.containsKey("userCode") ? item.get("userCode").s() : null;
        } else {
            System.out.println("No user found with username: " + username);
            return null;  // No user found with the given username
        }
    }


}
