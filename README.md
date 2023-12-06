# Database_Assignment2
Clone the Repository: Clone the application repository to your local machine using Git.(git clone https://github.com/khudiyevrauf/Database_Assignment2.git) 
Database Setup: Ensure PostgreSQL is installed and running on your system. Create a database named as you wish.
Database Tables: Create the necessary tables (Books, Authors, Customers, Orders) in your database.
JDBC Driver: Download and add the PostgreSQL JDBC driver to your project’s library.(https://jdbc.postgresql.org/download/)

In the application's source code, update the database URL, username, and password in the managing class to match your PostgreSQL settings:
private static final String URL = "jdbc:postgresql://localhost:5432/your_database_name";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";

Open the Project: Open the project in your IDE (e.g., IntelliJ IDEA).
Run the Application: Execute the managing class. This will start the application and prompt you with options in the console.
Choose an Operation: Select from the given operations (Insert, Retrieve, Update, Delete, Table Structure, Table Info) by entering the corresponding number or name.
Follow Prompts: Depending on your selected operation, follow the on-screen prompts to interact with the database.

