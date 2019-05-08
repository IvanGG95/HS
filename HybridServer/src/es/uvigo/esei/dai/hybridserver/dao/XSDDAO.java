package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.SQLException;
import java.util.List;

public class XSDDAO {
	private DAOHelper helper;

	public XSDDAO(List<String> parametrosConexion) {
		helper = new DAOHelper("XSD", parametrosConexion);
	}
	
	public String create(String content) throws SQLException {
		return this.helper.create(content);
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
}