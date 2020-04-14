package eu.domibus.connector.client.controller.persistence.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;

@Repository
public interface PDomibusConnectorClientMessageDao extends CrudRepository<PDomibusConnectorClientMessage, Long> {

	public List<PDomibusConnectorClientMessage> findByBackendMessageId(String backendId);
	
	public List<PDomibusConnectorClientMessage> findByEbmsMessageId(String ebmsMessageId);
	
	public List<PDomibusConnectorClientMessage> findByConversationId(String conversationId);
	
	@Query("SELECT m FROM PDomibusConnectorClientMessage m WHERE "
			+ "(m.created is not null AND m.created between ?1 and ?2)")
    public List<PDomibusConnectorClientMessage> findByPeriod(Date from, Date to);
	
	@Query("SELECT m FROM PDomibusConnectorClientMessage m WHERE "
			+ "m.confirmationTriggered is null")
    public List<PDomibusConnectorClientMessage> findUnconfirmed();
	
}
