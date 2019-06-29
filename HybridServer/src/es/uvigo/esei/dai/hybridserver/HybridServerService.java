package es.uvigo.esei.dai.hybridserver;

import java.sql.SQLException;
import java.util.List;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.dao.HTMLDAO;
import es.uvigo.esei.dai.hybridserver.dao.HTMLException;
import es.uvigo.esei.dai.hybridserver.dao.XMLDAO;
import es.uvigo.esei.dai.hybridserver.dao.XSDDAO;
import es.uvigo.esei.dai.hybridserver.dao.XSLTDAO;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HSService")
public class HybridServerService implements HSService {
	private List<String> parametrosConexion;

	public HybridServerService(List<String> parametrosConexion) {
		this.parametrosConexion = parametrosConexion;
	}

	@Override
	public List<String> HTMLuuidList() throws SQLException {
		HTMLDAO htmlDAO = new HTMLDAO(parametrosConexion);
		return htmlDAO.uuidList();
	}

	@Override
	public List<String> XMLuuidList() throws SQLException {
		XMLDAO xmlDAO = new XMLDAO(parametrosConexion);
		return xmlDAO.uuidList();
	}

	@Override
	public List<String> XSDuuidList() throws SQLException {
		XSDDAO xsdDAO = new XSDDAO(parametrosConexion);
		return xsdDAO.uuidList();
	}

	@Override
	public List<String> XSLTuuidList() throws SQLException {
		XSLTDAO xsltDAO = new XSLTDAO(parametrosConexion);
		return xsltDAO.uuidList();
	}

	@Override
	public String HTMLgetContent(String uuid) throws HTMLException, SQLException {
		HTMLDAO htmlDAO = new HTMLDAO(parametrosConexion);
		return htmlDAO.getContent(uuid);
	}

	@Override
	public String XMLgetContent(String uuid) throws HTMLException, SQLException {
		XMLDAO xmlDAO = new XMLDAO(parametrosConexion);
		return xmlDAO.getContent(uuid);
	}

	@Override
	public String XSDgetContent(String uuid) throws HTMLException, SQLException {
		XSDDAO xsdDAO = new XSDDAO(parametrosConexion);
		return xsdDAO.getContent(uuid);
	}

	@Override
	public String XSLTgetContent(String uuid) throws HTMLException, SQLException {
		XSLTDAO xsltDAO = new XSLTDAO(parametrosConexion);
		return xsltDAO.getContent(uuid);
	}

	@Override
	public String XSLTgetUuidXSD(String uuid) throws SQLException {
		XSLTDAO xsltDAO = new XSLTDAO(parametrosConexion);
		return xsltDAO.getUuidXSD(uuid);
	}

}
