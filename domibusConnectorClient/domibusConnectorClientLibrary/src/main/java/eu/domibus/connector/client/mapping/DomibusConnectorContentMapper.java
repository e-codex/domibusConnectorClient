package eu.domibus.connector.client.mapping;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * Interface with methods to map national format XML to eCodex format and vice
 * versa. Inheritance must be configured.
 * 
 * @author riederb
 * 
 */
public interface DomibusConnectorContentMapper {

    /**
     * 
     * Method to map international eCodex XML to national format. Must be
     * overridden when ContentMapper is used by configuration. The national xml
     * content will be written into the messageContent object.
     * 
     * @param messageContent
     *            - a {@link MessageContent} object containing the eCodex xml
     *            Content.
     * @throws DomibusConnectorContentMapperException
     * @throws ImplementationMissingException
     */
    public void mapInternationalToNational(DomibusConnectorMessageType message) throws DomibusConnectorContentMapperException,
            ImplementationMissingException;

    /**
     * Method to map national XML to international eCodex format. Must be
     * overridden when ContentMapper is used by configuration. The eCodex xml
     * content will be written into the messageContent object.
     * 
     * @param messageContent
     *            - a {@link MessageContent} object containing the national xml
     *            Content.
     * @throws DomibusConnectorContentMapperException
     * @throws ImplementationMissingException
     */
    public void mapNationalToInternational(DomibusConnectorMessageType message) throws DomibusConnectorContentMapperException,
            ImplementationMissingException;

}