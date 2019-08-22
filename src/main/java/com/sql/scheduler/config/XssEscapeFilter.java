package com.sql.scheduler.config;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class XssEscapeFilter extends XssEscapeServletFilter {
	private com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter xssEscapeFilter = com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter.getInstance();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new XssEscapeFilterWrapper(request, xssEscapeFilter), response);
	}
}