package eu.domibus.connector.client.rest.restobject;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

@ApiModel
public class BusinessMessageRO {

    private MessageDetailsRO messageDetailsDTO;

    private String applicationMessageId;

    //business xml
    private String businessXml;

    //business document
    private AttachmentRO businessAttachment;

    //extra attachments
    private List<AttachmentRO> attachments = new ArrayList<>();

    //confirmations
    private List<ConfirmationRO> confirmationROS = new ArrayList<>();
    private boolean draft;

    public List<ConfirmationRO> getConfirmationROS() {
        return confirmationROS;
    }

    public void setConfirmationROS(List<ConfirmationRO> confirmationROS) {
        this.confirmationROS = confirmationROS;
    }

    public String getBusinessXml() {
        return businessXml;
    }

    public void setBusinessXml(String businessXml) {
        this.businessXml = businessXml;
    }

    public AttachmentRO getBusinessAttachment() {
        return businessAttachment;
    }

    public void setBusinessAttachment(AttachmentRO businessAttachment) {
        this.businessAttachment = businessAttachment;
    }

    public List<AttachmentRO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentRO> attachments) {
        this.attachments = attachments;
    }

    public MessageDetailsRO getMessageDetailsDTO() {
        return messageDetailsDTO;
    }

    public void setMessageDetailsDTO(MessageDetailsRO messageDetailsDTO) {
        this.messageDetailsDTO = messageDetailsDTO;
    }

    public String getApplicationMessageId() {
        return applicationMessageId;
    }

    public void setApplicationMessageId(String applicationMessageId) {
        this.applicationMessageId = applicationMessageId;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean getDraft() {
        return draft;
    }
}
