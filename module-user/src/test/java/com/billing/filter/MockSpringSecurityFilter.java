package com.billing.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class MockSpringSecurityFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) {}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
		SecurityContextHolder.getContext()
			.setAuthentication((Authentication) ((HttpServletRequest) req).getUserPrincipal());
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {
		SecurityContextHolder.clearContext();
	}
}
