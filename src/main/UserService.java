package main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final DatabaseConnection databaseConnection = new DatabaseConnection();

    public ObjectNode getUserDataById(String username) {
        ObjectNode response = databaseConnection.fetchUserDataByUsername(username);
        if (response.has("error")) {
            logger.error("Error fetching user data: {}", response.get("error").asText());
        }
        return response;
    }

    public ObjectNode createUser(String username, String email, String name) {
        return databaseConnection.insertUser(username, email, name);
    }

    public ObjectNode getAllUsers() {
        return databaseConnection.fetchUsers();
    }

    public ObjectNode getPostDataById(String postId) {
        return databaseConnection.fetchPostDataById(Integer.parseInt(postId));
    }

    public ObjectNode getUserPosts(String username) {
        return databaseConnection.fetchUserPosts(username);
    }
}
