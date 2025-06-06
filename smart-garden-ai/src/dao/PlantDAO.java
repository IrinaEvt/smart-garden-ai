package dao;

import models.Plant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PlantDAO {

    public int savePlant(models.Plant plant, int userId) throws SQLException {
        int plantId = 0;
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

            plantId = getPlantIdByName(plant.getName());
            if (plant.getSymptoms() != null) {
                for (String symptom : plant.getSymptoms()) {
                    saveSymptom(symptom, plantId);
                }
            }
        }
        return plantId;
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

    public List<Plant> getPlantsByUserId(int userId) throws SQLException {
        List<Plant> plants = new ArrayList<>();

        String query = "SELECT id, name, type, soil_moisture, temperature, humidity, light FROM plant WHERE user_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Plant plant = new Plant();
                plant.setName(rs.getString("name"));
                plant.setType(rs.getString("type"));
                plant.setSoilMoisture(rs.getString("soil_moisture"));
                plant.setTemperature(rs.getString("temperature"));
                plant.setHumidity(rs.getString("humidity"));
                plant.setLight(rs.getString("light"));
                plants.add(plant);
            }
        }

        return plants;
    }

    public List<String> getSymptomsByPlantId(int plantId) throws SQLException {
        List<String> symptoms = new ArrayList<>();
        String query = "SELECT name FROM symptom WHERE plant_id = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, plantId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                symptoms.add(rs.getString("name"));
            }
        }

        return symptoms;
    }
}
