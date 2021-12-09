<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title>IWI SSO Login</title>
	<script type="text/javascript" src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	<script type="text/javascript">
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
		
		var fnLogin = function() {
			$.ajax({
				method : "post",
				url : "http://b.iwi.co.kr:9000/auth/signin",
				data : $("#frmLogin").getJsonStr(),
				beforeSend : function(xhr) {
					xhr.setRequestHeader("Content-type", "application/json");
					xhr.setRequestHeader("Authorization", "Bearer 0E697311A48144C89F51A324A030A67B");
				},
				xhrFields : {
					withCredentials : true
				},
				success : function(resp) {
					if (resp.success) {
						console.log(resp.data);
						alert("로그인 성공");
					} else {
						alert(resp.message);
					}
				}
			});
			return false;
		};
	</script>
</head>
<body>
	
	<form id="frmLogin" name="frmLogin" method="post" onsubmit="return fnLogin();">
		<input type="text" name="email" /><br />
		<input type="password" name="password" /><br />
		<button type="submit">로그인</button>
	</form>
	
</body>
</html>