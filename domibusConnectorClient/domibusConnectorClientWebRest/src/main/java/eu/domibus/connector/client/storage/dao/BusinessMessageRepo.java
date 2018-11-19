package eu.domibus.connector.client.storage.dao;

import eu.domibus.connector.client.storage.entity.BusinessMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessMessageRepo extends JpaRepository<BusinessMessage, Long> {

    @Query("select m from BusinessMessage m where m.applicationMessageId = ?1")
    Optional<BusinessMessage> findByApplicationMessageId(String nationalMessageId);

}
