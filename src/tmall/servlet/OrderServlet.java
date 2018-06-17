package tmall.servlet;

import tmall.bean.Order;
import tmall.dao.OrderDao;
import tmall.util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public class OrderServlet extends BaseBackServlet {
    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        List<Order> os = orderDao.list(page.getStart(),page.getCount());
        orderItemDao.fill(os);
        int total = orderDao.getTotal();
        page.setTotal(total);

        request.setAttribute("os",os);
        request.setAttribute("page",page);
        return "admin/listOrder.jsp";
    }

    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }

    public String delivery(HttpServletRequest request, HttpServletResponse response, Page page){
        int id = Integer.parseInt(request.getParameter("id"));
        Order o = orderDao.get(id);
        o.setDeliveryDate(new Date());
        o.setStatus(OrderDao.waitConfirm);
        orderDao.update(o);
        return "@admin_order_list";
    }
}
