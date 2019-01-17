package eu.domibus.connector.client.mapping;

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
     * Method to map international business XML to national format. Must be
     * overridden when ContentMapper is used by configuration. The national xml
     * content will be written into the messageContent object.
     * 
     * @param message - a {@link DomibusConnectorMessageType} object containing the eCodex xml Content.
     * @throws DomibusConnectorContentMapperException
     */
    public void mapInternationalToNational(DomibusConnectorMessageType message) throws DomibusConnectorContentMapperException;

    /**
     * Method to map national XML to international eCodex format. Must be
     * overridden when ContentMapper is used by configuration. The eCodex xml
     * content will be written into the messageContent object.
     * 
     * @param message - a {@link DomibusConnectorMessageType} object containing the national xml Content.
     * @throws DomibusConnectorContentMapperException
     */
    public void mapNationalToInternational(DomibusConnectorMessageType message) throws DomibusConnectorContentMapperException;

}
