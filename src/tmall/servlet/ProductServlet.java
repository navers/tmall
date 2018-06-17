package tmall.servlet;

import tmall.bean.Category;
import tmall.bean.Product;
import tmall.bean.PropertyValue;
import tmall.util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ProductServlet extends BaseBackServlet {
    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDao.get(cid);

        List<Product> ps = productDao.list(cid,page.getStart(),page.getCount());
        int total = productDao.getTotal(cid);
        page.setTotal(total);
        page.setParam("&cid="+cid);

        request.setAttribute("ps",ps);
        request.setAttribute("c",c);
        request.setAttribute("page",page);
        return "admin/listProduct.jsp";
    }

    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDao.get(cid);
        String name = request.getParameter("name");
        String subTitle = request.getParameter("subTitle");
        float originalPrice = Float.parseFloat(request.getParameter("originalPrice"));
        float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
        int stock = Integer.parseInt(request.getParameter("stock"));

        Product p = new Product();
        p.setName(name);
        p.setSubTitle(subTitle);
        p.setOriginalPrice(originalPrice);
        p.setPromotePrice(promotePrice);
        p.setStock(stock);
        p.setCategory(c);
        productDao.add(p);

        return "@admin_product_list?cid="+cid;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Product p = productDao.get(id);
        productDao.delete(id);
        return "@admin_product_list?cid="+p.getCategory().getId();
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Product p = productDao.get(id);
        request.setAttribute("p",p);
        return "admin/editProduct.jsp";
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDao.get(cid);
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String subTitle = request.getParameter("subTitle");
        Float originalPrice = Float.parseFloat(request.getParameter("originalPrice"));
        Float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
        int stock = Integer.parseInt(request.getParameter("stock"));

        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setSubTitle(subTitle);
        p.setOriginalPrice(originalPrice);
        p.setPromotePrice(promotePrice);
        p.setStock(stock);
        p.setCategory(c);

        productDao.update(p);
        return "@admin_product_list?cid="+cid;
    }

    public String editPropertyValue(HttpServletRequest request, HttpServletResponse response, Page page){
        int id = Integer.parseInt(request.getParameter("id"));
        Product p = productDao.get(id);
        propertyValueDao.init(p);

        List<PropertyValue> pvs = propertyValueDao.list(id);
        request.setAttribute("p",p);
        request.setAttribute("pvs",pvs);
        return "admin/editPropertyValue.jsp";
    }

    public String updatePropertyValue(HttpServletRequest request, HttpServletResponse response, Page page){
        int pvid = Integer.parseInt(request.getParameter("pvid"));
        String value = request.getParameter("value");

        PropertyValue pv = propertyValueDao.get(pvid);
        pv.setValue(value);
        propertyValueDao.update(pv);
        return "%success";
    }
}
