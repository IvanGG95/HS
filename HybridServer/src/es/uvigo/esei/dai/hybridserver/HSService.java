package es.uvigo.esei.dai.hybridserver;

import java.sql.SQLException;
import java.util.List;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.dao.HTMLException;

@WebService
public interface HSService {

	public List<String> HTMLuuidList() throws SQLException;

	public List<String> XMLuuidList()throws SQLException;

	public List<String> XSDuuidList()throws SQLException;

	public List<String> XSLTuuidList()throws SQLException;

	public String HTMLgetContent(String uuid) throws HTMLException, SQLException;

	public String XMLgetContent(String uuid) throws HTMLException, SQLException;

	public String XSDgetContent(String uuid) throws HTMLException, SQLException;

	public String XSLTgetContent(String uuid) throws HTMLException, SQLException;
	
	public String XSLTgetUuidXSD(String uuid) throws SQLException;

}
