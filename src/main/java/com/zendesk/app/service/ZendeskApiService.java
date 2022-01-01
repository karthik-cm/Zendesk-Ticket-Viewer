package com.zendesk.app.service;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.zendesk.app.constants.ZendeskConstants;

@Configuration
public class ZendeskApiService {
	
	private static Logger logger = LoggerFactory.getLogger(ZendeskApiService.class);
	
	
	private static final String SUBDOMAIN = "SUBDOMAIN";
	private static final String PAGE = "PAGE";
	
	private static final String STATUS_CODE = "STATUS_CODE";
	private static final String STATUS = "STATUS";
	private static final String ERROR_MSG = "ERROR_MSG";
	
	
	// Zendesk API's
	private static final String ZENDESK_COUNT_TICKETS_API = "https://"+SUBDOMAIN+"/api/v2/tickets/count.json";
	private static final String ZENDESK_LIST_TICKETS_API = "https://"+SUBDOMAIN+"/api/v2/tickets.json?page="+PAGE;
	
	// private static final String ZENDESK_PAGINATION_API = "https://"+SUBDOMAIN+"/api/v2/tickets.json?page[size]=25";
	
	
	public JSONObject authenticate_login(String subdomain, String emailid, String password, String page, HttpSession session) {
		logger.info("\n\nInside authenticate_login() ::::: ZendeskApiService");
		JSONObject loginResponse = new JSONObject();
		
		try {

			if(subdomain != null && emailid != null && password != null) {
				String url = ZENDESK_LIST_TICKETS_API;
				url = url.replaceFirst("SUBDOMAIN", subdomain);
				url = url.replaceFirst("PAGE", page);


				// Check Zendesk service is up + Authenticate user details
				JSONObject apiResponseJson = call_zendesk_api_service(url, emailid, password);

				int statusCode = apiResponseJson.getInt(STATUS_CODE);
				logger.info("HTTP Status Code : "+statusCode);

				if(statusCode == 200) {
					// SUCCESS
					loginResponse.put(STATUS, "SUCCESS");
					logger.info("SUCCESS: Zendesk List Tickets service is up and running! Proceeding to view tickets...");

					session.setMaxInactiveInterval(ZendeskConstants.TIMEOUT_SESSION_IN_SECONDS); // timeout session after 300 seconds (5 min)

					JSONObject params = new JSONObject();
					params.put(ZendeskConstants.SUBDOMAIN, subdomain);
					params.put(ZendeskConstants.EMAILID, emailid);
					params.put(ZendeskConstants.PASSWORD, password);
					
					// Set parameters in session
					session.setAttribute("params", params);
				}
				else {
					// ERROR
					loginResponse.put(STATUS, "ERROR");
					logger.info("ERROR: Some error occurred while processing! Returning to view with appropriate message...");

					if(statusCode == 401) {
						// username / password is wrong - couldn't authenticate you
						loginResponse.put(ERROR_MSG, apiResponseJson.getString("error"));
						logger.info("Error: "+apiResponseJson.getString("error"));
					}
					else if(statusCode == 404) {
						// invalid subdomain name
						loginResponse.put(ERROR_MSG, apiResponseJson.getJSONObject("error").getString("message"));
						logger.info("Error: "+apiResponseJson.getJSONObject("error").getString("message"));
					}
				}
			}
			else {
				loginResponse.put(STATUS, "ERROR");
				loginResponse.put(ERROR_MSG, "Missing mandatory parameters - Subdomain/Emailid/Password");
				logger.info("Error: Missing mandatory parameters from user");
			}

		}
		catch(Exception e) {
			logger.error("Exception occurred inside authenticate_login() ::::: ZendeskApiService "+e);
		}
		
		return loginResponse;
	}
	
	
	//	Get total tickets count (test purpose)
	public JSONObject call_zendesk_count_ticket_service(String subdomain, String emailid, String password) {
		String url = ZENDESK_COUNT_TICKETS_API;
		url = url.replaceFirst("SUBDOMAIN", subdomain);
		
		JSONObject apiResponseJson = new JSONObject();
		
		try {
			apiResponseJson = call_zendesk_api_service(url, emailid, password);
		}
		catch(Exception e)	{
			logger.error("Exception occurred inside call_zendesk_count_ticket_service() ::::: ZendeskApiService ", e);
		}
		
		
		return apiResponseJson;
	}
	
	
	//	View tickets based on page (count=100)
	public JSONObject call_zendesk_view_ticket_service(String subdomain, String emailid, String password, String page) {
		String url = ZENDESK_LIST_TICKETS_API;
		url = url.replaceFirst("SUBDOMAIN", subdomain);
		url = url.replaceFirst("PAGE", page);
		
		
//		String url = ZENDESK_PAGINATION_API;
//		url = url.replaceFirst("SUBDOMAIN", subdomain);
		
		
		JSONObject apiResponseJson = new JSONObject();
		int statusCode = 404;
		
		try {
			apiResponseJson = call_zendesk_api_service(url, emailid, password);
			statusCode = apiResponseJson.getInt(STATUS_CODE);
		}
		catch(Exception e)	{
			logger.error("Exception occurred inside call_zendesk_view_ticket_service() ::::: ZendeskApiService ", e);
		}
		
		
		if(statusCode == 200) {
			return apiResponseJson;
		}
		else {
			return null;
		}
	}
	
	
	// Generic service method to call any Zendesk API service
	public JSONObject call_zendesk_api_service(String url, String emailid, String password) {
		logger.info("\n\nInside call_zendesk_api_service() ::::: ZendeskApiService");
		JSONObject apiResponseJson = new JSONObject();

		try {
			logger.info("Zendesk API : "+url);
			
			HttpGet httpGet = new HttpGet(url);
			
			// Username Password Credentials
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(emailid, password);
			
			
			// Credentials Provider
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, credentials);
			
			
			// Http Client
			try(CloseableHttpClient httpClient = HttpClientBuilder
					.create()
					.setDefaultCredentialsProvider(credentialsProvider)
					.build();){
				
				// Http Response
				HttpResponse httpResponse = httpClient.execute(httpGet);
				
				
				if(httpResponse != null) {
					int statusCode = httpResponse.getStatusLine().getStatusCode();
					
					// Http Entity
					HttpEntity entity = httpResponse.getEntity();
					String response = EntityUtils.toString(entity);
					
					if(response != null) {
						apiResponseJson = new JSONObject(response);
						logger.info("Zendesk API Response : "+apiResponseJson);
					}
					
					apiResponseJson.put(STATUS_CODE, statusCode);
				}
				else {
					apiResponseJson.put(STATUS_CODE, 404);
				}
			}
		}
		catch(IOException e)	{
			apiResponseJson.put(STATUS_CODE, 404);
			logger.error("IOException occurred inside call_zendesk_api_service() ::::: ZendeskApiService ", e);
		}
		catch(Exception e)	{
			apiResponseJson.put(STATUS_CODE, 404);
			logger.error("Exception occurred inside call_zendesk_api_service() ::::: ZendeskApiService ", e);
		}
		
		return apiResponseJson;
	}
	
}