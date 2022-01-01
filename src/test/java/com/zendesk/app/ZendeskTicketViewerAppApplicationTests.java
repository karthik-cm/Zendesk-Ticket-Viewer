package com.zendesk.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.zendesk.app.service.ZendeskApiService;

class ZendeskTicketViewerAppApplicationTests {

	// Valid credentials
	private static String SUBDOMAIN = ""; 
	private static String EMAILID = ""; 
	private static String PASSWORD = "";
	private static String PAGE = "1";


	// Invalid credentials
	private static String INVALID_SUBDOMAIN = ""; 
	private static String INVALID_EMAILID = ""; 
	private static String INVALID_PASSWORD = "";


	private static final String ZENDESK_LIST_TICKETS_API = "https://"+SUBDOMAIN+"/api/v2/tickets.json?page="+PAGE;
	private static final String INVALID_ZENDESK_LIST_TICKETS_API = "https://"+INVALID_SUBDOMAIN+"/api/v2/tickets.json?page="+PAGE;



	private static ZendeskApiService zendeskApiService;


	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		zendeskApiService = new ZendeskApiService();
	}



	@Test
	void test_call_zendesk_api_service() {
		/*** TO TEST ZENDESK SERVICE API ***/

		// Right credentials
		JSONObject apiResponse = zendeskApiService.call_zendesk_api_service(ZENDESK_LIST_TICKETS_API, EMAILID, PASSWORD);
		assertEquals(200, apiResponse.getInt("STATUS_CODE")); // Expected HTTP STATUS CODE IS 200



		// Wrong credentials - invalid URL (invalid subdomain)
		JSONObject apiResponse2 = zendeskApiService.call_zendesk_api_service(INVALID_ZENDESK_LIST_TICKETS_API, EMAILID, PASSWORD);
		assertEquals(404, apiResponse2.getInt("STATUS_CODE")); // Expected HTTP STATUS CODE IS 404


		// Wrong credentials - invalid emailid
		JSONObject apiResponse3 = zendeskApiService.call_zendesk_api_service(ZENDESK_LIST_TICKETS_API, INVALID_EMAILID, PASSWORD);
		assertEquals(401, apiResponse3.getInt("STATUS_CODE")); // Expected HTTP STATUS CODE IS 401


		// Wrong credentials - invalid password
		JSONObject apiResponse4 = zendeskApiService.call_zendesk_api_service(ZENDESK_LIST_TICKETS_API, EMAILID, INVALID_PASSWORD);
		assertEquals(401, apiResponse4.getInt("STATUS_CODE")); // Expected HTTP STATUS CODE IS 401
	}


	@Test
	void test_call_zendesk_view_ticket_service() {
		/*** TO TEST ZENDESK VIEW TICKET SERVICE METHOD ***/


		// Right credentials
		JSONObject apiResponse = zendeskApiService.call_zendesk_view_ticket_service(SUBDOMAIN, EMAILID, PASSWORD, PAGE);
		assertEquals(100, apiResponse.getJSONArray("tickets").length()); // tickets array length is expected is 100



		// Wrong credentials - invalid subdomain
		JSONObject apiResponse2 = zendeskApiService.call_zendesk_view_ticket_service(INVALID_SUBDOMAIN, EMAILID, PASSWORD, PAGE);
		assertEquals(null, apiResponse2); // expecting null response if invalid subdomain

		// Wrong credentials - invalid emailid
		JSONObject apiResponse3 = zendeskApiService.call_zendesk_view_ticket_service(SUBDOMAIN, INVALID_EMAILID, PASSWORD, PAGE);
		assertEquals(null, apiResponse3); // expecting null response if invalid email


		// Wrong credentials - invalid password
		JSONObject apiResponse4 = zendeskApiService.call_zendesk_view_ticket_service(SUBDOMAIN, EMAILID, INVALID_PASSWORD, PAGE);
		assertEquals(null, apiResponse4); // expecting null response if invalid password
	}

}
