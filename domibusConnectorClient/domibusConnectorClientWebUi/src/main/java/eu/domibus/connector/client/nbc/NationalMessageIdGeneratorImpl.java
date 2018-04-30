package eu.domibus.connector.client.nbc;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NationalMessageIdGeneratorImpl implements NationalMessageIdGenerator {

    @Override
    public String generateNationalId() {
        return "WebUIApp" + UUID.randomUUID().toString();
    }

}
