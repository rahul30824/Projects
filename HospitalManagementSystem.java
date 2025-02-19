package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Ragul@24";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("⚠️ MySQL JDBC Driver Not Found! " + e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("\n🛑 HOSPITAL MANAGEMENT SYSTEM 🏥");
                System.out.println("1️ ADD Patient");
                System.out.println("2️ View Patients");
                System.out.println("3️ view Dictors");
                System.out.println("4️ BookAppointment");
                System.out.println("5️ Exit");
                System.out.print("Enter your choice: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("❌ Invalid input! Please enter a number.");
                    scanner.next(); // Clear invalid input
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatients();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        break;
                    case 5:
                        System.out.println("✅ THANK YOU FOR USING HOSPITAL MANAGEMENT SYSTEM! 🚀");
                        return;
                    default:
                        System.out.println("❌ Invalid choice! Please enter a number between 1 and 5.");
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Database Connection Error: " + e.getMessage());
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        if (!scanner.hasNextInt()) {
            System.out.println("❌ Invalid input! Patient ID must be a number.");
            scanner.next();
            return;
        }
        int patientId = scanner.nextInt();

        System.out.print("Enter Doctor ID: ");
        if (!scanner.hasNextInt()) {
            System.out.println("❌ Invalid input! Doctor ID must be a number.");
            scanner.next();
            return;
        }
        int doctorId = scanner.nextInt();

        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery)) {
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("✅ Appointment Successfully Booked!");
                    } else {
                        System.out.println("❌ Failed to Book Appointment!");
                    }
                } catch (SQLException e) {
                    System.err.println("⚠️ Error Booking Appointment: " + e.getMessage());
                }
            } else {
                System.out.println("❌ Doctor is NOT available on this date!");
            }
        } else {
            System.out.println("❌ Either the Patient or Doctor ID is invalid!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0; // If count is 0, doctor is available
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error Checking Doctor Availability: " + e.getMessage());
        }
        return false;
    }
}
