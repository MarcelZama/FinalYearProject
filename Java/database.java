import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class database {
        // JDBC URL, username, and password of MySQL server
        private static final String JDBC_URL = "jdbc:sqlite:sample.db";
        private static final String USER = "";
        private static final String PASSWORD = "";

        // SQL query to retrieve data
        private static final String SELECT_QUERY = "SELECT id, username, email FROM users";


    try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY);
                ResultSet resultSet = preparedStatement.executeQuery()) {

                // Display column names
                System.out.printf("%-5s %-15s %-30s%n", "ID", "USERNAME", "EMAIL");
                System.out.println("--------------------------------------------");

                // Iterate through the result set and display data
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");

                    System.out.printf("%-5d %-15s %-30s%n", id, username, email);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
