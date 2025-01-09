package com.example.support_ticket_system.repository;


import com.example.support_ticket_system.model.Agent;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AgentRepository {

    private final DynamoDbTable<Agent> agentTable;

    public AgentRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.agentTable = dynamoDbEnhancedClient.table("Agents", TableSchema.fromBean(Agent.class));
    }

    public void saveAgent(Agent agent) {
        agentTable.putItem(agent);
    }

    public List<Agent> getAllAgents() {
        return agentTable.scan().items().stream().collect(Collectors.toList());
    }

    public Optional<Agent> findAgentById(String id) {
        return Optional.ofNullable(agentTable.getItem(r -> r.key(k -> k.partitionValue(id))));
    }

    public void updateAgent(Agent agent) {
        agentTable.putItem(agent);
    }


}
