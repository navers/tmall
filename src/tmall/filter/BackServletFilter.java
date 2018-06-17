package tmall.filter;

import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BackServletFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String contentPath = request.getServletContext().getContextPath();
        String uri = request.getRequestURI();
        uri = StringUtils.remove(uri,contentPath);
        //System.out.println(uri);
        //System.out.println(contentPath);

        if(uri.startsWith("/admin_")){
            String servletPath = StringUtils.substringBetween(uri,"_","_")+"Servlet";
            String method = StringUtils.substringAfterLast(uri,"_");
            request.setAttribute("method",method);
            request.getRequestDispatcher("/"+servletPath).forward(request,response);
            return;
        }
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
