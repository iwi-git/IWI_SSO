<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/taglib.jsp"%>
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
				url : "/auth/signin",
				data : $("#frmLogin").getJsonStr(),
				beforeSend : function(xhr) {
					xhr.setRequestHeader("Content-type", "application/json");
				},
				success : function(resp) {
					if (resp.code == "00") {
						location.replace("/");
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
		<input type="text" name="email" value="kjg@iwi.co.kr" /><br />
		<input type="password" name="password" value="01091663305" /><br />
		<button type="submit">로그인</button>
	</form>
	
</body>
</html>