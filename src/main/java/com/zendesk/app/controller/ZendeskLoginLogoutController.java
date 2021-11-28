package com.zendesk.app.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zendesk.app.constants.ZendeskConstants;
import com.zendesk.app.service.ZendeskApiService;

@Controller
@RequestMapping(ZendeskConstants.REQUEST_ZENDESK_TICKET_VIEWER)
public class ZendeskLoginLogoutController {
	
	private static Logger logger = LoggerFactory.getLogger(ZendeskLoginLogoutController.class);
	
	@Autowired
	ZendeskApiService zendeskApiService;
	
	
	@RequestMapping(value = ZendeskConstants.REQUEST_LOGIN, method = RequestMethod.POST)
	@ResponseBody
	public String login(HttpServletRequest request, HttpSession session) {
		logger.info("\n\nInside login() :::::: ZendeskLoginLogoutController : Login functionality");
		Map<String, String[]> params = request.getParameterMap();
		
		JSONObject loginResponse = new JSONObject();
		
		try {
			String subdomain = params.containsKey(ZendeskConstants.SUBDOMAIN) ? params.get(ZendeskConstants.SUBDOMAIN)[0] : null;
			String emailid = params.containsKey(ZendeskConstants.EMAILID) ? params.get(ZendeskConstants.EMAILID)[0] : null;
			String password = params.containsKey(ZendeskConstants.PASSWORD) ? params.get(ZendeskConstants.PASSWORD)[0] : null;
			String page = "1";
			
			logger.info("Form details ::::: SUBDOMAIN: "+subdomain +", EMAILID: "+emailid +", PASSWORD: "+password);
			
			
			// Authenticate user login
			loginResponse = zendeskApiService.authenticate_login(subdomain, emailid, password, page, session);
			
			logger.info("Login Response JSON : "+loginResponse);
		}
		catch(Exception e) {
			logger.error("Exception occurred inside login() ::::: ZendeskApiService ", e);
		}
		
		return loginResponse.toString();
	}
	
	
	@RequestMapping(value = ZendeskConstants.REQUEST_LOGOUT, method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpSession session) {
		logger.info("\n\nInside logout() :::::: ZendeskLoginLogoutController : Logout functionality");
		
		// Invalidate the user session
		session.invalidate();
		
		logger.info("Invalidated the user session... Now redirecting to HOME page");
		
		logger.info(":::::::::::: APPLICATION END ::::::::::::\\n");
		
		// Redirect to Home page
		return ZendeskConstants.REQUEST_REDIRECT_TO_HOME_PAGE;
	}
	
}
