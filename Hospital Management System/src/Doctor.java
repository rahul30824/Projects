package HospitalManagementSystem;

import java.sql.*;

public class Doctor {
    private final Connection connection;

    public Doctor(Connection connection) {
        this.connection = connection;
    }

    public void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            System.out.println("\n📋 Doctor List:");
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id") +
                        ", Name: " + resultSet.getString("name") +
                        ", Specialization: " + resultSet.getString("specialization"));
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error Fetching Doctors: " + e.getMessage());
        }
    }

    public boolean getDoctorById(int doctorId) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error Fetching Doctor: " + e.getMessage());
        }
        return false;
    }
}
