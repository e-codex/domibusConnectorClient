package eu.domibus.connector.client.ui.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import eu.domibus.connector.client.ui.view.messages.Messages;
import eu.domibus.connector.client.ui.view.sendmessage.SendMessage;

//@HtmlImport("styles/shared-styles.html")
@HtmlImport("styles/shared-styles.html")
@Route("")
@PageTitle("domibusConnectorClient")
public class DomibusConnectorClientUIMainView extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Map<Tab, Component> tabsToPages = new HashMap<>();
	Tabs TopMenu = new Tabs();

	public DomibusConnectorClientUIMainView(@Autowired DomibusConnectorClientUIHeader header, 
			@Autowired Messages messages, 
			@Autowired SendMessage sendMessage 
//    		@Autowired Configuration configuration, 
//    		@Autowired Users users,
//    		@Autowired Info info, 
//    		@Autowired ConnectorTests connectorTests
    		) {
//		this.userInfo = userInfo;
        
    	Div areaMessages = new Div();
		areaMessages.add(messages);
		areaMessages.setVisible(true);
		
		Div areaSendMessage = new Div();
		areaSendMessage.add(sendMessage);
		areaSendMessage.setVisible(false);
//		
//		Div areaConfiguration = new Div();
//		areaConfiguration.add(configuration);
//		areaConfiguration.setVisible(false);
//		
//		Div areaUsers = new Div();
//		areaUsers.add(users);
//		areaUsers.setVisible(false);
//		
//		Div areaTests = new Div();
//		areaTests.add(connectorTests);
//		areaTests.setVisible(false);
//		
//		Div areaInfo = new Div();
//		areaInfo.add(info);
//		areaInfo.setVisible(true);
		
		createTab(areaMessages, "Messages", new Icon(VaadinIcon.LIST), false);
		
		createTab(areaSendMessage, "SendMessage", new Icon(VaadinIcon.MAILBOX), false);
		
//		createTab(areaConfiguration, "Configuration", new Icon(VaadinIcon.COG_O), false);
//		
//		createTab(areaUsers, "Users", new Icon(VaadinIcon.USERS), false);
//		
//		createTab(areaTests, "Connector Tests", new Icon(VaadinIcon.CONNECT_O), false);
//		
//		createTab(areaInfo, "Info", new Icon(VaadinIcon.INFO_CIRCLE_O), true);
		
		
		Div pages = new Div(areaMessages, areaSendMessage
//				, areaPModes, areaConfiguration, areaUsers, areaTests, areaInfo
				);
		
		Set<Component> pagesShown = Stream.of(areaMessages, areaSendMessage
//				, areaPModes, areaConfiguration, areaUsers, areaTests, areaInfo
				)
		        .collect(Collectors.toSet());
		
		TopMenu.addSelectedChangeListener(event -> {
		    pagesShown.forEach(page -> page.setVisible(false));
		    pagesShown.clear();
		    Component selectedPage = tabsToPages.get(TopMenu.getSelectedTab());
		    selectedPage.setVisible(true);
		    pagesShown.add(selectedPage);
		});
		
		add(header, 
//				userInfo,
				TopMenu,pages);
	}
	
	private void createTab(Div tabArea, String tabLabel, Icon tabIcon, boolean selected) {
		Span tabText = new Span(tabLabel);
		tabText.getStyle().set("font-size", "20px");
		
		tabIcon.setSize("20px");
		
		HorizontalLayout tabLayout = new HorizontalLayout(tabIcon, tabText);
		tabLayout.setAlignItems(Alignment.CENTER);
		Tab tab = new Tab(tabLayout);
		tab.setSelected(selected);
		
		tabsToPages.put(tab, tabArea);
		TopMenu.add(tab);
		if(selected) {
			TopMenu.setSelectedTab(tab);
		}
		
	}

}
