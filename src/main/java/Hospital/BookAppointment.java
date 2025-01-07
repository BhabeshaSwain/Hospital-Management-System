package Hospital;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookAppointment {
	private static final String url = "jdbc:mysql://localhost:3306/hospital";
	private static final String user = "root";
	private static final String password = "bhabesha";

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Connection con = DriverManager.getConnection(url, user, password);
			Patient patient = new Patient(con, sc);
			Doctor doctor = new Doctor(con);
			while (true) {
				System.out.println("HOSPITAL MANAGEMENT SYSTEM");
				System.out.println("1. Add Patient");
				System.out.println("2. View Patients");
				System.out.println("3. View Doctors");
				System.out.println("4. Book Appointment");
				System.out.println("5. Exit");
				System.out.println("Enter Your Choice");

				int choice = sc.nextInt();
				switch (choice) {
				case 1:
					patient.addPatient();
					System.out.println();
					break;
				case 2:
					patient.viewPatients();
					System.out.println();
					break;
				case 3:
					doctor.viewDoctors();
					System.out.println();
					break;
				case 4:
					bookAppointment(patient, doctor, con, sc);
					System.out.println();
					break;
				case 5:
					return;
				default:
					System.out.println("Enter valid choice");
					break;

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void bookAppointment(Patient patient, Doctor doctor, Connection con, Scanner sc) {
		System.out.println("Enter Patient Id: ");
		int patientId = sc.nextInt();
		System.out.println("Enter Doctor Id: ");
		int doctorId = sc.nextInt();
		System.out.println("Enter appointment date (YYYY-MM-DD): ");
		String appointmentDate = sc.next();

		if (patient.getPatientId(patientId) && (doctor.getDoctorId(doctorId))) {
			if (checkDoctorAvailability(doctorId, appointmentDate, con)) {
				String appointmentQuery = "insert into appointments(patient_id, doctor_id, appointment_date) values(?,?,?)";
				try {
					PreparedStatement ps = con.prepareStatement(appointmentQuery);
					ps.setInt(1, patientId);
					ps.setInt(2, doctorId);
					ps.setString(3, appointmentDate);

					int rowsAffected = ps.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Appointment Booked!");
					} else {
						System.out.println("Failed to Book Appointment!");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Doctor not available on this Date!");
			}
		} else {
			System.out.println("Either Doctor or Patient doesn't exit!");
		}
	}

	private static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection con) {
		String query = "select count(*) from appointments where doctor_id = ? AND appointment_date = ?";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, doctorId);
			ps.setString(2, appointmentDate);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int count = rs.getInt(1);
				if (count == 0) {
					return true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

}
