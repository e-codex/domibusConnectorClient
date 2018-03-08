package eu.domibus.connector.client.gui;

import java.awt.EventQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("swing-gui")
public class DomibusConnectorCommandLineRunner implements CommandLineRunner {

    @Autowired
    ApplicationContext ctx;
    


    @Override
    public void run(String... args) throws Exception {
        EventQueue.invokeLater(() -> {
            DomibusConnectorUI ex = ctx.getBean(DomibusConnectorUI.class);
            ex.setVisible(true);
        });
    }

}
