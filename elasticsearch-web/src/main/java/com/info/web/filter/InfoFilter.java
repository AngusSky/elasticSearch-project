package com.info.web.filter;

import com.alibaba.fastjson.JSONObject;
import com.angus.PathPatternMatcher;
import com.angus.ThreadLocalResourceHolder;
import com.angus.util.NetWorkUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(InfoFilter.class);

    private List<String> excludePath;
    public static final String USER_REQUEST_ID = "user_request_id";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludePathv = filterConfig.getInitParameter("excludePath");
        excludePath = new ArrayList<String>();
        if (!StringUtils.isBlank(excludePathv)) {
            String[] paths = excludePathv.split(";");
            excludePath.addAll(Arrays.asList(paths));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final long start = System.currentTimeMillis();

        HttpServletRequest hrequest = (HttpServletRequest) request;
        HttpServletResponse hresponse = (HttpServletResponse) response;
        hresponse.setHeader("Access-Control-Allow-Origin", "*");
        hresponse.setHeader("Access-Control-Allow-Methods", "POST, GET");
        hresponse.setHeader("Access-Control-Max-Age", "3600");
        hresponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

        final String serverAddress = request.getServerName();
        final String funcUrl = hrequest.getRequestURI() + "!" + (request.getParameter("actionMethod") == null ? "" : "" + request.getParameter("actionMethod"));

        String path = hrequest.getServletPath();
        if (PathPatternMatcher.urlPathMatch(excludePath, path)) {
            chain.doFilter(hrequest, hresponse);
            return;
        }

        final String userIp = NetWorkUtils.getIpFromRequest(hrequest);
        final String params = JSONObject.toJSONString(hrequest.getParameterMap());
        log.info("[start request:]||[" + hrequest.getSession().getId() + "]||[" + serverAddress + "]||[" + funcUrl + "]||[" + userIp + "]||["+params+"]");
        try {
            chain.doFilter(request, hresponse);
        } finally {
            long delay = (System.currentTimeMillis() - start);
            log.info("[end request:]||[" + hrequest.getSession().getId() + "]||[" + serverAddress + "]||[" + funcUrl + "]||[" + userIp + "]||["+ delay +"]");
            ThreadLocalResourceHolder.unbindProperty(USER_REQUEST_ID);
        }
    }

    @Override
    public void destroy() {

    }
}
