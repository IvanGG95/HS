package es.uvigo.esei.dai.hybridserver.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DAOHelper {
	private String tipoFichero;
	private List<String> parametrosConexion;
	
	public DAOHelper(String tipoFichero, List<String> parametrosConexion) {
		this.tipoFichero = tipoFichero;
		this.parametrosConexion = parametrosConexion;
	}
	
	public String create(String content) throws SQLException {
		UUID randomUuid = UUID.randomUUID();
		String uuid = randomUuid.toString();
		String query = "INSERT INTO " + tipoFichero.toUpperCase() + " (uuid, content) " + "VALUES (?, ?)";
		try (Connection connection = DriverManager.getConnection(parametrosConexion.get(0), parametrosConexion.get(1), parametrosConexion.get(2))) {
			try (PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setString(1, uuid);
				statement.setString(2, content);

				if (statement.executeUpdate() != 1) {
					throw new SQLException("Error al hacer la inserción");
				}
			} catch (SQLException e) {
				throw new RuntimeException("Error en la base de datos");
			}
		}
		return uuid;
	}
	
	public void delete(String uuid) throws HTMLException, SQLException {
		String query = "DELETE FROM " + tipoFichero.toUpperCase() + " WHERE uuid = ?";
		try (Connection connection = DriverManager.getConnection(parametrosConexion.get(0), parametrosConexion.get(1), parametrosConexion.get(2))) {
			try (PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setString(1, uuid);

				if (statement.executeUpdate() != 1) {
					throw new SQLException("Error al eliminar el uuid: " + uuid);
				}

			} catch (SQLException e) {
				throw new RuntimeException("Error en la base de datos");
			}
		}
	}
	
	public String getContent(String uuid) throws HTMLException, SQLException {
		String query = "SELECT content " + "FROM " + tipoFichero.toUpperCase() + " WHERE uuid = ?";
		try (Connection connection = DriverManager.getConnection(parametrosConexion.get(0), parametrosConexion.get(1), parametrosConexion.get(2))) {
			try (PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setString(1, uuid);

				try (ResultSet result = statement.executeQuery()) {
					result.next();
					return result.getString("content");
					
				}

			} catch (SQLException e) {
				throw new RuntimeException("Error en la base de datos");
			}
		}
	}
	
	public List<String> uuidList() throws SQLException {
		List<String> list = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(parametrosConexion.get(0), parametrosConexion.get(1), parametrosConexion.get(2))) {
			try (Statement statement = connection.createStatement()) {

				try (ResultSet result = statement.executeQuery("SELECT uuid FROM " + tipoFichero.toUpperCase())) {
					while (result.next()) {
						list.add(result.getString("uuid"));
					}

					return list;
				}

			} catch (SQLException e) {
				throw new RuntimeException("Error en la base de datos");
			}
		}
	}

}
