package eu.domibus.connector.client.filesystem.configuration;

import java.nio.file.Path;

public class DirectoryConfigurationProperties {

	private Path path;
	private boolean createIfNonExistent;
	
	public DirectoryConfigurationProperties() {
		// TODO Auto-generated constructor stub
	}


	public boolean isCreateIfNonExistent() {
		return createIfNonExistent;
	}

	public void setCreateIfNonExistent(boolean createIfNonExistent) {
		this.createIfNonExistent = createIfNonExistent;
	}


	public Path getPath() {
		return path;
	}


	public void setPath(Path path) {
		this.path = path;
	}


	@Override
	public String toString() {
		return "DirectoryConfigurationProperties [path=" + path + ", createIfNonExistent=" + createIfNonExistent + "]";
	}

}
