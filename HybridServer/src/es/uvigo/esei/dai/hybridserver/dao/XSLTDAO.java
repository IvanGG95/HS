package es.uvigo.esei.dai.hybridserver.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class XSLTDAO {
	private List<String> parametrosConexion;
	private DAOHelper helper;

	public XSLTDAO(List<String> parametrosConexion) {
		this.parametrosConexion = parametrosConexion;
		helper = new DAOHelper("XSLT", parametrosConexion);
	}
	
	public String create(String content, String uuidXSD) throws SQLException {
		UUID randomUuid = UUID.randomUUID();
		String uuid = randomUuid.toString();
		String query = "INSERT INTO XSLT (uuid, content, xsduuid) " + "VALUES (?, ?, ?)";
		try (Connection connection = DriverManager.getConnection(parametrosConexion.get(0), parametrosConexion.get(1), parametrosConexion.get(2))) {
			try (PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setString(1, uuid);
				statement.setString(2, content);
				statement.setString(3, uuidXSD);

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
		this.helper.delete(uuid);
	}
	
	public String getContent(String uuid) throws HTMLException, SQLException {
		return this.helper.getContent(uuid);
	}
	
	public List<String> uuidList() throws SQLException {
		return this.helper.uuidList();
	}
	
	public String getUuidXSD(String uuid) throws SQLException {
		String query = "SELECT xsduuid " + "FROM XSLT " + "WHERE uuid = ?";
		try (Connection connection = DriverManager.getConnection(parametrosConexion.get(0), parametrosConexion.get(1), parametrosConexion.get(2))) {
			try (PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setString(1, uuid);

				try (ResultSet result = statement.executeQuery()) {
					result.next();
					return result.getString("xsduuid");
					
				}

			} catch (SQLException e) {
				throw new RuntimeException("Error en la base de datos");
			}
		}
	}
}
