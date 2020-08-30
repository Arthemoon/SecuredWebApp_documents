package com.patrikmaryska.bc_prace.bc_prace.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 @Component
 @Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig implements Filter {

   @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        if(((HttpServletRequest) req).getMethod().equals("TRACE") || ((HttpServletRequest) req).getMethod().equals("CONNECT")
          ||((HttpServletRequest) req).getMethod().equals("HEAD")){
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ((HttpServletRequest) req).getMethod() + " is not allowed.");
            return;
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET , DELETE, PUT");
        response.setHeader("Access-Control-Max-Age","3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-type, Authorization");

        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET , DELETE, PUT");
            response.setHeader("Access-Control-Max-Age","3600");
            response.setHeader("Access-Control-Allow-Headers", "Content-type, Authorization");
        } else {
            chain.doFilter(req, res);
        }
    }


    @Override
    public void destroy() {

    }
}
