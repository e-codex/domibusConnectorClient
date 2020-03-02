package eu.domibus.connector.client.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.domibus.connector.client.controller.persistence.dao.DomibusConnectorClientMessageDao;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;

@RestController
public class DomibusConnectorClientRestAPI {
	
	@Autowired
	private DomibusConnectorClientMessageDao messageDao;

	public DomibusConnectorClientRestAPI() {
		// TODO Auto-generated constructor stub
	}
	
	@GetMapping("/getAllMessages")
	public Iterable<PDomibusConnectorClientMessage> getAllMessages(){
		return messageDao.findAll();
	}

}
