<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>IWI SSO</title>

<script type="text/javascript" src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script type="text/javascript" src="https://code.jquery.com/ui/1.13.0/jquery-ui.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://code.jquery.com/ui/1.13.0/themes/base/jquery-ui.css" />

<link rel="stylesheet" type="text/css" href="/resources/lib/fancytree/src/skin-win8/ui.fancytree.css" />
<script type="text/javascript" src="/resources/lib/fancytree/src/jquery.fancytree.js"></script>

<style type="text/css">
* {
	font-size: 12px;
	color: #333;
	box-sizing: border-box;
	vertical-align: middle;
}

.userListArea {
	float: left;
	width: calc(100% - 600px - 50px);
	height: 400px;
	overflow-y: scroll;
	position: relative;
	border: solid 1px #DDD;
}

.userFormArea {
	float: right;
	width: 600px;
	position: relative;
}

table {
	border-collapse: collapse;
	border-spacing: 0;
	width: 100%;
}

table.grid {
	min-width:800px;
}

table.grid * {
	box-sizing: border-box;
}

table.grid tbody tr {
	cursor: pointer;
}

table.grid th, table.grid td {
	border: solid 1px #DDD;
	text-align: center;
	padding: 0 20px;
	height: 35px;
	line-height: 35px;
}

table.grid th {
	background-color: #F3F3F3;
	border-bottom-color: #AAA;
}

table.grid tr.selected > * {
	background: #F9F9F9;
	font-weight: bold;
}

table.grid tr.selected > *:first-child {
	box-shadow: 5px 0 0 #0288D1 inset;
}

table.grid tr > *:first-child {
	border-left: none;
}

table.grid tr:first-child > * {
	border-top: none;
}

table#userListHeader {
	position: absolute;
	top: 0;
}

table#userList {
	margin-top: 37px;
}

table#userList tr:first-child > * {
	border-top: none;
}

table.form {
	border-collapse: separate;
	border-spacing: 2px;
}

table.form tr > * {
	text-align: left;
	height: 32px;
}

.passwordReset {
	display: none;
}

form input[type=text],
form input[type=password],
form select {
	height: 32px;
	line-height: 32px;
	margin: 0;
	padding: 0 10px;
	border: solid 1px #CCC;
	border-radius: 3px;
	outline: none;
	width: 180px;
}

form input[type=text]:focus,
form input[type=password]:focus,
form select:focus {
	border-color: #4D90FF;
}

form input[data-required],
form input[data-required],
form select[data-required] {
	background: #FFFCEA;
}

form input:read-only,
form input:read-only:focus {
	border: solid 1px #CCC;
	background: #F3F3F3;
	cursor: default;
}

input.datepicker {
	/* cursor:pointer; */
	padding-right:40px;
	background-image:url('/resources/images/icon/calendar.png');
	background-repeat:no-repeat;
	background-position:calc(100% - 10px) calc(50% - 1px);
}

button {
	height: 32px;
	line-height: 32px;
	margin: 0;
	padding: 0 12px;
	border: solid 1px #666;
	border-radius: 3px;
	outline: none;
	background: #666;
	color: #FFF;
	font-weight: bold;
	cursor: pointer;
}

button:active {
	border-color: #333;
	background: #333;
}

button.blue {
	border-color: #4D90FF;
	background: #4D90FF;
}

button.blue:active {
	border-color: #1868C3;
	background: #1868C3;
}

button.red {
	border-color: #C60C30;
	background: #C60C30;
}

button.red:active {
	border-color: #AA0525;
	background: #AA0525;
}

button.wide {
	padding: 0 36px;
}

span.desc {
	vertical-align: middle;
	font-size: 0.9em;
	color: #666;
	letter-spacing: -0.05em;
	padding-left: 5px;
}

strong.desc {
	vertical-align: middle;
	padding-left: 5px;
}

