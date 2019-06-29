package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.dao.HTMLDAO;
import es.uvigo.esei.dai.hybridserver.dao.HTMLException;
import es.uvigo.esei.dai.hybridserver.dao.XMLDAO;
import es.uvigo.esei.dai.hybridserver.dao.XSDDAO;
import es.uvigo.esei.dai.hybridserver.dao.XSLTDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public class ServiceThread implements Runnable {
	Socket socket;
	private List<String> parametrosConexion;
	private ArrayList<ServerConfiguration> servers;

	public ServiceThread(Socket socket, List<String> parametrosConexion, ArrayList<ServerConfiguration> servers) {
		this.socket = socket;
		this.parametrosConexion = parametrosConexion;
		this.servers = servers;
	}

	@Override
	public void run() {
		HTTPRequest request;
		ClientHS remotes = new ClientHS(servers);
		try {
			request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			OutputStream output;
			if (request.getResourceName().equals("html") || request.getResourceName().equals("xml")
					|| request.getResourceName().equals("xsd") || request.getResourceName().equals("xslt")) {
				switch (request.getResourceName()) {
				case "html":
					HTMLDAO htmlDAO = new HTMLDAO(parametrosConexion);
					switch (request.getMethod()) {
					case POST:
						if (!(request.getResourceParameters().get("html") == null)) {
							String newPage = request.getResourceParameters().get("html");
							String uuid = htmlDAO.create(newPage);
							byte[] content1 = ("<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>").getBytes();
							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 200 OK\r\n".getBytes());
								String contentLenght1 = String.format("Content-Lenght: %d\r\n", content1.length);
								output.write(contentLenght1.getBytes());
								output.write("Content-Type: text/html\r\n".getBytes());
								output.write("\r\n".getBytes());
								output.write(content1);
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;
					case GET:
						if (request.getResourceParameters().containsKey("uuid")) {
							if (htmlDAO.uuidList().contains(request.getResourceParameters().get("uuid")) || remotes
									.getRemoteUuid("HTML").contains(request.getResourceParameters().get("uuid"))) {
								String webPage = "";
								if (htmlDAO.uuidList().contains(request.getResourceParameters().get("uuid"))) {
									webPage = htmlDAO.getContent(request.getResourceParameters().get("uuid"));

								} else if (remotes.getRemoteUuid("HTML")
										.contains(request.getResourceParameters().get("uuid"))) {
									webPage = remotes.getRemoteContent(request.getResourceParameters().get("uuid"),
											"HTML");

								}

								byte[] content = webPage.getBytes();
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 200 OK\r\n".getBytes());
									String contentLenght = String.format("Content-Lenght: %d\r\n", content.length);
									output.write(contentLenght.getBytes());
									output.write("Content-Type: text/html\r\n".getBytes());
									output.write("\r\n".getBytes());
									output.write(content);
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}

							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {

							String pageContainer = "";

							if ((!(htmlDAO.uuidList().isEmpty())) || (!(remotes.getRemoteUuid("HTML").isEmpty()))) {
								ArrayList toPage = (ArrayList) htmlDAO.uuidList();
								for (String uuid : remotes.getRemoteUuid("HTML")) {
									if (!(toPage.contains(uuid))) {
										toPage.add(uuid);
									}
								}
								for (int i = 0; i < toPage.size(); i++) {
									pageContainer += "<a href=\"html?uuid=" + toPage.get(i) + "\">" + toPage.get(i)
											+ "</a>" + "</br>";

								}
								byte[] content = pageContainer.getBytes();

								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 200 OK\r\n".getBytes());
									String contentLenght = String.format("Content-Lenght: %d\r\n", content.length);
									output.write(contentLenght.getBytes());
									output.write("Content-Type: text/html\r\n".getBytes());
									output.write("\r\n".getBytes());
									output.write(content);
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}

						break;
					case DELETE:
						if (request.getResourceParameters().containsKey("uuid")) {
							if (htmlDAO.uuidList().contains(request.getResourceParameters().get("uuid"))) {
								String newid = request.getResourceParameters().get("uuid");
								htmlDAO.delete(newid);
								output = socket.getOutputStream();
								output.write("HTTP/1.1 200 OK\r\n".getBytes());
								output.flush();

							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						} else {

							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						break;
					}

					break;
				case "xml":
					XMLDAO xmldao = new XMLDAO(parametrosConexion);
					switch (request.getMethod()) {
					case POST:
						if (!(request.getResourceParameters().get("xml") == null)) {
							String newPage = request.getResourceParameters().get("xml");
							String uuid = xmldao.create(newPage);
							byte[] content1 = ("<a href=\"xml?uuid=" + uuid + "\">" + uuid + "</a>").getBytes();
							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 200 OK\r\n".getBytes());
								String contentLenght1 = String.format("Content-Lenght: %d\r\n", content1.length);
								output.write(contentLenght1.getBytes());
								output.write("Content-Type: application/xml\r\n".getBytes());
								output.write("\r\n".getBytes());
								output.write(content1);
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;
					case GET:
						if (request.getResourceParameters().containsKey("uuid")
								&& request.getResourceParameters().containsKey("xslt")) {

							XSLTDAO xsltdao = new XSLTDAO(parametrosConexion);
							if ((xmldao.uuidList().contains(request.getResourceParameters().get("uuid")) || remotes
									.getRemoteUuid("XML").contains(request.getResourceParameters().get("uuid")))
									&& (xsltdao.uuidList().contains(request.getResourceParameters().get("xslt"))
											|| remotes.getRemoteUuid("XSLT")
													.contains(request.getResourceParameters().get("xslt")))) {
									String uuidXSLT = request.getResourceParameters().get("xslt");
									String uuidXSD;
									String xsdContent;
									String xmlContent;
									String xsltContent;
									if(xsltdao.uuidList().contains(request.getResourceParameters().get("xslt"))) {
										uuidXSD = xsltdao.getUuidXSD(uuidXSLT);
										XSDDAO xsddao = new XSDDAO(parametrosConexion);
										xsdContent = xsddao.getContent(uuidXSD);
										xsltContent=xsltdao.getContent(uuidXSLT);
									}else {
										uuidXSD = remotes.getRemoteXSLTgetUuidXSD(uuidXSLT);
										xsdContent = remotes.getRemoteContent(uuidXSD, "XSD");
										xsltContent= remotes.getRemoteContent(request.getResourceParameters().get("xslt"), "XSLT");
									}
									if(xmldao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
										 xmlContent = xmldao.getContent(request.getResourceParameters().get("uuid"));
									}else {
										 xmlContent = remotes.getRemoteContent(request.getResourceParameters().get("uuid"), "XML");
									}
									

									
									
									try {
										SchemaFactory schemaFactory = SchemaFactory
												.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
										Schema schema = schemaFactory
												.newSchema(new StreamSource(new StringReader(xsdContent)));

										// Construcción del parser del documento. Se establece el esquema y se activa
										// la validación y comprobación de namespaces
										DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
										factory.setValidating(false);
										factory.setNamespaceAware(true);
										factory.setSchema(schema);

										// Se añade el manejador de errores
										DocumentBuilder builder = factory.newDocumentBuilder();
										builder.setErrorHandler(new SimpleErrorHandler());

										builder.parse(new InputSource(new StringReader(xmlContent)));

										StringWriter writer = new StringWriter();
										transform(new StreamSource(new StringReader(xmlContent)),
												new StreamSource(new StringReader(xsltContent)),
												new StreamResult(writer));

										String webPage = writer.toString();
										byte[] content = webPage.getBytes();

										try {
											output = socket.getOutputStream();
											output.write("HTTP/1.1 200 OK\r\n".getBytes());
											String contentLenght = String.format("Content-Lenght: %d\r\n",
													content.length);
											output.write(contentLenght.getBytes());
											output.write("Content-Type: text/html\r\n".getBytes());
											output.write("\r\n".getBytes());
											output.write(content);
											output.flush();
										} catch (Exception e) {
											e.printStackTrace();
										}
									} catch (SAXException | ParserConfigurationException | TransformerException e) {
										System.err.println("Documento XML no válido");
										try {
											output = socket.getOutputStream();
											output.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
											output.flush();
										} catch (Exception e1) {
											e.printStackTrace();
										}
									}
								
							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}
						if (!(request.getResourceParameters().containsKey("xslt"))) {
							if (request.getResourceParameters().containsKey("uuid")) {
								if (xmldao.uuidList().contains(request.getResourceParameters().get("uuid")) || remotes
										.getRemoteUuid("XML").contains(request.getResourceParameters().get("uuid"))) {
									String webPage = "";
									if (xmldao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
										webPage = xmldao.getContent(request.getResourceParameters().get("uuid"));

									} else if (remotes.getRemoteUuid("XML")
											.contains(request.getResourceParameters().get("uuid"))) {
										webPage = remotes.getRemoteContent(request.getResourceParameters().get("uuid"),
												"XML");

									}
									byte[] content = webPage.getBytes();
									try {
										output = socket.getOutputStream();
										output.write("HTTP/1.1 200 OK\r\n".getBytes());
										String contentLenght = String.format("Content-Lenght: %d\r\n", content.length);
										output.write(contentLenght.getBytes());
										output.write("Content-Type: application/xml\r\n".getBytes());
										output.write("\r\n".getBytes());
										output.write(content);
										output.flush();
									} catch (Exception e) {
										e.printStackTrace();
									}

								} else {
									try {
										output = socket.getOutputStream();
										output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
										output.flush();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

							} else {

								String pageContainer = "";
								if ((!(xmldao.uuidList().isEmpty())) || (!(remotes.getRemoteUuid("XML").isEmpty()))) {
									ArrayList toPage = (ArrayList) xmldao.uuidList();
									for (String uuid : remotes.getRemoteUuid("XML")) {
										if (!(toPage.contains(uuid))) {
											toPage.add(uuid);
										}
									}
									for (int i = 0; i < toPage.size(); i++) {
										pageContainer += "<a href=\"xml?uuid=" + toPage.get(i) + "\">" + toPage.get(i)
												+ "</a>" + "</br>";

									}
									byte[] content = pageContainer.getBytes();

									try {
										output = socket.getOutputStream();
										output.write("HTTP/1.1 200 OK\r\n".getBytes());
										String contentLenght = String.format("Content-Lenght: %d\r\n", content.length);
										output.write(contentLenght.getBytes());
										output.write("Content-Type: text/html\r\n".getBytes());
										output.write("\r\n".getBytes());
										output.write(content);
										output.flush();
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									try {
										output = socket.getOutputStream();
										output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
										output.flush();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

							}
						}

						break;
					case DELETE:
						if (request.getResourceParameters().containsKey("uuid")) {
							if (xmldao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
								String newid = request.getResourceParameters().get("uuid");
								xmldao.delete(newid);
								output = socket.getOutputStream();
								output.write("HTTP/1.1 200 OK\r\n".getBytes());
								output.flush();

							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						} else {

							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						break;
					}

					break;
				case "xsd":

					XSDDAO xsddao = new XSDDAO(parametrosConexion);
					switch (request.getMethod()) {
					case POST:
						if (!(request.getResourceParameters().get("xsd") == null)) {
							String newPage = request.getResourceParameters().get("xsd");
							String uuid = xsddao.create(newPage);
							byte[] content1 = ("<a href=\"xsd?uuid=" + uuid + "\">" + uuid + "</a>").getBytes();
							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 200 OK\r\n".getBytes());
								String contentLenght1 = String.format("Content-Lenght: %d\r\n", content1.length);
								output.write(contentLenght1.getBytes());
								output.write("Content-Type: application/xml\r\n".getBytes());
								output.write("\r\n".getBytes());
								output.write(content1);
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;
					case GET:
						if (request.getResourceParameters().containsKey("uuid")) {
							if (xsddao.uuidList().contains(request.getResourceParameters().get("uuid")) || remotes
									.getRemoteUuid("XSD").contains(request.getResourceParameters().get("uuid"))) {
								String webPage = "";
								if (xsddao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
									webPage = xsddao.getContent(request.getResourceParameters().get("uuid"));

								} else if (remotes.getRemoteUuid("XSD")
										.contains(request.getResourceParameters().get("uuid"))) {
									webPage = remotes.getRemoteContent(request.getResourceParameters().get("uuid"),
											"XSD");

								}
								byte[] content = webPage.getBytes();
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 200 OK\r\n".getBytes());
									String contentLenght = String.format("Content-Lenght: %d\r\n", content.length);
									output.write(contentLenght.getBytes());
									output.write("Content-Type: application/xml\r\n".getBytes());
									output.write("\r\n".getBytes());
									output.write(content);
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}

							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						} else {

							String pageContainer = "";
							if ((!(xsddao.uuidList().isEmpty())) || (!(remotes.getRemoteUuid("XSD").isEmpty()))) {
								ArrayList toPage = (ArrayList) xsddao.uuidList();
								for (String uuid : remotes.getRemoteUuid("XSD")) {
									if (!(toPage.contains(uuid))) {
										toPage.add(uuid);
									}
								}
								for (int i = 0; i < toPage.size(); i++) {
									pageContainer += "<a href=\"xsd?uuid=" + toPage.get(i) + "\">" + toPage.get(i)
											+ "</a>" + "</br>";

								}
								byte[] content = pageContainer.getBytes();

								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 200 OK\r\n".getBytes());
									String contentLenght = String.format("Content-Lenght: %d\r\n", content.length);
									output.write(contentLenght.getBytes());
									output.write("Content-Type: text/html\r\n".getBytes());
									output.write("\r\n".getBytes());
									output.write(content);
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}

						break;
					case DELETE:
						if (request.getResourceParameters().containsKey("uuid")) {
							if (xsddao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
								String newid = request.getResourceParameters().get("uuid");
								xsddao.delete(newid);
								output = socket.getOutputStream();
								output.write("HTTP/1.1 200 OK\r\n".getBytes());
								output.flush();

							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						} else {

							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						break;
					}

					break;
				case "xslt":

					XSLTDAO xsltdao = new XSLTDAO(parametrosConexion);
					switch (request.getMethod()) {
					case POST:
						if (!(request.getResourceParameters().get("xslt") == null)
								&& !(request.getResourceParameters().get("xsd") == null)) {
							String newPage = request.getResourceParameters().get("xslt");
							System.out.println(newPage);
							String uuidXSD = request.getResourceParameters().get("xsd");
							System.out.println(uuidXSD);
							try {

								String uuid = xsltdao.create(newPage, uuidXSD);

								byte[] content1 = ("<a href=\"xslt?uuid=" + uuid + "\">" + uuid + "</a>").getBytes();
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 200 OK\r\n".getBytes());
									String contentLenght1 = String.format("Content-Lenght: %d\r\n", content1.length);
									output.write(contentLenght1.getBytes());
									output.write("Content-Type: application/xml\r\n".getBytes());
									output.write("\r\n".getBytes());
									output.write(content1);
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
								output.flush();

							}
						} else {
							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;
					case GET:
						if (request.getResourceParameters().containsKey("uuid")) {
							if (xsltdao.uuidList().contains(request.getResourceParameters().get("uuid")) || remotes
									.getRemoteUuid("XSLT").contains(request.getResourceParameters().get("uuid"))) {
								String webPage = "";
								if (xsltdao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
									webPage = xsltdao.getContent(request.getResourceParameters().get("uuid"));

								} else if (remotes.getRemoteUuid("XSLT")
										.contains(request.getResourceParameters().get("uuid"))) {
									webPage = remotes.getRemoteContent(request.getResourceParameters().get("uuid"),
											"XSLT");

								}
								byte[] content = webPage.getBytes();
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 200 OK\r\n".getBytes());
									String contentLenght = String.format("Content-Lenght: %d\r\n", content.length);
									output.write(contentLenght.getBytes());
									output.write("Content-Type: application/xml\r\n".getBytes());
									output.write("\r\n".getBytes());
									output.write(content);
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}

							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {

							String pageContainer = "";
							if ((!(xsltdao.uuidList().isEmpty())) || (!(remotes.getRemoteUuid("XSLT").isEmpty()))) {
								ArrayList toPage = (ArrayList) xsltdao.uuidList();
								for (String uuid : remotes.getRemoteUuid("XSLT")) {
									if (!(toPage.contains(uuid))) {
										toPage.add(uuid);
									}
								}
								for (int i = 0; i < toPage.size(); i++) {
									pageContainer += "<a href=\"xslt?uuid=" + toPage.get(i) + "\">" + toPage.get(i)
											+ "</a>" + "</br>";

								}
								byte[] content = pageContainer.getBytes();

								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 200 OK\r\n".getBytes());
									String contentLenght = String.format("Content-Lenght: %d\r\n", content.length);
									output.write(contentLenght.getBytes());
									output.write("Content-Type: text/html\r\n".getBytes());
									output.write("\r\n".getBytes());
									output.write(content);
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}

						break;
					case DELETE:
						if (request.getResourceParameters().containsKey("uuid")) {
							if (xsltdao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
								String newid = request.getResourceParameters().get("uuid");
								xsltdao.delete(newid);
								output = socket.getOutputStream();
								output.write("HTTP/1.1 200 OK\r\n".getBytes());
								output.flush();

							} else {
								try {
									output = socket.getOutputStream();
									output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
									output.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						} else {

							try {
								output = socket.getOutputStream();
								output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
								output.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						break;
					}

					break;
				}

			} else {
				if (request.getResourceChain().equals("/")) {
					String webPage2 = "<html><body><h1>Hybrid Server</h1><div>Atenea Fernandez Outeda (53861461S)</br>Ivan Gonzalez Gonzalez (53973938E)</div></body></html>";
					byte[] content2 = webPage2.getBytes();

					OutputStream output1 = socket.getOutputStream();

					output1.write("HTTP/1.1 200 OK\r\n".getBytes());
					String contentLenght2 = String.format("Content-Lenght: %d\r\n", content2.length);
					output1.write(contentLenght2.getBytes());
					output1.write("Content-Type: text/html\r\n".getBytes());
					output1.write("\r\n".getBytes());
					output1.write(content2);
					output1.flush();
				} else {
					try {
						output = socket.getOutputStream();
						output.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
						output.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			socket.close();

		} catch (IOException | HTTPParseException | SQLException | HTMLException e1) {
			try {
				OutputStream output = socket.getOutputStream();
				output = socket.getOutputStream();
				output.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes());
				output.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			e1.printStackTrace();
		}

	}

	public static void transform(Source xmlSource, Source xsltSource, Result result) throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(xsltSource);
		transformer.transform(xmlSource, result);
	}
}
