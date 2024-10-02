package main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService = new UserService();

    public ObjectNode getUser(Request request, Response response) {
        String username = request.params(":username");
        logger.info("Fetching user data for username: {}", username);
        ObjectNode userData = userService.getUserDataById(username);
        logger.info("User data: {}", userData.toString());

        if (userData.has("error")) {
            if (userData.get("error").asText().equals("User not found")) {
                response.status(404); // Not Found
            } else {
                response.status(400); // Bad Request
            }
        } else {
            response.status(200); // OK
        }

        return userData;
    }

    public ObjectNode createUser(Request request, Response response) {
        String username = request.queryParams("username");
        String email = request.queryParams("email");
        String name = request.queryParams("name");
        logger.info("Creating user with username: {}, email: {} and fullname: {}", username, email, name);

        ObjectNode result = userService.createUser(username, email, name);
        if (result.has("message") && result.get("message").asText().equals("User created successfully")) {
            response.status(201); // Created
        } else {
            response.status(500); // Internal Server Error
        }
        return result;
    }

    public ObjectNode getAllUsers(Request request, Response response) {
        logger.info("Fetching all users");

        ObjectNode result = userService.getAllUsers();
        if (!result.isEmpty()) {
            response.status(200); // OK
        } else {
            response.status(500); // Internal Server Error
        }
        return result;
    }

    public ObjectNode getPost(Request request, Response response) {
        String postId = request.params(":id");
        logger.info("Fetching post data for ID: {}", postId);
        ObjectNode postData = userService.getPostDataById(postId);
        logger.info("Post data: {}", postData.toString());

        if (postData.has("error")) {
            if (postData.get("error").asText().equals("Post not found")) {
                response.status(404); // Not Found
            } else {
                response.status(400); // Bad Request
            }
        } else {
            response.status(200); // OK
        }

        return postData;
    }

    public ObjectNode getUserPosts(Request request, Response response) {
        String username = request.params(":username");
        logger.info("Fetching posts for username: {}", username);
        ObjectNode userPosts = userService.getUserPosts(username);
        logger.info("User posts: {}", userPosts.toString());

        if (userPosts.has("error")) {
            if (userPosts.get("error").asText().equals("User not found")) {
                response.status(404); // Not Found
            } else {
                response.status(400); // Bad Request
            }
        } else {
            response.status(200); // OK
        }

        return userPosts;
    }
}
