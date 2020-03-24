package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.util.List;

import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;

public interface DomibusConnectorClientFSStorage extends DomibusConnectorClientStorage {

	public void setIncomingMessagesDir(File incomingMessagesDir);
	
	public void setOutgoingMessagesDir(File outgoingMessagesDir);


}
