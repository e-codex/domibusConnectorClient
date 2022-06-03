package eu.domibus.connector.client.rest.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class DomibusConnectorClientMessage {

	private Long id;
	
    private String ebmsMessageId;
	
    private String backendMessageId;
	
    private String conversationId;
    
    private String originalSender;
    
    private String finalRecipient;
	
    private String fromPartyId;
	
    private String fromPartyType;
	
    private String fromPartyRole;
	
    private String toPartyId;
	
    private String toPartyType;
	
    private String toPartyRole;
    
    private String service;

    private String serviceType;

    private String action;
	
    private String storageStatus;
	
    private String storageInfo;
	
    private String lastConfirmationReceived;
    
    private String messageStatus;
	
    private Date created;
	
    private Set<DomibusConnectorClientConfirmation> evidences = new HashSet<>();
    
    private DomibusConnectorClientMessageFileList files = new DomibusConnectorClientMessageFileList();

}
