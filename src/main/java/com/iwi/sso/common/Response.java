package com.iwi.sso.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {

	private String code = "00";
	private String message;
	private Object data;

	public Response() {
	}

	public Response(Object d) {
		this("00", null, d);
	}

	public Response(String c) {
		this(c, null, null);
	}

	public Response(String c, Object d) {
		this(c, null, d);
	}

	public Response(String c, String m) {
		this(c, m, null);
	}

	public Response(String c, String m, Object d) {
		this.code = c;
		this.message = m;
		this.data = d;
	}

}
