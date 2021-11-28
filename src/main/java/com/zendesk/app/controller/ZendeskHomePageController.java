package com.zendesk.app.controller;


import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zendesk.app.constants.ZendeskConstants;

@Controller
@RequestMapping(ZendeskConstants.REQUEST_ZENDESK_TICKET_VIEWER)
public class ZendeskHomePageController {
	
	Logger logger = LoggerFactory.getLogger(ZendeskHomePageController.class);
	
	@RequestMapping(value = ZendeskConstants.REQUEST_HOME_PAGE, method = RequestMethod.GET)
	public String request_home_page(HttpSession session) {
		logger.info("\n\n:::::::::::: APPLICATION START ::::::::::::\n");
		logger.info("Inside request_home_page() :::::: ZendeskHomePageController : Loading the home page for Zendesk Application");
		return ZendeskConstants.VIEW_ZENDESK_HOME_PAGE;
	}
}
