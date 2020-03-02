package eu.domibus.connector.client.controller.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;

@Repository
public interface DomibusConnectorClientMessageDao extends CrudRepository<PDomibusConnectorClientMessage, Long> {

	
}
