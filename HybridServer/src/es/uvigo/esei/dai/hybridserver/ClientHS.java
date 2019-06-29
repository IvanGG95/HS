package es.uvigo.esei.dai.hybridserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import es.uvigo.esei.dai.hybridserver.dao.HTMLException;

public class ClientHS {
	private ArrayList<ServerConfiguration> servers;

	public ClientHS(ArrayList<ServerConfiguration> servers) {
		this.servers = new ArrayList();
		this.servers = servers;
	}

	public ArrayList<String> getRemoteUuid(String type) throws MalformedURLException, SQLException {
		ArrayList<HSService> services = new ArrayList();

		for (int i = 0; servers.size() > i; i++) {
			//System.out.println(servers.get(i).getWsdl()+"   "+servers.get(i).getNamespace()+" "+servers.get(i).getService());
			URL url = new URL(servers.get(i).getWsdl());
			QName qname = new QName(servers.get(i).getNamespace(), servers.get(i).getService()+"Service");
			try {
				Service service = Service.create(url, qname);
				HSService hss = service.getPort(HSService.class);
				services.add(hss);
			}catch(WebServiceException e) {
				//System.err.println("Error en el servicio");
			}
			
			
		}
		ArrayList<String> uuids = new ArrayList();
		for (int i = 0; services.size() > i; i++) {
			switch (type) {
			case "HTML":
				for (String uuid : services.get(i).HTMLuuidList()) {
					if (!uuids.contains(uuid)) {
						uuids.add(uuid);
					}
				}

				break;
			case "XML":
				for (String uuid : services.get(i).XMLuuidList()) {
					if (!uuids.contains(uuid)) {
						uuids.add(uuid);
					}
				}

				break;
			case "XSD":
				for (String uuid : services.get(i).XSDuuidList()) {
					if (!uuids.contains(uuid)) {
						uuids.add(uuid);
					}
				}

				break;
			case "XSLT":
				for (String uuid : services.get(i).XSLTuuidList()) {
					if (!uuids.contains(uuid)) {
						uuids.add(uuid);
					}
				}

				break;
			}

		}

		return uuids;
	}

	public String getRemoteContent(String uuid, String type) throws MalformedURLException, SQLException, HTMLException {
		ArrayList<HSService> services = new ArrayList();

		for (int i = 0; servers.size() > i; i++) {
			URL url = new URL(servers.get(i).getWsdl());
			QName qname = new QName(servers.get(i).getNamespace(), servers.get(i).getService()+"Service");
			try {
				Service service = Service.create(url, qname);
				HSService hss = service.getPort(HSService.class);
				services.add(hss);
			}catch(WebServiceException e) {
				//System.err.println("Error en el servicio");
				
			}
		}
		
		String toret = null;
		switch (type) {
		case "HTML":
			for (int i = 0; services.size() > i; i++) {
				
				if (services.get(i).HTMLuuidList().contains(uuid)) {
					toret = services.get(i).HTMLgetContent(uuid);
				}

			}
			break;
		case "XML":
			for (int i = 0; services.size() > i; i++) {
				if (services.get(i).XMLuuidList().contains(uuid)) {
					toret = services.get(i).XMLgetContent(uuid);
				}

			}
			break;
		case "XSD":
			for (int i = 0; services.size() > i; i++) {
				if (services.get(i).XSDuuidList().contains(uuid)) {
					toret = services.get(i).XSDgetContent(uuid);
				}

			}
			break;
		case "XSLT":
			for (int i = 0; services.size() > i; i++) {
				if (services.get(i).XSLTuuidList().contains(uuid)) {
					toret = services.get(i).XSLTgetContent(uuid);
				}

			}
			break;
		}

		return toret;
	}
	
	public String getRemoteXSLTgetUuidXSD(String uuid) throws SQLException, HTMLException, MalformedURLException {
		ArrayList<HSService> services = new ArrayList();

		for (int i = 0; servers.size() > i; i++) {
			URL url = new URL(servers.get(i).getWsdl());
			QName qname = new QName(servers.get(i).getNamespace(), servers.get(i).getService()+"Service");
			try {
				Service service = Service.create(url, qname);
				HSService hss = service.getPort(HSService.class);
				services.add(hss);
			}catch(WebServiceException e) {
				System.err.println("Error en el servicio");
			}
		}
		
		String toret = null;
		for (int i = 0; services.size() > i; i++) {
			if (services.get(i).XSLTuuidList().contains(uuid)) {
				toret = services.get(i).XSLTgetUuidXSD(uuid);
			}

		}
		
		return toret;
	}
}
