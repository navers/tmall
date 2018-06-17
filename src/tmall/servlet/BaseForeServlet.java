package tmall.servlet;

import tmall.dao.*;
import tmall.util.Page;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class BaseForeServlet extends HttpServlet {

    protected CategoryDao categoryDao = new CategoryDao();
    protected OrderDao orderDao = new OrderDao();
    protected OrderItemDao orderItemDao = new OrderItemDao();
    protected ProductDao productDao = new ProductDao();
    protected ProductImageDao productImageDao = new ProductImageDao();
    protected PropertyDao propertyDao = new PropertyDao();
    protected PropertyValueDao propertyValueDao = new PropertyValueDao();
    protected ReviewDao reviewDao = new ReviewDao();
    protected UserDao userDao = new UserDao();

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response){
        try {
            int start = 0;
            int count = 5;
            try {
                start = Integer.parseInt(request.getParameter("page.start"));
                count = Integer.parseInt(request.getParameter("page.count"));
            } catch (NumberFormatException e) {
            }

            Page page = new Page(start,count);

            String method = request.getAttribute("method").toString();

            Method m = this.getClass().getMethod(method,HttpServletRequest.class,
                    HttpServletResponse.class,Page.class);
            String redirect = m.invoke(this,request,response,page).toString();

            if (redirect.startsWith("@")) {
                setNoCache(response);
                response.sendRedirect(redirect.substring(1));
            } else if (redirect.startsWith("%"))
                response.getWriter().print(redirect.substring(1));
            else
                request.getRequestDispatcher(redirect).forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void setNoCache(HttpServletResponse response){
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Cache-Control","no-store");
        response.setHeader("Pragma","no-cache");
        response.setHeader("Expires","0");
    }

}
