<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>IWI SSO</title>
</head>
<body>
	IWI SSO
	<br />
	<br />
	${session}
	<br />
	<br />
	<a href="/mng/user">사용자 관리</a>
	<br />
	부서 관리
	<br />
	직급 관리
	<br />
	직무 관리
</body>
</html>