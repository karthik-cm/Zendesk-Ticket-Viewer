package com.zendesk.app.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zendesk.app.constants.ZendeskConstants;

@Controller
public class ZendeskErrorController implements ErrorController {
	
	@RequestMapping(value = ZendeskConstants.REQUEST_ERROR, method = RequestMethod.GET)
    public String handleError(HttpServletRequest request) {
        return ZendeskConstants.VIEW_ZENDESK_ERROR_PAGE;
    }
	
	
	public String getErrorPath() {
        return ZendeskConstants.REQUEST_ERROR;
    }
}
