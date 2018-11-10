package eu.domibus.connector.client.storage.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSPORT_LOG")
public class Transport {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "TRANSPORT_ID")
    private String transportId;

    @OneToOne(optional = false)
    private MessageDetails messageDetails;

    @Column(name = "TRANSPORT_DIRECTION")
    @Enumerated(EnumType.STRING)
    private TransportDirection transportDirection;

    //if incoming whent it has been received
    @Column(name = "RECEIVED")
    private LocalDateTime received;

    //if outgoing when it has been successfully sent
    @Column(name = "SENT")
    private LocalDateTime sent;

    //when has the transport been initiated
    @Column(name = "CREATED")
    private LocalDateTime created;

    @Version
    @Column(name = "LAST_UPDATE")
    private LocalDateTime updated;

    @PrePersist
    public void prePersist() {
        if (created == null) {
            created = LocalDateTime.now();
        }
    }

    public String getTransportId() {
        return transportId;
    }

    public void setTransportId(String transportId) {
        this.transportId = transportId;
    }

    public MessageDetails getMessageDetails() {
        return messageDetails;
    }

    public void setMessageDetails(MessageDetails messageDetails) {
        this.messageDetails = messageDetails;
    }

    public TransportDirection getTransportDirection() {
        return transportDirection;
    }

    public void setTransportDirection(TransportDirection transportDirection) {
        this.transportDirection = transportDirection;
    }

    public LocalDateTime getReceived() {
        return received;
    }

    public void setReceived(LocalDateTime received) {
        this.received = received;
    }

    public LocalDateTime getSent() {
        return sent;
    }

    public void setSent(LocalDateTime sent) {
        this.sent = sent;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public static enum TransportState {
        PENDING, //transport started, waiting for response
        SUCCESS, //transport sucessfull
        FAILURE; //transport failed
    }

    public static enum TransportDirection {
        INCOMING, OUTGOING;
    }

}