.pl20 {
	padding-left: 20px;
}
</style>
<script type="text/javascript">
	var formatTel = function(str) {
		str = formatNum(str);
		if (str) {
			if (str.length == 8) {
				str = str.replace(/^([0-9]{4})([0-9]{4})$/, "$1-$2");
			} else if (str.length == 12) {
				str = str.replace(/(^[0-9]{4})([0-9]{4})([0-9]{4})$/, "$1-$2-$3");
			} else {
				str = str.replace(/(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$/, "$1-$2-$3");
			}
		}
		return str;
	};

	var formatNum = function(str) {
		if (str) {
			str = str.replace(/[^0-9]/g, "");
		}
		return str;
	};
	
	var formatDate = function(str) {
		str = formatNum(str);
		if(str.length == 6) {
			str = str.replace(/(^[0-9]{2})([0-9]{2})([0-9]{2})$/, "$1-$2-$3");
		} else if(str.length == 8) {
			str = str.replace(/(^[0-9]{4})([0-9]{2})([0-9]{2})$/, "$1-$2-$3");
		}
		return str;
	}

	var getJsonData = function(url, method, param) {
		var json;
		if (url) {
			$.ajax({
				url : url,
				method : ((!method) ? "get" : method),
				data : ((!param) ? {} : param),
				dataType : "json",
				async : false,
				success : function(response) {
					if (response.code == "00") {
						json = response.data;
					} else if (response.code == "11") {
						alert("인증 토큰이 만료되었습니다.");
						location.replace("/login");
					} else {
						alert(response.message);
					}
				},
				error : function(e) {
					console.log(e);
				}
			});
		}
		return json;
	};

	var getUserList = function(param) {
		var json = getJsonData("/mng/user/list", "get", {});
		if (json) {
			var grid = $(".grid > tbody").empty();
			$.each(json, function(i, user) {
				var tr = $("<tr></tr>", {
					"data-user-seq" : user.userSeq
				});

				$("<td></td>", { html : user.userNo }).appendTo(tr);
				$("<td></td>", { html : user.userNm }).appendTo(tr);
				$("<td></td>", { html : user.email }).appendTo(tr);
				$("<td></td>", { html : user.deptNm }).appendTo(tr);
				$("<td></td>", { html : user.posiNm }).appendTo(tr);
				$("<td></td>", { html : user.dutyNm }).appendTo(tr);

				tr.appendTo(grid);
			});
		}
	}

	var getUserInfo = function(userSeq) {
		if (userSeq) {
			var user = getJsonData("/mng/user/" + userSeq, "get", {});
			if (user) {
				var form = $("form#frmUser");

				form.find("input[name=userId]").prop("readonly", true);
				form.find("input[name=userPw]").prop("disabled", true).val("");
				form.find(".passwordInput").hide();
				form.find(".passwordReset").show();
				
				form.find("input,select").each(function(i) {
					var name = $(this).attr("name");
					$(this).val(user[name]).trigger("change");
				});

				return true;
			}
		}
		return false;
	};

	var fnValidate = function(form) {
		var result = true;
		if (form && form.length > 0) {
			form.find("[data-required]:not(:disabled)").each(function(i) {
				if (!$(this).val()) {
					var item = $(this).data("required");
					$(this).trigger("change").focus();
					setTimeout(function() {
						alert("[" + item + "] 필수 입력항목입니다.");
					});
					result = false;
					return false;
				}
			});
		}
		return result;
	};

	var fnSaveUser = function() {
		var form = $("form#frmUser");
		if (form.length > 0) {
			if (fnValidate(form)) {
				var userSeq = form.find("input[name=userSeq]").val();
				var msg = (userSeq) ? "사용자 정보를 수정" : "사용자를 생성";
				if (confirm(msg + " 하시겠습니까?")) {
					var param = form.serializeArray();
					var json = getJsonData("/mng/user/save", "post", param);
					if (json) {
						// save
						console.log(json);
					}
				}
			}
		}

		return false;
	};

	var fnResetUser = function() {
		if (confirm("입력을 취소 하시겠습니까?")) {
			var form = $("form#frmUser");
			if (form.length > 0) {
				var userSeq = form.find("input[name=userSeq]").val();
				if (userSeq) {
					getUserInfo(userSeq);
				} else {
					form[0].reset();
				}
			}
		}
	};

	var getTreeData = function(array, vValue, vText, vParent) {
		var map = {};
		for (var i = 0; i < array.length; i++) {
			var obj = {
				"value" : array[i][vValue],
				"text" : array[i][vText],
				"level" : array[i]['level']
			};
			obj.children = [];
			map[obj.value] = obj;
			var parent = array[i][vParent] || '-';
			if (!map[parent]) {
				map[parent] = {
					children : []
				};
			}
			map[parent].children.push(obj);
		}
		return map['-'].children;
	}
	
	$(function() {
		$(".datepicker").datepicker({
			showOtherMonths : true,
			selectOtherMonths : true,
			dateFormat: 'yymmdd',
			prevText: '이전 달',
			nextText: '다음 달',
			monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
			monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
			dayNames: ['일', '월', '화', '수', '목', '금', '토'],
			dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
			dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
			showMonthAfterYear: true,
			yearSuffix: '년',
			changeMonth: true,
			changeYear: true
		});

		$("div.userListArea").on("scroll", function(e) {
			$(this).find("table#userListHeader").css("top", $(this).scrollTop() + "px");
			$(this).find("table#userList tr:last-child > * ").css("border-bottom", "none");
			if($(this).scrollTop() > 0) {
				$(this).find("table#userListHeader").css("box-shadow", "0 0 5px rgba(0, 0, 0, 0.2)");
			} else {
				$(this).find("table#userListHeader").css("box-shadow", "unset");
			}
		});

		$("input[name=userId]").on("keyup keydown change blur", function(e) {
			if ($(this).val()) {
				$("input[name=email]").val($(this).val() + "@iwi.co.kr");
			} else {
				$("input[name=email]").val("");
			}
			
			if(!$("input[name=userPw]").is(":disabled")) {
				$("input[name=userPw]").val($(this).val());
			}
		});
		
		$("input[name=userPw]").on("focus", function(e) {
			$(this).select();
		});

		$("input[data-format]").on("keyup keydown change blur", function(e) {
			var format = $(this).data("format");
			if (format == "num") {
				$(this).val(formatNum($(this).val()));
			} else if (format == "tel") {
				$(this).val(formatTel($(this).val()));
			} else if(format == "date") {
				$(this).val(formatDate($(this).val()));
			}
		});

		$("*[data-required]").on("keyup keydown change blur", function(e) {
			if(!$(this).is(":disabled")) {
				if (!$(this).val()) {
					$(this).css("border-color", "#F00");
				} else {
					$(this).css("border-color", "");
				}
			}
		});
		
		$(document).on("click", "table.grid > tbody > tr", function(e) {
			var userSeq = $(this).data("user-seq");
			if (getUserInfo(userSeq)) {
				$(this).addClass("selected").siblings().removeClass("selected");
			}
		});
		
		var deptList = ${deptList};
		var treeDept = getTreeData(deptList, "deptCd", "deptNm", "upDeptCd");
		console.log(treeDept);
		
		getUserList();
	});
</script>
</head>
<body>
	<div class="userListArea">
		<table id="userListHeader" class="grid">
			<colgroup>
				<col style="width: 100px;" />
				<col style="width: 150px;" />
				<col />
				<col style="width: 200px;" />
				<col style="width: 100px;" />
				<col style="width: 100px;" />
			</colgroup>
			<thead>
				<tr>
					<th>사번</th>
					<th>이름</th>
					<th>이메일</th>
					<th>부서</th>
					<th>직급</th>
					<th>직무</th>
				</tr>
			</thead>
		</table>
		<table id="userList" class="grid">
			<colgroup>
				<col style="width: 100px;" />
				<col style="width: 150px;" />
				<col />
				<col style="width: 200px;" />
				<col style="width: 100px;" />
				<col style="width: 100px;" />
			</colgroup>
			<tbody>
			</tbody>
		</table>
	</div>
	<div class="userFormArea">
		<form id="frmUser" onsubmit="return fnSaveUser();">
			<input type="hidden" name="userSeq" /> <input type="hidden" name="email" />
			<table class="form">
				<colgroup>
					<col style="width: 80px;" />
					<col />
				</colgroup>
				<tbody>
					<tr>
						<th>아이디</th>
						<td><input type="text" name="userId" maxlength="20" data-required="아이디" /> <strong class="desc">@iwi.co.kr</strong></td>
					</tr>
					<tr>
						<th>비밀번호</th>
						<td>
							<div class="passwordInput">
								<input type="password" name="userPw" maxlength="20" data-required="비밀번호" />
								<span class="desc">초기 비밀번호는 아이디와 동일하게 설정됩니다.</span>
							</div>
							<div class="passwordReset">
								<button type="button">초기화</button>
								<span class="desc">비밀번호 초기화 시 아이디와 동일하게 변경됩니다.</span>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<table class="form" style="margin-top: 20px;">
				<colgroup>
					<col style="width: 80px;" />
					<col />
					<col style="width: 80px;" />
					<col />
				</colgroup>
				<tbody>
					<tr>
						<th>이름</th>
						<td><input type="text" name="userNm" maxlength="20" data-required="이름" /></td>
						<th class="pl20">성별</th>
						<td><select name="userSex" data-required="성별">
								<option value="">선택</option>
								<option value="M">남자</option>
								<option value="F">여자</option>
								<option value="Z">기타</option>
						</select></td>
					</tr>
					<tr>
						<th>사번</th>
						<td><input type="text" name="userNo" maxlength="20" data-format="num" /></td>
						<th class="pl20">직급</th>
						<td><select name="posiCd" data-required="직급">
								<option value="">선택</option>
								<c:forEach items="${posiList}" var="posi">
									<option value="${posi.posiCd}">${posi.posiNm}</option>
								</c:forEach>
						</select></td>
					</tr>
					<tr>
						<th>부서</th>
						<td>
							<%-- <select name="deptCd" data-required="부서">
								<option value="">선택</option>
								<c:forEach items="${deptList}" var="dept">
									<option value="${dept.deptCd}">${dept.deptNm}</option>
								</c:forEach>
							</select> --%>
							<input type="text" name="deptCd" data-required="부서" />
							<div class="dialog tree" style="position:absolute; padding:20px; border:solid 1px #DDD; background:#FFF;">
								<ul>
									<c:forEach items="${deptList}" var="dept">
										<li>${dept.deptNm}</li>
									</c:forEach>
								</ul>
							</div>
							<!-- <script>$(".tree").fancytree();</script> -->
						</td>
						<th class="pl20">직무</th>
						<td><select name="dutyCd" data-required="직무">
								<option value="">선택</option>
								<c:forEach items="${dutyList}" var="duty">
									<option value="${duty.dutyCd}">${duty.dutyNm}</option>
								</c:forEach>
						</select></td>
					</tr>
					<tr>
						<th>입사일자</th>
						<td><input type="text" name="entryYmd" class="datepicker" data-format="date" /></td>
						<th class="pl20">퇴사일자</th>
						<td><input type="text" name="quitYmd" class="datepicker" data-format="date" /></td>
					</tr>
				</tbody>
			</table>
			<table class="form" style="margin-top: 20px;">
				<colgroup>
					<col style="width: 80px;" />
					<col />
					<col style="width: 80px;" />
					<col />
				</colgroup>
				<tbody>
					<tr>
						<th>휴대폰번호</th>
						<td><input type="text" name="userHp" maxlength="20" data-format="tel" data-required="휴대폰번호" /></td>
						<th class="pl20">생년월일</th>
						<td><input type="text" name="userBirth" class="datepicker" data-format="date" /></td>
					</tr>
					<tr>
						<th>전화번호</th>
						<td><input type="text" name="userTel" maxlength="20" data-format="tel" /></td>
						<th class="pl20">팩스번호</th>
						<td><input type="text" name="userFax" maxlength="20" data-format="tel" /></td>
					</tr>
				</tbody>
			</table>
			<div style="margin-top: 30px; text-align: right;">
				<button type="submit" class="wide red">저장</button>
				<button type="button" class="wide" onclick="fnResetUser();">취소</button>
			</div>
		</form>
	</div>
</body>
</html>