package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final String DATABASE_URL = "jdbc:sqlite:database.sqlite";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ObjectNode fetchUserDataByUsername(String username) {
        ObjectNode response = objectMapper.createObjectNode();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                String sql = "SELECT * FROM users WHERE username = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                ResultSet resultSet = pstmt.executeQuery();

                if (resultSet.next()) {
                    mapData(resultSet, response);
                } else {
                    response.put("error", "User not found");
                }
            }
        } catch (SQLException e) {
            logger.error("Database error occurred", e);
            response.put("error", "Database error occurred");
        }
        return response;
    }

    public ObjectNode insertUser(String username, String email, String name) {
        ObjectNode response = objectMapper.createObjectNode();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                String sql = "INSERT INTO users (username, email, name) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, name);
                int rowsAffected = pstmt.executeUpdate();
                logger.info("Rows affected: {}", rowsAffected);

                if (rowsAffected > 0) {
                    response.put("message", "User created successfully");
                } else {
                    response.put("error", "Failed to create user");
                }
            }
        } catch (SQLException e) {
            logger.error("Database error occurred", e);
            response.put("error", "Database error occurred");
        }
        return response;
    }

    public ObjectNode fetchUsers() {
        ObjectNode response = objectMapper.createObjectNode();
        ArrayNode usersArray = objectMapper.createArrayNode();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                String sql = "SELECT * FROM users";
                Statement stmt = conn.createStatement();
                ResultSet resultSet = stmt.executeQuery(sql);

                while (resultSet.next()) {
                    ObjectNode userNode = objectMapper.createObjectNode();
                    mapData(resultSet, userNode);
                    usersArray.add(userNode);
                }
            }
        } catch (SQLException e) {
            logger.error("Database error occurred", e);
            response.put("error", "Database error occurred");
            return response;
        }

        response.set("users", usersArray);
        return response;
    }

    public ObjectNode fetchPostDataById(int postId) {
        ObjectNode response = objectMapper.createObjectNode();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                String sql = "SELECT * FROM posts WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, postId);
                ResultSet resultSet = pstmt.executeQuery();

                if (resultSet.next()) {
                    mapData(resultSet, response);
                } else {
                    response.put("error", "Post not found");
                }
            }
        } catch (SQLException e) {
            logger.error("Database error occurred", e);
            response.put("error", "Database error occurred");
        }
        return response;
    }

    public ObjectNode fetchUserPosts(String username) {
        ObjectNode response = objectMapper.createObjectNode();
        ArrayNode postsArray = objectMapper.createArrayNode();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                String sql = "SELECT posts.* FROM posts " +
                        "JOIN users ON posts.user_id = users.id " +
                        "WHERE users.username = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                ResultSet resultSet = pstmt.executeQuery();

                while (resultSet.next()) {
                    ObjectNode postNode = objectMapper.createObjectNode();
                    mapData(resultSet, postNode);
                    postsArray.add(postNode);
                }
            }
        } catch (SQLException e) {
            logger.error("Database error occurred", e);
            response.put("error", "Database error occurred");
            return response;
        }

        response.set("posts", postsArray);
        return response;
    }

    private void mapData(ResultSet resultSet, ObjectNode userNode) throws SQLException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = resultSet.getMetaData().getColumnName(i);
            Object value = resultSet.getObject(i);

            // If the value is a Date, convert it to a String
            if (value instanceof Date) {
                userNode.put(columnName, value.toString());
            } else {
                userNode.put(columnName, value != null ? value.toString() : null); // Convert other types to String
            }
        }
    }


}
