<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
  	
	
	<!-- jQuery table (pagination + sorter + search) JS -->
	<script src="../js/table-sorter.js"></script>
	
	<!-- jQuery table export JS (csv export) -->
	<script src="../js/tableHTMLExport.js"></script>
</head>

<body>
	<div class="container" style="padding: 50px 0px 0px 0px">
		<div style="padding-bottom: 100px;">
			<button class="btn btn-danger" onclick="logout()" style="float: right; width: 15%; padding: 8px;">Logout</button>
			
			<img src="../images/zendesk.png" style="height: 50px;" />
			<h2>Zendesk Ticket Viewer</h2>
		</div>

		<div>
			<div id="ticket-count-div">
				<h4 style="float: right; background: yellow; padding: 8px; font-weight: bold;">Total Tickets : <span id="ticketCount"></span> </h4>
			</div>
			
			<div id="ticket-details-div" style="width: 50%; display: flex; padding-bottom: 50px;">
				<select class="form-control" id="tickets-range" style="width: 40%"></select>
				<button class="btn btn-primary" onclick="getTicketDetails()" style="width: 25%; padding: 7px; margin-left: 25px;">View Tickets</button>
			</div>
			
			<div id="ticket-export-div" style="padding-bottom: 25px;">
				<input type="text" id="search" class="form-control" placeholder="Search in table..." style="width: 25%; float: right; margin-bottom: 10px;">
				<button class="btn btn-success" onclick="exportTable('csv')">Export to CSV</button>
			</div>
			
			<div>
				<div><span id="error-msg" style="color: red; font-weight: bold;"></span></div>
				
				<!-- Tickets Table will be rendered here -->
				<div id="tickets-table" class="table-sortable"></div>
			</div>
		</div>
	</div>
	
</body>


<script>
$(document).ready(function(){
	// View Tickets Response from Service API
	var viewTicketsResponse = JSON.parse(JSON.stringify(${viewTicketsResponse}));
	
	if(viewTicketsResponse != null){
		var tickets = viewTicketsResponse.hasOwnProperty('tickets') ? viewTicketsResponse.tickets : null;
		
		if(tickets != null && tickets.length > 0){
			var count = parseInt(viewTicketsResponse.count);
			var dropdownCount = Math.ceil(parseFloat(count/100));
		
			$('#ticketCount').text(count);
			
			
			// form dropdown
			formTicketsListDropdown(dropdownCount, 'tickets-range');
			
			var page = JSON.parse(JSON.stringify(${page}))
			$('#tickets-range').val(page);
			
			var columns = {
			    'id':'Id',
			    'subject':'Subject',
			    'description':'Description',
			    'priority':'Priority',
			    'status':'Status',
			    'created_at':'Created Date'
			}
			
			// Create tickets table & apply pagination
			formTableAndApplyPagination('tickets-table', tickets, columns, 'search');
		}
		else{
			$('div > span#error-msg').text('Error: Invalid response from the Zendesk API (tickets property not present)');
			$('#ticket-count-div, #ticket-export-div, #ticket-details-div').hide();
		}
	}
	else{
		$('div > span#error-msg').text('Error: No response from the Zendesk API. Please try after sometime.');
		$('#ticket-count-div, #ticket-export-div, #ticket-details-div').hide();
	}
});



function formTicketsListDropdown(count, id){
	var options = '';
	
	var start = 0;
	for(var i=1; i<=count; i++){
		var end = i * 100;
		options += '<option value="'+i+'">' +'Set-' +i +' ('+(start+1) +' - ' +end+')' +'</option>';
		start = end;
	}
	
	$('#'+id).html(options);
}



function formTableAndApplyPagination(id, tickets, columns, searchField){
	
	var table = $('#'+id).tableSortable({
		data: tickets,
        columns: columns,
        searchField: '#'+searchField,
        formatCell: null,
        rowsPerPage: 25,
        pagination: true,
        tableDidUpdate: function() {
        	styleTable();
        },
        onPaginationChange: function(nextPage, setPage) {
            setPage(nextPage);
        }
    });
	
	
	// style table css
	styleTable();
	
	
	if(tickets.length == 0){
		$('tbody.gs-table-body').html('<tr><td colspan="6" style="text-align: center; font-weight: bold;">No tickets found for this page</td></tr>');
	}
}



function exportTable(type){
	var timestamp = Date.now();
	var filename = 'tickets_extract_' +timestamp +'.csv';
	
	$('.gs-table').tableHTMLExport({
	  type: type,
	  filename: filename,
	});
}


function styleTable(){
	$('table.table').addClass('table-bordered');
	$('thead.gs-table-head').css({'background-color': 'lightgrey'});
	$('.gs-table-head').find('tr').find('th:eq(2)').css({'width': '650px'});
}


function getTicketDetails(){
	var page = $('select#tickets-range').val();
	window.location.href = 'view_tickets?page='+page;
}


function logout(){
	window.location.href = 'logout';
}

</script>
</html>