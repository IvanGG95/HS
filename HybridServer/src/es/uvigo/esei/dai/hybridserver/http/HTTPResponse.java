package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HTTPResponse {
	HTTPResponseStatus status;
	String version;
	String content;
	Map<String, String> parameters;
	List<String> paraList;

	public HTTPResponse() {
		status = null;
		version = "";
		content = "";
		parameters = new LinkedHashMap<>();
		paraList = new LinkedList<>();
	}

	public HTTPResponseStatus getStatus() {
		return status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String putParameter(String name, String value) {
		parameters.put(name, value);
		String list = name + ": " + parameters.get(name);
		paraList.add(list);
		return list;
	}

	public boolean containsParameter(String name) {
		return parameters.containsKey(name);
	}

	public String removeParameter(String name) {
		return parameters.remove(name);
	}

	public void clearParameters() {
		parameters.clear();
	}

	public List<String> listParameters() {
		return paraList;
	}

	public void print(Writer writer) throws IOException {
		String definitive;
		status.getStatus();
		definitive = getVersion() + " " + status.getCode() + " " + status.getStatus();
		if (content != "") {
			definitive += "\r\nContent-Length: " + content.length();
		}
		if (listParameters() != null) {
			for (int i = 0; i < listParameters().size(); i++) {
				definitive += "\r\n";
				definitive += (listParameters().get(i));
			}
		}

		writer.write(definitive);
		writer.write("\r\n\r\n");
		writer.write(content);

	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
