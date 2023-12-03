import java.sql.*;
import java.util.Scanner;

public class managing {

    private static final String URL = "jdbc:postgresql://localhost:5432/test_data";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Choose operation: \n1. Insert\n2. Retrieve\n3. Update\n4. Delete\n5. Table structure\n6. Table info");
            String operation = scanner.nextLine();


            switch (operation.toLowerCase()) {
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

}