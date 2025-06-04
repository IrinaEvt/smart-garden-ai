package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String url = "jdbc:sqlite:plants.db";

        try {
            // 👉 Това казва на Java да зареди SQLite драйвера от JAR файла
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {

                System.out.println("DB initialize");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY, username TEXT, password TEXT)");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS plant (id INTEGER PRIMARY KEY, name TEXT, type TEXT, soil_moisture TEXT, temperature TEXT, humidity TEXT, light TEXT, user_id INTEGER)");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS symptom (id INTEGER PRIMARY KEY, name TEXT, plant_id INTEGER)");

                System.out.println("✔ Таблиците са готови.");
            }

        } catch (Exception e) {
            System.out.println("❌ Грешка при инициализация на базата: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
