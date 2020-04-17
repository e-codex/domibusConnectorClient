package eu.domibus.connector.client.ui.view.sendmessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.spring.annotation.UIScope;

@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@UIScope
@org.springframework.stereotype.Component
public class SendMessages extends VerticalLayout {

	Div areaSendMessage = null;
	
	Tab sendMessageTab = new Tab("Send Message");
	
	Tabs sendMessagesMenu = new Tabs();
	
	private SendMessage sendMessage;
	
	public SendMessages(@Autowired SendMessage sendMessage) {
		this.sendMessage = sendMessage;
		
		
		areaSendMessage = new Div();
		areaSendMessage.add(sendMessage);
		areaSendMessage.setVisible(false);
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(sendMessageTab, areaSendMessage);
		
		
		sendMessagesMenu.add(sendMessageTab);
		
		
		Div pages = new Div(areaSendMessage);
		
		Set<Component> pagesShown = Stream.of(areaSendMessage)
		        .collect(Collectors.toSet());
		
	
		sendMessagesMenu.addSelectedChangeListener(event -> {
		    pagesShown.forEach(page -> page.setVisible(false));
		    pagesShown.clear();
		    Component selectedPage = tabsToPages.get(sendMessagesMenu.getSelectedTab());
		    selectedPage.setVisible(true);
		    pagesShown.add(selectedPage);
		});

		add(sendMessagesMenu,pages);
	}
	
	public void showSendMessage(Long connectorMessageId) {
		sendMessage.loadPreparedMessage(connectorMessageId);
		sendMessage.setAlignItems(Alignment.START);
		sendMessagesMenu.setSelectedTab(sendMessageTab);
	}

}
