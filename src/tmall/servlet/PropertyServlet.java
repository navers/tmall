package tmall.servlet;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class PropertyServlet extends BaseBackServlet {
    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDao.get(cid);
        List<Property> ps = propertyDao.list(cid,page.getStart(),page.getCount());
        int total = propertyDao.getTotal(cid);
        page.setTotal(total);
        page.setParam("&cid="+cid);

        request.setAttribute("ps",ps);
        request.setAttribute("c",c);
        request.setAttribute("page",page);

        return "admin/listProperty.jsp";
    }

    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDao.get(cid);
        String name = request.getParameter("name");
        Property p = new Property();
        p.setName(name);
        p.setCategory(c);
        propertyDao.add(p);

        return "@admin_property_list?cid="+cid;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Property p = propertyDao.get(id);
        propertyDao.delete(id);
        return "@admin_property_list?cid="+p.getCategory().getId();
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Property p = propertyDao.get(id);
        request.setAttribute("p",p);
        return "admin/editProperty.jsp";
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDao.get(cid);

        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        Property p = new Property();
        p.setId(id);
        p.setName(name);
        p.setCategory(c);
        propertyDao.update(p);
        return "@admin_property_list?cid="+cid;
    }
}
