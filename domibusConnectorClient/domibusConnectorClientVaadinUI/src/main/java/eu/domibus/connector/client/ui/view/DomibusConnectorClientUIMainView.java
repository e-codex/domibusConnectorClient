package eu.domibus.connector.client.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.ui.view.messages.Messages;
import eu.domibus.connector.client.ui.view.sendmessage.SendMessages;

@UIScope
@org.springframework.stereotype.Component
public class DomibusConnectorClientUIMainView extends AppLayout implements RouterLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Tabs mainMenu = new Tabs();
	
	Tab messagesTab;
	Tab sendMessagesTab;

	public DomibusConnectorClientUIMainView(
    		) {
		
		setPrimarySection(Section.NAVBAR);

        VerticalLayout topBar = new VerticalLayout();
        topBar.add(new DomibusConnectorClientUIHeader());
        addToNavbar(topBar);
        
        messagesTab = new Tab(createRouterLink("Messages", new Icon(VaadinIcon.ENVELOPES), Messages.class));
        messagesTab.setSelected(false);
        
        sendMessagesTab =new Tab(createRouterLink("Send Messages", new Icon(VaadinIcon.MAILBOX), SendMessages.class));
        sendMessagesTab.setSelected(false);
        
        mainMenu.add(messagesTab, sendMessagesTab);
        
        mainMenu.setOrientation(Tabs.Orientation.HORIZONTAL);
		
        mainMenu.addSelectedChangeListener(e -> {
			e.getPreviousTab().setSelected(false);
			e.getSelectedTab().setSelected(true);
		});
       
        topBar.add(mainMenu);
	}
	
	private RouterLink createRouterLink(String tabLabel, Icon tabIcon, Class<? extends Component> component) {
		Span tabText = new Span(tabLabel);
		tabText.getStyle().set("font-size", "20px");
		
		tabIcon.setSize("20px");
		
		HorizontalLayout tabLayout = new HorizontalLayout(tabIcon, tabText);
		tabLayout.setAlignItems(Alignment.CENTER);
		
		RouterLink routerLink = new RouterLink(null, component);
		routerLink.add(tabLayout);
		
		return routerLink;
	}

}
