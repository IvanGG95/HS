package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

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

	public ServiceThread(Socket socket, List<String> parametrosConexion) {
		this.socket = socket;
		this.parametrosConexion = parametrosConexion;

	}

	@Override
	public void run() {
		HTTPRequest request;
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
							if (htmlDAO.uuidList().contains(request.getResourceParameters().get("uuid"))) {
								String webPage = htmlDAO.getContent(request.getResourceParameters().get("uuid"));
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
							if (!(htmlDAO.uuidList().isEmpty())) {
								for (int i = 0; i < htmlDAO.uuidList().size(); i++) {
									pageContainer += "<a href=\"html?uuid=" + htmlDAO.uuidList().get(i) + "\">"
											+ htmlDAO.uuidList().get(i) + "</a>" + "</br>";

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
						if (request.getResourceParameters().containsKey("uuid")) {
							if (xmldao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
								String webPage = xmldao.getContent(request.getResourceParameters().get("uuid"));
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
							if (!(xmldao.uuidList().isEmpty())) {
								for (int i = 0; i < xmldao.uuidList().size(); i++) {
									pageContainer += "<a href=\"xml?uuid=" + xmldao.uuidList().get(i) + "\">"
											+ xmldao.uuidList().get(i) + "</a>" + "</br>";

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
							if (xsddao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
								String webPage = xsddao.getContent(request.getResourceParameters().get("uuid"));
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
							if (!(xsddao.uuidList().isEmpty())) {
								for (int i = 0; i < xsddao.uuidList().size(); i++) {
									pageContainer += "<a href=\"xsd?uuid=" + xsddao.uuidList().get(i) + "\">"
											+ xsddao.uuidList().get(i) + "</a>" + "</br>";

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
							if (xsltdao.uuidList().contains(request.getResourceParameters().get("uuid"))) {
								String webPage = xsltdao.getContent(request.getResourceParameters().get("uuid"));
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
							if (!(xsltdao.uuidList().isEmpty())) {
								for (int i = 0; i < xsltdao.uuidList().size(); i++) {
									pageContainer += "<a href=\"xslt?uuid=" + xsltdao.uuidList().get(i) + "\">"
											+ xsltdao.uuidList().get(i) + "</a>" + "</br>";

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
}
