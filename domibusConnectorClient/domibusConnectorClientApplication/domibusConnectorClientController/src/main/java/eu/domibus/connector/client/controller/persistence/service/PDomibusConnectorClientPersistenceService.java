package eu.domibus.connector.client.controller.persistence.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.controller.persistence.dao.PDomibusConnectorClientConfirmationDao;
import eu.domibus.connector.client.controller.persistence.dao.PDomibusConnectorClientMessageDao;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;

@Component
public class PDomibusConnectorClientPersistenceService {

	@Autowired
	private PDomibusConnectorClientMessageDao messageDao;
	
	@Autowired
	private PDomibusConnectorClientConfirmationDao confirmationDao;

	public PDomibusConnectorClientPersistenceService() {
		// TODO Auto-generated constructor stub
	}

	public PDomibusConnectorClientMessage persistNewMessage(DomibusConnectorMessageType message) {
		if(message!=null) {
			PDomibusConnectorClientMessage newMessage = new PDomibusConnectorClientMessage();

			DomibusConnectorMessageDetailsType messageDetails = message.getMessageDetails();
			if(messageDetails!=null) {
				newMessage.setBackendMessageId(messageDetails.getBackendMessageId());
				newMessage.setConversationId(messageDetails.getConversationId());
				newMessage.setEbmsMessageId(messageDetails.getEbmsMessageId());
				newMessage.setFinalRecipient(messageDetails.getFinalRecipient());
				newMessage.setOriginalSender(messageDetails.getOriginalSender());
				if(messageDetails.getAction()!=null)
					newMessage.setAction(messageDetails.getAction().getAction());
				if(messageDetails.getService()!=null)
					newMessage.setService(messageDetails.getService().getService());

				DomibusConnectorPartyType fromParty = messageDetails.getFromParty();
				if(fromParty!=null) {
					newMessage.setFromPartyId(fromParty.getPartyId());
					newMessage.setFromPartyRole(fromParty.getRole());
					newMessage.setFromPartyType(fromParty.getPartyIdType());
				}

				DomibusConnectorPartyType toParty = messageDetails.getToParty();
				if(toParty!=null) {
					newMessage.setToPartyId(toParty.getPartyId());
					newMessage.setToPartyRole(toParty.getRole());
					newMessage.setToPartyType(toParty.getPartyIdType());
				}
			}
			newMessage.setUpdated(new Date());
			newMessage.setCreated(new Date());
			
			newMessage = messageDao.save(newMessage);

			return newMessage;
		}
		return null;
	}
	
	public PDomibusConnectorClientMessage persistAllConfirmaitonsForMessage(PDomibusConnectorClientMessage clientMessage, DomibusConnectorMessageType message) {
		List<DomibusConnectorMessageConfirmationType> messageConfirmations = message.getMessageConfirmations();
		if(messageConfirmations!=null && !messageConfirmations.isEmpty()) {
			messageConfirmations.forEach(messageConfirmation -> {
				PDomibusConnectorClientConfirmation clientConfirmaiton = persistNewConfirmation(messageConfirmation, clientMessage);
				clientMessage.getConfirmations().add(clientConfirmaiton);
				clientMessage.setLastConfirmationReceived(clientConfirmaiton.getConfirmationType());
			});
			messageDao.save(clientMessage);
		}
		return clientMessage;
	}
	
	public PDomibusConnectorClientConfirmation persistNewConfirmation(DomibusConnectorMessageConfirmationType confirmation, PDomibusConnectorClientMessage clientMessage) {
		PDomibusConnectorClientConfirmation clientConfirmation = new PDomibusConnectorClientConfirmation();
		clientConfirmation.setMessage(clientMessage);
		clientConfirmation.setConfirmationType(confirmation.getConfirmationType().name());
		clientConfirmation.setReceived(new Date());

		clientConfirmation = confirmationDao.save(clientConfirmation);
		
		return clientConfirmation;
	}
	
	public PDomibusConnectorClientMessage mergeClientMessage(PDomibusConnectorClientMessage clientMessage) {
		clientMessage.setUpdated(new Date());
		
		clientMessage.getConfirmations().forEach(confirmation -> {
			confirmationDao.save(confirmation);
		});
		clientMessage = messageDao.save(clientMessage);
		
		return clientMessage;
	}
	
	
	public PDomibusConnectorClientMessage findOriginalClientMessage(DomibusConnectorMessageType message) {
		DomibusConnectorMessageDetailsType messageDetails = message.getMessageDetails();
		if(messageDetails!=null) {
			
			if(messageDetails.getRefToMessageId()!=null) {
				List<PDomibusConnectorClientMessage> clientMessages = messageDao.findByEbmsMessageId(messageDetails.getRefToMessageId());
				if(clientMessages!=null && clientMessages.size()==1) {
					return clientMessages.get(0);
				}
			}
		}
		
		return null;
	}
	
	public List<PDomibusConnectorClientMessage> findUnconfirmedMessages(){
		List<PDomibusConnectorClientMessage> unconfirmedMessages = new ArrayList<PDomibusConnectorClientMessage>();
		messageDao.findAll().forEach(message -> {
			unconfirmedMessages.add(message);
			if(message.getConfirmations()!= null && !message.getConfirmations().isEmpty()) {
				message.getConfirmations().forEach(confirmation -> {
					if(confirmation.getConfirmationType().equals(DomibusConnectorConfirmationType.DELIVERY.name()) ||
							confirmation.getConfirmationType().equals(DomibusConnectorConfirmationType.NON_DELIVERY.name())) {
						unconfirmedMessages.remove(message);
					}
				});
			}	
		});
		
		return unconfirmedMessages;
	}
	

}
