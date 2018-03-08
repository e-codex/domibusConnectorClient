package eu.domibus.connector.client.filesystem;

public class DomibusStandaloneConnectorFileSystemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4383246883598401524L;

	public DomibusStandaloneConnectorFileSystemException() {
	}

	public DomibusStandaloneConnectorFileSystemException(String message) {
		super(message);
	}

	public DomibusStandaloneConnectorFileSystemException(Throwable cause) {
		super(cause);
	}

	public DomibusStandaloneConnectorFileSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public DomibusStandaloneConnectorFileSystemException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
