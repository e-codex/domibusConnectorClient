package eu.domibus.connector.client.controller.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "CONNECTOR_CLIENT_MESSAGE")
public class PDomibusConnectorClientMessage {

	@Id
    @Column(name="ID")
	@SequenceGenerator(name = "clientMessageSeqGen", sequenceName = "clientMessageSeq", initialValue = 5, allocationSize = 100)
    @GeneratedValue(generator = "clientMessageSeqGen")
	private long id;
	
	@Column(name = "EBMS_MESSAGE_ID", length = 255)
    private String ebmsMessageId;
	
	@Column(name = "BACKEND_MESSAGE_ID", unique = true, length = 255)
    private String backendMessageId;
	
	@Column(name = "CONVERSATION_ID", length = 255)
    private String conversationId;
	
	@Column(name = "STORAGE_STATUS", length = 255)
    private String storageStatus;
	
	@Column(name = "STORAGE_INFO", length = 255)
    private String storageInfo;
	
	@Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	public PDomibusConnectorClientMessage() {
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEbmsMessageId() {
		return ebmsMessageId;
	}

	public void setEbmsMessageId(String ebmsMessageId) {
		this.ebmsMessageId = ebmsMessageId;
	}

}
