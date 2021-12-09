package com.iwi.sso.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {

	private boolean success = false;
	private String message;
	private Object data;

	public Response() {
	}

	public Response(boolean s) {
		this(false, null, null);
	}

	public Response(Object d) {
		this(true, null, d);
	}

	public Response(boolean s, Object d) {
		this(s, null, d);
	}

	public Response(boolean s, String m) {
		this(s, m, null);
	}

	public Response(boolean s, String m, Object d) {
		this.success = s;
		this.message = m;
		this.data = d;
	}

}
