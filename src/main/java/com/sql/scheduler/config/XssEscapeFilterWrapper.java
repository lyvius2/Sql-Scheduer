package com.sql.scheduler.config;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssEscapeFilterWrapper extends HttpServletRequestWrapper {
	private XssEscapeFilter xssEscapeFilter;
	private String path = null;

	public XssEscapeFilterWrapper(ServletRequest request, XssEscapeFilter xssEscapeFilter) {
		super((HttpServletRequest) request);
		this.xssEscapeFilter = xssEscapeFilter;
		this.path = ((HttpServletRequest) request).getRequestURI();
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		return doFilter(name, value);
	}

	@Override
	public String[] getParameterValues(String name) {
		String values[] = super.getParameterValues(name);
		if (values == null) {
			return values;
		}
		for (int index = 0; index < values.length; index++) {
			values[index] = doFilter(name, values[index]);
		}
		return values;
	}

	/**
	 * @param name String
	 * @param value String
	 * @return String
	 */
	private String doFilter(String name, String value) {
		if (name.equals("performing") || name.equals("conditional") ||
				name.equals("dbUrl") || name.equals("beginWsUrl") || name.equals("endWsUrl")) return value;
		else return xssEscapeFilter.doFilter(path, name, value);
	}
}