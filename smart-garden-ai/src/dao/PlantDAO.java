package dao;

import java.sql.*;


public class PlantDAO {

    public void savePlant(models.Plant plant, int userId) throws SQLException {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "INSERT INTO plant (name, type, soil_moisture, temperature, humidity, light, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, plant.getName());
            stmt.setString(2, plant.getType());
            stmt.setString(3, plant.getSoilMoisture());
            stmt.setString(4, plant.getTemperature());
            stmt.setString(5, plant.getHumidity());
            stmt.setString(6, plant.getLight());
            stmt.setInt(7, userId);
            stmt.executeUpdate();

            int plantId = getPlantIdByName(plant.getName());
            if (plant.getSymptoms() != null) {
                for (String symptom : plant.getSymptoms()) {
                    saveSymptom(symptom, plantId);
                }
            }
        }
    }

    public void saveSymptom(String symptom, int plantId) throws SQLException {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "INSERT INTO symptom (name, plant_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, symptom);
            stmt.setInt(2, plantId);
            stmt.executeUpdate();
        }
    }

    public int getPlantIdByName(String plantName) throws SQLException {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "SELECT id FROM plant WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, plantName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        return -1;
    }
}
