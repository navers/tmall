package tmall.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import tmall.dao.*;
import tmall.util.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@WebServlet(name = "BaseBackServlet")
public  abstract class BaseBackServlet extends HttpServlet {

    public abstract String list(HttpServletRequest request,HttpServletResponse response,Page page);
    public abstract String add(HttpServletRequest request,HttpServletResponse response,Page page);
    public abstract String delete(HttpServletRequest request,HttpServletResponse response,Page page);
    public abstract String edit(HttpServletRequest request,HttpServletResponse response,Page page);
    public abstract String update(HttpServletRequest request,HttpServletResponse response,Page page);

    protected CategoryDao categoryDao = new CategoryDao();
    protected PropertyDao propertyDao = new PropertyDao();
    protected ProductDao productDao = new ProductDao();
    protected ProductImageDao productImageDao = new ProductImageDao();
    protected PropertyValueDao propertyValueDao = new PropertyValueDao();
    protected UserDao userDao = new UserDao();
    protected OrderItemDao orderItemDao = new OrderItemDao();
    protected OrderDao orderDao = new OrderDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            /*获取分页信息*/
            int start = 0;
            int count = 10;
            try{
                start = Integer.parseInt(request.getParameter("page.start"));
                count = Integer.parseInt(request.getParameter("page.count"));
            }catch (NumberFormatException e){
            }

            Page page = new Page(start,count);

            /*借助反射调用对应的方法*/
            String method = (String) request.getAttribute("method");
            Method m = this.getClass().getMethod(method,HttpServletRequest.class,
                    HttpServletResponse.class,Page.class);
            String redirect = m.invoke(this,request,response,page).toString();
            //System.out.println(method);
            //System.out.println("list".equals(method));

            /*根据方法的返回值，进行相应的客户端跳转，服务端跳转，或者仅仅是输出字符串*/
            if (redirect.startsWith("@")) {
                if ("list".equals(method))
                    setNoCache(response);
                response.sendRedirect(redirect.substring(1));
            }
            else if (redirect.startsWith("%"))
                response.getWriter().print(redirect.substring(1));
            else
                request.getRequestDispatcher(redirect).forward(request,response);
        }catch (Exception e){
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

    public InputStream parseUpload(HttpServletRequest request,Map<String ,String> params){
        InputStream is = null;
        try{
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //设置上传文件大小为10M
            factory.setSizeThreshold(1024*10240);
            ServletFileUpload upload = new ServletFileUpload(factory);

            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();
            while (iter.hasNext()){
                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()){
                    //获取上传文件的输入流
                    is = item.getInputStream();
                } else {
                    String paramName = item.getFieldName();
                    String paramValue = item.getString();
                    paramValue = new String(paramValue.getBytes("ISO-8859-1"),"UTF-8");
                    params.put(paramName,paramValue);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return is;
    }

}
