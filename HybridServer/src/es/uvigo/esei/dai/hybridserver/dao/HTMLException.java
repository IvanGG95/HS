package es.uvigo.esei.dai.hybridserver.dao;


public class HTMLException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private final String uuid;
	
	public HTMLException(String uuid) {
		this.uuid = uuid;
	}
	
	public HTMLException(String message, String uuid) {
		super(message);
		this.uuid = uuid;
	}
}
