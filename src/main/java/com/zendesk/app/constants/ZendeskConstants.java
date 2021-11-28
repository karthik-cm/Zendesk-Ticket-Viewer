package com.zendesk.app.constants;

public class ZendeskConstants {
	
	public static final String REDIRECT_REQUEST = "redirect:";
	
	public static final String REQUEST_ZENDESK_TICKET_VIEWER = "/zendeskTicketViewer";
	
	
	// HOME PAGE
	public static final String REQUEST_HOME_PAGE = "/home";
	public static final String VIEW_ZENDESK_HOME_PAGE = "zendesk_home";
	
	public static final String REQUEST_REDIRECT_TO_HOME_PAGE = REDIRECT_REQUEST +REQUEST_ZENDESK_TICKET_VIEWER +REQUEST_HOME_PAGE;
	
	
	// LOGIN/LOGOUT
	public static final String REQUEST_LOGIN = "/login";
	public static final String REQUEST_LOGOUT = "/logout";
	
	
	// VIEW TICKETS
	public static final String REQUEST_VIEW_TICKETS = "/view_tickets";
	public static final String VIEW_ZENDESK_VIEW_TICKETS_PAGE = "zendesk_view_tickets";
	
	
	
	// ERROR
	public static final String REQUEST_ERROR = "/error";
	public static final String VIEW_ZENDESK_ERROR_PAGE = "/zendesk_error";
	
	
	
	
	// Generic Constants
	public static final String SUBDOMAIN = "subdomain";
	public static final String EMAILID = "emailid";
	public static final String PASSWORD = "password";
	
	public static final String PAGE = "page";
	
	public static final int TIMEOUT_SESSION_IN_SECONDS = 300;
}
