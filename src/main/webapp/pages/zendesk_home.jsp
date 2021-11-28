<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Zendesk Ticket Viewer</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<!--  CDN Import : Bootstrap, jQuery-->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
  	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>

	<div class="container" style="padding: 50px 150px 0px 150px">
		<div style="padding-bottom: 50px;">
			<img src="../images/zendesk.png" style="height: 50px;" />
			<h2>Zendesk Ticket Viewer</h2>
		</div>

		<div>
			<h4 style="padding-bottom: 15px;">Login with your credentials to view tickets</h4>
			
			<!-- to display error message -->
			<div style="padding-bottom: 10px;">
				<span id="error-msg" style="color: red; font-weight: bold;"></span>
			</div>
		
			<!-- Login form -->
			<form id="login-form">
				<div class="form-group">
					<label for="subdomain" class="font-weight-bold">Subdomain<span style="color: red"><sup>*</sup></span></label> 
					<input type="text" class="form-control" placeholder="Enter your subdomain name - for ex: zccstudents.zendesk.com" id="subdomain">
				</div>
				
				<div class="form-group">
					<label for="emailid" class="font-weight-bold">Email address <span style="color: red"><sup>*</sup></span></label>
					<input type="email" class="form-control" placeholder="Enter your registered email" id="emailid">
				</div>
				
				<div class="form-group">
					<label for="password" class="font-weight-bold">Password <span style="color: red"><sup>*</sup></span></label>
					<input type="password" class="form-control" placeholder="Enter your password" id="password">
				</div>

				<div style="margin-top: 50px;">
					<button type="reset" class="btn btn-info" style="padding: 10px; width: 25%;">Reset</button>
					<button type="button" class="btn btn-success" onclick="loginToApp()" style="padding: 10px; margin-left: 25px; width: 25%;">Login</button>
				</div>
			</form>
		</div>
	</div>

</body>

<script>

function loginToApp(){
	$('#error-msg').text('');
	
	let subdomain = $('form#login-form').find('#subdomain').val();
	let emailid = $('form#login-form').find('#emailid').val();
	let password = $('form#login-form').find('#password').val();
	
	
	if(subdomain != null && subdomain.length > 0 && emailid != null && emailid.length > 0 && password.length > 0 && password != null ){
		
		// Check for Subdomain field
		if(subdomain != null && subdomain.length > 0 ){
			let subdomainArr = subdomain.split('.');
			let errorFlag = false;
			
			if(subdomainArr.length == 3){
				if(subdomainArr[1] != 'zendesk' || subdomainArr[2] != 'com'){
					errorFlag = true;
				}
			}
			else{
				errorFlag = true;
			}
			
			if(errorFlag){
				$('#error-msg').text('Error: Invalid Subdomain');
				return;
			}
		}
		
		
		// Authenticate user login - jQuery AJAX call
		$.ajax({
			type: 'POST',
			url: '/zendeskTicketViewer/login',
			data: {
				'subdomain': subdomain,
				'emailid': emailid,
				'password': password
			},
			success: function(response){
				var loginResponse = JSON.parse(response);
				var status = loginResponse.hasOwnProperty('STATUS') ? loginResponse.STATUS : null;
				
				if(status != null){
					if(status == 'SUCCESS'){
						view_tickets(1);
					}
					else if(status == 'ERROR'){
						$('#error-msg').text('Error: '+loginResponse.ERROR_MSG);
					}
				}
				else{
					$('#error-msg').text('Error: Zendesk services are currently down temporarily. Please try login after sometime.');
				}
			},
			error: function(response){
				$('#error-msg').text('Error: Zendesk services are currently down temporarily. Please try login after sometime.');
			}
		});
	}
	else{
		// missing mandatory fields
		$('#error-msg').text('Error: Fields marked with * are mandatory');
	}
}


function view_tickets(page){
	window.location.href = 'view_tickets?page='+page;
}

</script>
</html>