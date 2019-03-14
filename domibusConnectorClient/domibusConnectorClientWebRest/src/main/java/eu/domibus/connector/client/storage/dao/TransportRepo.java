package eu.domibus.connector.client.storage.dao;

import eu.domibus.connector.client.storage.entity.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRepo extends JpaRepository<Transport, Long> {
}
