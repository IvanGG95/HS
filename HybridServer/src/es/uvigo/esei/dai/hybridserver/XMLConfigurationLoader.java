/**
 *  HybridServer
 *  Copyright (C) 2017 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class XMLConfigurationLoader {
	public XMLConfigurationLoader() {
		
	}
	public Configuration load(File xmlFile) throws Exception {
		// Construcción del schema
				SchemaFactory schemaFactory = 
					SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = schemaFactory.newSchema(new File("configuration.xsd"));
				
				// Construcción del parser del documento. Se establece el esquema y se activa
				// la validación y comprobación de namespaces
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(false);
				factory.setNamespaceAware(true);
				factory.setSchema(schema);
				
				// Se añade el manejador de errores
				DocumentBuilder builder = factory.newDocumentBuilder();
				builder.setErrorHandler(new SimpleErrorHandler());
				Document document = builder.parse(xmlFile);
				
				
				
				NodeList servers = document.getChildNodes().item(0).getChildNodes().item(5).getChildNodes();
				int numServers = 0;
				List<ServerConfiguration> sConf = new ArrayList<>();
				for(int i = 1; i < servers.getLength()-1; i = i + 2) {
					ServerConfiguration SC = new ServerConfiguration();
					
					SC.setName(document.getChildNodes().item(0).getChildNodes().item(5).getChildNodes().item(i).getAttributes().getNamedItem("name").getNodeValue());
					SC.setWsdl(document.getChildNodes().item(0).getChildNodes().item(5).getChildNodes().item(i).getAttributes().getNamedItem("wsdl").getNodeValue());
					SC.setNamespace(document.getChildNodes().item(0).getChildNodes().item(5).getChildNodes().item(i).getAttributes().getNamedItem("namespace").getNodeValue());
					SC.setService(document.getChildNodes().item(0).getChildNodes().item(5).getChildNodes().item(i).getAttributes().getNamedItem("service").getNodeValue());
					SC.setHttpAddress(document.getChildNodes().item(0).getChildNodes().item(5).getChildNodes().item(i).getAttributes().getNamedItem("httpAddress").getNodeValue());
					numServers++;
					sConf.add(SC);
				}
				
				return new Configuration(Integer.parseInt(document.getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(1).getTextContent()),
						Integer.parseInt(document.getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(5).getTextContent()),
						document.getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(3).getTextContent(),
						document.getChildNodes().item(0).getChildNodes().item(3).getChildNodes().item(1).getTextContent(),
						document.getChildNodes().item(0).getChildNodes().item(3).getChildNodes().item(3).getTextContent(),
						document.getChildNodes().item(0).getChildNodes().item(3).getChildNodes().item(5).getTextContent(),
						sConf);
			
	}
}
