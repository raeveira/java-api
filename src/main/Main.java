package main;

import spark.Spark;

public class Main {
    public static void main(String[] args) {
        Spark.port(8080);
        Spark.ipAddress("localhost");
        System.out.println("Server started at http://localhost:8080");

        UserController userController = new UserController();

        Spark.get("/user/:username", userController::getUser);
        Spark.get("/users", userController::getAllUsers);
        Spark.get("/posts/:id", userController::getPost);
        Spark.get("/user/:username/posts", userController::getUserPosts);
        Spark.post("/users", userController::createUser);

        // Exception handling
        Spark.exception(Exception.class, (exception, request, response) -> {
            System.err.println("Internal server error occurred: " + exception.getMessage());
            response.status(500);
            response.body("Internal server error occurred");
        });
    }
}
