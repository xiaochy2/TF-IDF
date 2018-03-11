function EnterPress(e){   
    var e = e || window.event;   
    if (e.keyCode == 13) {   
	    console.log("press the enter key");
	    //var query = document.getElementById("autocomplete").value;
	    // pass the value of the input box to the handle function
	    handleSearch($('#inputbox').val())
	    //handleNormalSearch(query)
    }   
}  

function handleSearch(query) {
	console.log("doing search with query: " + query);
	// TODO: you should do normal search here
	jQuery.ajax({
		"method": "GET",
		// generate the request url from the query.
		// escape the query string to avoid errors caused by special characters 
		"url": "Search?query=" + query,
		"success": function(data) {
			// pass the data, query, and doneCallback function into the success handler
			handleSearchResult(data)
		},
		"error": function(errorData) {
			console.log("lookup ajax error")
			console.log(errorData)
		}
	})	
}

function handleSearchResult(resultData) {
	//resultDataJson = JSON.parse(resultDataString);
	
	console.log("handle search response");
	console.log(resultData);
	if (resultData != ""&& resultData != null) {
		var query = document.getElementById("inputbox").value;
		var url = "/SearchEngine/Search?&query="+query;
		window.location.href = url;
		
		
	} else {
		console.log("show error message");
		jQuery("#search_error_message").text("Sorry, no result exists!");
	}
	
	
	
	
	
	
	
	//window.location.href = "/Search";

	/*
	if (resultData != ""&& resultData != null) {
		var title = document.getElementById("inputbox").value;
		window.location.href = url;
		
		
	} else {
		console.log("show error message");
		jQuery("#search_error_message").text("Sorry, no result exists!");
	}
	*/
}
