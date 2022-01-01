package com.zendesk.app.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zendesk.app.constants.ZendeskConstants;
import com.zendesk.app.service.ZendeskApiService;

@Controller
@RequestMapping(ZendeskConstants.REQUEST_ZENDESK_TICKET_VIEWER)
public class ZendeskViewTicketController {
	
	private static Logger logger = LoggerFactory.getLogger(ZendeskViewTicketController.class);
	
	
	@Autowired
	ZendeskApiService zendeskApiService;
	
	
	@RequestMapping(value = ZendeskConstants.REQUEST_VIEW_TICKETS, method = RequestMethod.GET)
	public ModelAndView view_tickets(HttpServletRequest request, HttpSession session) {
		logger.info("\n\nInside view_tickets() :::::: ZendeskViewTicketController : View tickets functionality");
		
		ModelAndView mv = new ModelAndView();
		boolean errorFlag = false;
		
		try {
			if(session != null) {
				JSONObject params = (JSONObject) session.getAttribute("params");
				
				if(params != null && params.length() > 0) {
					String subdomain = params.getString(ZendeskConstants.SUBDOMAIN);
					String emailid = params.getString(ZendeskConstants.EMAILID);
					String password = params.getString(ZendeskConstants.PASSWORD);
					
					
					Map<String, String[]> requestParameterMap = request.getParameterMap();
					String page = requestParameterMap.containsKey(ZendeskConstants.PAGE) ? requestParameterMap.get(ZendeskConstants.PAGE)[0] : "1"; 
					
					// Call View Ticket Zendesk API service to List tickets
					JSONObject viewTicketsResponse = zendeskApiService.call_zendesk_view_ticket_service(subdomain, emailid, password, page);
					
					if(viewTicketsResponse != null) {
						logger.info("View Ticket Zendesk API response : "+viewTicketsResponse);
						JSONArray tickets = viewTicketsResponse.getJSONArray("tickets");
						
						if(tickets != null && tickets.length() > 0) {
							viewTicketsResponse = formatZendeskTickets(viewTicketsResponse);
							logger.info("After formatting - View Ticket Zendesk API response : "+viewTicketsResponse);
						}
						
						logger.info("Gathered the tickets information successfully... Now redirecting to view_tickets page");
						mv.setViewName(ZendeskConstants.VIEW_ZENDESK_VIEW_TICKETS_PAGE);
						mv.addObject("viewTicketsResponse", viewTicketsResponse.toString());
						mv.addObject("page", page);
					}
					else {
						// API Service is down
						errorFlag = true;
					}
				}
				else {
					// Session invalidated
					errorFlag = true;
				}
			}
			else {
				// Session invalidated
				errorFlag = true;
			}
			
			
			if(errorFlag) {
				// Invalidate session and redirect request to Home page
				session.invalidate();
				mv.setViewName(ZendeskConstants.REQUEST_REDIRECT_TO_HOME_PAGE);
			}
		}
		catch(Exception e) {
			logger.error("Exception occurred inside view_tickets() ::::: ZendeskViewTicketController ", e);
		}
		
		return mv;
	}

	private JSONObject formatZendeskTickets(JSONObject viewTicketsResponse) {
		logger.info("\n\nInside formatZendeskTickets() :::::: ZendeskViewTicketController : Format ticket details to standard way");
		
		try {
			if(viewTicketsResponse != null) {
				JSONArray ticketsArr = viewTicketsResponse.getJSONArray("tickets");
				
				for(int i=0; i<ticketsArr.length(); i++) {
					JSONObject ticket = ticketsArr.getJSONObject(i);
					
					String subject = ticket.isNull("subject") ? "-" : ticket.getString("subject");
					String description = ticket.isNull("description") ? "-" : ticket.getString("description");
					String priority = ticket.isNull("priority") ? "-" : ticket.getString("priority");
					String status = ticket.isNull("status") ? "-" : ticket.getString("status");
					String createdDt = ticket.isNull("created_at") ? "-" : ticket.getString("created_at");
					
					ticket.put("subject", subject);
					ticket.put("description", description);
					ticket.put("priority", priority);
					ticket.put("status", status);
					ticket.put("created_at", createdDt);
				}
				
			}
		}
		catch(Exception e) {
			logger.error("Exception occurred inside formatZendeskTickets() ::::: ZendeskViewTicketController ", e);
		}
		
		return viewTicketsResponse;
	}
}
