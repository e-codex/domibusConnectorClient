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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.spring.annotation.UIScope;

@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@UIScope
@org.springframework.stereotype.Component
public class SendMessage extends VerticalLayout {

	Div areaCreateNewMessage = null;
	Div areaReplyToMessage = null;
	
	Tab createNewMessageTab = new Tab("Create New Message");
	Tab replyToMessageTab = new Tab("Reply To Message");
	
	Tabs sendMessageMenu = new Tabs();
	
	public SendMessage(@Autowired CreateNewMessage createNewMessage, @Autowired ReplyToMessage replyToMessage) {
		
		areaCreateNewMessage = new Div();
		areaCreateNewMessage.add(createNewMessage);
		areaCreateNewMessage.setVisible(false);
		
		areaReplyToMessage = new Div();
		areaReplyToMessage.add(replyToMessage);
		areaReplyToMessage.setVisible(false);
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(createNewMessageTab, areaCreateNewMessage);
		tabsToPages.put(replyToMessageTab, areaReplyToMessage);
		
		
		sendMessageMenu.add(createNewMessageTab, replyToMessageTab);
		
		
		Div pages = new Div(areaCreateNewMessage, areaReplyToMessage);
		
		Set<Component> pagesShown = Stream.of(areaCreateNewMessage)
		        .collect(Collectors.toSet());
		
	
		sendMessageMenu.addSelectedChangeListener(event -> {
		    pagesShown.forEach(page -> page.setVisible(false));
		    pagesShown.clear();
		    Component selectedPage = tabsToPages.get(sendMessageMenu.getSelectedTab());
		    selectedPage.setVisible(true);
		    pagesShown.add(selectedPage);
		});

		add(sendMessageMenu,pages);
	}

}
