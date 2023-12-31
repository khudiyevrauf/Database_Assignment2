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
                case "1":
                case "insert":
                    insertRecord(scanner, connection);
                    break;

                case "2":
                case "retrieve":
                    retrieveRecord(connection);
                    break;

                case "3":
                case "update":
                    updateRecord(scanner, connection);
                    break;

                case "4":
                case "delete":
                    deleteRecord(scanner, connection);
                    break;

                case "5":
                case "table structure":
                    displayStructureOfTables(connection);
                    break;

                case "6":
                case "table info":
                    System.out.print("Table name: ");
                    String nameTable = scanner.nextLine();
                    extractMetadata(connection, nameTable);
                    break;

                default:
                    System.out.println("Please, choose a valid option.");
                    break;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void insertRecord(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Choose table to insert into: \n1. Book\n2. Author\n3. Customer\n4. Order");
        String table = scanner.nextLine();

        switch (table.toLowerCase()) {

            case "1":
            case "book":
                insertBook(scanner, connection);
                break;

            case "2":
            case "author":
                insertAuthor(scanner, connection);
                break;

            case "3":
            case "customer":
                insertCustomer(scanner, connection);
                break;

            case "4":
            case "order":
                insertOrder(scanner, connection);
                break;

            default:
                System.out.println("Invalid table.");
        }
    }


    private static void insertOrder(Scanner scanner, Connection connection) throws SQLException {

        System.out.println("Inserting a new order.");
        System.out.print("Enter order ID: ");
        int orderId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter customer ID: ");
        int customerId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter order quantity: ");
        int orderQuantity = Integer.parseInt(scanner.nextLine());

        String checkVolumeSql = "SELECT volume FROM Books WHERE id_book = ?";
        String insertOrderSql = "INSERT INTO Orders (id_order, id_customer, id_book, order_quantity) VALUES (?, ?, ?, ?)";
        String updateBookVolumeSql = "UPDATE Books SET volume = volume - ? WHERE id_book = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement checkVolumeStmt = connection.prepareStatement(checkVolumeSql)) {
                checkVolumeStmt.setInt(1, bookId);
                ResultSet resultSet = checkVolumeStmt.executeQuery();
                if (!resultSet.next() || resultSet.getInt("volume") < orderQuantity) {
                    throw new SQLException("Not enough volume for book ID: " + bookId);
                }
            }

            try (PreparedStatement insertOrderStmt = connection.prepareStatement(insertOrderSql)) {
                insertOrderStmt.setInt(1, orderId);
                insertOrderStmt.setInt(2, customerId);
                insertOrderStmt.setInt(3, bookId);
                insertOrderStmt.setInt(4, orderQuantity);
                insertOrderStmt.executeUpdate();
            }

            try (PreparedStatement updateBookVolumeStmt = connection.prepareStatement(updateBookVolumeSql)) {
                updateBookVolumeStmt.setInt(1, orderQuantity);
                updateBookVolumeStmt.setInt(2, bookId);
                updateBookVolumeStmt.executeUpdate();
            }

            connection.commit();
            System.out.println("Order inserted and book volume updated.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void insertBook(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Inserting a new book.");
        System.out.print("Enter book ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter author ID: ");
        int authorId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter quantity of pages: ");
        int pages = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter volume: ");
        int volume = Integer.parseInt(scanner.nextLine());

        String sql = "INSERT INTO Books (id_book, id_author, title, pages_quantity, volume) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, authorId);
            stmt.setString(3, title);
            stmt.setInt(4, pages);
            stmt.setInt(5, volume);
            int rowsInserted = stmt.executeUpdate();
            System.out.println(rowsInserted + " book(s) inserted.");
        }
    }

    private static void insertAuthor(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Inserting a new author.");
        System.out.print("Enter author ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter author's name: ");
        String name = scanner.nextLine();

        String sql = "INSERT INTO Authors (id_author, name_author) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            int rowsInserted = stmt.executeUpdate();
            System.out.println(rowsInserted + " author(s) inserted.");
        }
    }

    private static void insertCustomer(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Inserting a new customer.");
        System.out.print("Enter customer ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter customer's name: ");
        String name = scanner.nextLine();

        String sql = "INSERT INTO Customers (id_customer, name_customer) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            int rowsInserted = stmt.executeUpdate();
            System.out.println(rowsInserted + " customer(s) inserted.");
        }
    }

    private static void retrieveRecord(Connection connection) throws SQLException {
        String joinSql = "SELECT b.title, b.volume, a.name_author, o.order_quantity " +
                "FROM Books b " +
                "JOIN Authors a ON b.id_author = a.id_author " +
                "JOIN Orders o ON b.id_book = o.id_book";

        System.out.println("Retrieving all books with authors and orders:");
        try (PreparedStatement stmt = connection.prepareStatement(joinSql);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                int volume = resultSet.getInt("volume");
                String author = resultSet.getString("name_author");
                int orderQuantity = resultSet.getInt("order_quantity");
                System.out.println("Title: " + title + ", Volume: " + volume + ", Author: " + author + ", Ordered: " + orderQuantity);
            }
        }
    }

    private static void updateRecord(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Updating a book record.");
        System.out.print("Enter the ID of the book you want to update: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();

        String sql = "UPDATE Books SET title = ? WHERE id_book = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setInt(2, id);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " rows updated.");
        }
    }

    private static void deleteRecord(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Deleting a book record.");
        System.out.print("Enter the ID of the book you want to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        String sql = "DELETE FROM Books WHERE id_book = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted + " rows deleted.");

        }
    }

    private static void extractMetadata(Connection conn, String specifiedTable) {
        try {
            showDetailsOfColumns(conn, specifiedTable);
            showPrimaryKeysInfo(conn, specifiedTable);
            showForeignKeysDetails(conn, specifiedTable);
        } catch (SQLException e) {
            System.out.println("Failed to retrieve metadata for table: " + specifiedTable);
            e.printStackTrace();
        }
    }

    private static void displayStructureOfTables(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();

        try (ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE"})) {
            System.out.println("Overview of Database Tables:");
            while (resultSet.next()) {
                System.out.println("Table Name: " + resultSet.getString("TABLE_NAME") +
                        ", Type: " + resultSet.getString("TABLE_TYPE"));
            }
        }
    }

    private static void showDetailsOfColumns(Connection conn, String specifiedTable) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();

        System.out.println("Details of columns for " + specifiedTable + ":");
        try (ResultSet resultSet = metaData.getColumns(null, null, specifiedTable, null)) {
            while (resultSet.next()) {
                System.out.println("Column: " + resultSet.getString("COLUMN_NAME") +
                        ", Type: " + resultSet.getString("TYPE_NAME") +
                        ", Size: " + resultSet.getInt("COLUMN_SIZE"));
            }
        }
    }

    private static void showPrimaryKeysInfo(Connection conn, String specifiedTable) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();

        System.out.println("Primary Key Information for " + specifiedTable + ":");
        try (ResultSet resultSet = metaData.getPrimaryKeys(null, null, specifiedTable)) {
            while (resultSet.next()) {
                System.out.println("Column: " + resultSet.getString("COLUMN_NAME") +
                        ", Key Name: " + resultSet.getString("PK_NAME"));
            }
        }
    }

    private static void showForeignKeysDetails(Connection conn, String specifiedTable) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();

        System.out.println("Foreign Key Constraints in " + specifiedTable + ":");
        try (ResultSet resultSet = metaData.getImportedKeys(null, null, specifiedTable)) {
            while (resultSet.next()) {
                System.out.println("FK Name: " + resultSet.getString("FK_NAME") +
                        ", FK Column: " + resultSet.getString("FKCOLUMN_NAME") +
                        ", Reference Table: " + resultSet.getString("PKTABLE_NAME") +
                        ", Reference Column: " + resultSet.getString("PKCOLUMN_NAME"));
            }
        }
    }


}