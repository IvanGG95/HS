package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {
	
	private HTTPRequestMethod method;
	private String resourceChain;
	private String resourceName;
	private String httpVersion;
	private Map<String, String> queryParams;
	private Map<String, String> headerParams;
	private int contentLength;
	private String content;
	
	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		boolean existsFirstLine = false;
		boolean existsLength = false;
		
		BufferedReader br = new BufferedReader(reader);
		queryParams = new LinkedHashMap<>();
		headerParams = new LinkedHashMap<>();
		String line;
		try{
		while (!((line = br.readLine()).isEmpty())) {
			if (line.contains("GET") || line.contains("POST")
					|| line.contains("PUT") || line.contains("DELETE")) {
				
				existsFirstLine = true;
				
				//Set method from reader
				String[] lineWithoutSpace = line.split(" ");
				method = HTTPRequestMethod.valueOf(lineWithoutSpace[0]);
				
				//Set resourceChain from reader
				resourceChain = lineWithoutSpace[1];
				if (!(resourceChain.contains("/"))) {
					throw new HTTPParseException("");
				}
				
				String[] lineWithoutQuestion = lineWithoutSpace[1].split("\\?");
				//Set resourceName from reader
				resourceName = lineWithoutQuestion[0].replaceFirst("\\/", "");
				
				//Set httpVersion from reader
				try {
				
				httpVersion = lineWithoutSpace[2].replace("\r\n", "");
				}catch(ArrayIndexOutOfBoundsException e) {
					throw new HTTPParseException(e);
				}
				if (line.contains("?")) {
					String[] lineWithoutAmpersand = lineWithoutQuestion[1].split("&");
					for (String param : lineWithoutAmpersand) {
						String[] paramSplit = param.split("=");
						queryParams.put(paramSplit[0], paramSplit[1]);
					}
				}
				
			} else if (line.contains("Content-Length: ")) {
				//Set contentLength from reader
				existsLength = true;
				String length = line.replace("Content-Length: ", "");
				contentLength = Integer.parseInt(length);
				headerParams.put("Content-Length", length);
				
			} else if (line.contains(": ")) {
				//Set headerParams from reader
				String[] headerParamsSplit = line.split(": ");
				headerParams.put(headerParamsSplit[0], headerParamsSplit[1]);
			}else {
				throw new HTTPParseException();
			}
			
		}
	
		}catch(java.lang.NullPointerException e) {
			throw new HTTPParseException(e);
		}
		
		if (!existsFirstLine) {
			throw new HTTPParseException();
		}
		
		//Set content from reader if exists
		
		if (existsLength) {
			
			char[] contentBytes = new char[contentLength];
			br.read(contentBytes);
			content = new String(contentBytes);
			
			String type = headerParams.get("Content-Type");
			if (type != null && type.startsWith("application/x-www-form-urlencoded")) {
			   content = URLDecoder.decode(content, "UTF-8");
			}
			
			String[] lineWithoutAmpersand = content.split("&");
			
			for (String param : lineWithoutAmpersand) {
				String[] paramSplit = param.split("=");
				System.out.println(paramSplit[0]+" "+paramSplit[1]);
				queryParams.put(paramSplit[0], paramSplit[1]);
			}
		}
		
	}

	public HTTPRequestMethod getMethod() {
		return method;
	}

	public String getResourceChain() {
		return resourceChain;
	}

	public String[] getResourcePath() {
		if(resourceName.isEmpty()) {
			return new String[] {};
		}else {
			return resourceName.split("/");
		}
		
	}

	public String getResourceName() {
		return resourceName;
	}
	
	public String getHttpVersion() {
		return httpVersion;
	}

	public Map<String, String> getResourceParameters() {
		return queryParams;
	}

	public Map<String, String> getHeaderParameters() {
		return headerParams;
	}

	public String getContent() {
		return content;
	}

	public int getContentLength() {
		return contentLength;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
