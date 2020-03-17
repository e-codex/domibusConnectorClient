package eu.domibus.connector.client.ui.view.messages;

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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.spring.annotation.UIScope;


@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@UIScope
@org.springframework.stereotype.Component
public class Messages extends VerticalLayout {

	Div areaMessagesList = null;
	Div areaSearch = null;
	Div areaMessageDetails = null;
	
	Tab messagesListTab = new Tab("All Messages");
	Tab searchTab = new Tab("Message Search");
	Tab messageDetailsTab = new Tab("Message Details");
	
	Tabs messagesMenu = new Tabs();
	
	MessageDetails messageDetails;
	
	public Messages(@Autowired MessagesList messagesList, @Autowired Search search, 
			@Autowired MessageDetails messageDetails)  {
		
		this.messageDetails = messageDetails;
		messagesList.setMessagesView(this);
		search.setMessagesView(this);
		
		areaMessagesList = new Div();
		areaMessagesList.add(messagesList);
		areaMessagesList.setVisible(false);
		
		areaSearch = new Div();
		areaSearch.add(search);
		areaSearch.setVisible(true);
		
		areaMessageDetails = new Div();
		areaMessageDetails.add(messageDetails);
		areaMessageDetails.setVisible(false);
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(searchTab, areaSearch);
		tabsToPages.put(messagesListTab, areaMessagesList);
		tabsToPages.put(messageDetailsTab, areaMessageDetails);
		
		
		messagesMenu.add(searchTab, messagesListTab, messageDetailsTab);
		
		
		Div pages = new Div(areaSearch, areaMessagesList, areaMessageDetails);
		
		Set<Component> pagesShown = Stream.of(areaSearch)
		        .collect(Collectors.toSet());
		
	
		messagesMenu.addSelectedChangeListener(event -> {
		    pagesShown.forEach(page -> page.setVisible(false));
		    pagesShown.clear();
		    Component selectedPage = tabsToPages.get(messagesMenu.getSelectedTab());
		    selectedPage.setVisible(true);
		    pagesShown.add(selectedPage);
		});

		add(messagesMenu,pages);
	}
	
	public void showMessageDetails(Long connectorMessageId) {
		messageDetails.loadMessageDetails(connectorMessageId);
		messageDetails.setAlignItems(Alignment.START);
		messagesMenu.setSelectedTab(messageDetailsTab);
	}

}
