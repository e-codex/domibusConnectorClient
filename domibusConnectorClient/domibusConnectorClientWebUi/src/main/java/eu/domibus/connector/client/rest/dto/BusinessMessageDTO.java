package eu.domibus.connector.client.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class BusinessMessageDTO {

    private MessageDetailsDTO messageDetailsDTO;

    //business xml
    private byte[] businessXml;

    //business document
    private AttachmentDTO businessAttachment;

    //extra attachments
    private List<AttachmentDTO> attachments = new ArrayList<>();

    //confirmations
    private List<ConfirmationDTO> confirmationDTOS = new ArrayList<>();

    public List<ConfirmationDTO> getConfirmationDTOS() {
        return confirmationDTOS;
    }

    public void setConfirmationDTOS(List<ConfirmationDTO> confirmationDTOS) {
        this.confirmationDTOS = confirmationDTOS;
    }

    public byte[] getBusinessXml() {
        return businessXml;
    }

    public void setBusinessXml(byte[] businessXml) {
        this.businessXml = businessXml;
    }

    public AttachmentDTO getBusinessAttachment() {
        return businessAttachment;
    }

    public void setBusinessAttachment(AttachmentDTO businessAttachment) {
        this.businessAttachment = businessAttachment;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public MessageDetailsDTO getMessageDetailsDTO() {
        return messageDetailsDTO;
    }

    public void setMessageDetailsDTO(MessageDetailsDTO messageDetailsDTO) {
        this.messageDetailsDTO = messageDetailsDTO;
    }
}
