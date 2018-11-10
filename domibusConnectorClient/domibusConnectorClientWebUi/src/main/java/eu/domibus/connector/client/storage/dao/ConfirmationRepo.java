package eu.domibus.connector.client.storage.dao;

import eu.domibus.connector.client.storage.entity.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationRepo extends JpaRepository<Confirmation, Long> {
}
