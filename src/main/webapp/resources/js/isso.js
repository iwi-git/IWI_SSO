var ISSO = function() {

	if((typeof $) != "function") {
		console.log("need jQuery");
	}

	this.SSO_URL = "http://b.iwi.co.kr:9000";
	
	this.fnLogin = function(obj, callbackFunction) {
		var form = null;
		
		if((typeof obj) == "string") {
			form = $("form#" + obj);
		} else if((typeof obj) == "object" && $(obj).length > 0) {
			form = $(obj);
		}
		
		if(!form || form.length < 1) {
			console.log("define login form");
		} else {
			var json = this.getJsonData("post", this.SSO_URL+"/auth/signin", form.getJsonStr());
			
			if(callbackFunction) {
				if((typeof callbackFunction) == "function") {
					window[callbackFunction.name](json);
				} else if((typeof callbackFunction) == "string" && (typeof window[callbackFunction]) == "function") {
					window[callbackFunction](json);
				}
				
				return;
			} else if((typeof obj) == "object" && obj.tagName.toUpperCase() == "FORM") {
				if(json.success) {
					var action = form.attr("action");
					if(action) {
						location.href = action;
					}
				} else {
					alert(json.message);
				}
				
				return false;
			}
			
			return json;
		}
	};
	
	this.isLogin = function() {
		
		return false;
	}
	
	this.getJsonData = function(method, url, params, async) {
		if(params == null || params == undefined) params = {};
		if(async == null || async == undefined) async = false;
		
		var json = null;
		
		if(method && url) {
			$.ajax({
				method : method,
				url : url,
				data : params,
				async : async,
				beforeSend : function(xhr) {
					xhr.setRequestHeader("Content-type", "application/json");
				},
				xhrFields : {
					withCredentials : true
				},
				success : function(resp) {
					json = resp;
				}
			});
		}
		
		return json;
	};

};

if((typeof $) == "function") {

	jQuery.fn.getJsonStr = function() {
		var json = null;
		var obj = null;
		try {
			if (this[0].tagName && this[0].tagName.toUpperCase() == "FORM") {
				var arr = this.serializeArray();
				if (arr) {
					obj = {};
					jQuery.each(arr, function() {
						obj[this.name] = this.value;
					});
				}
			}
			json = JSON.stringify(obj);
		} catch (e) {
			alert(e.message);
		}
		return json;
	};
	
}