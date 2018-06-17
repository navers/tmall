package tmall.servlet;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.web.util.HtmlUtils;
import tmall.bean.*;
import tmall.comparator.*;
import tmall.dao.CategoryDao;
import tmall.dao.OrderDao;
import tmall.dao.ProductDao;
import tmall.dao.ProductImageDao;
import tmall.util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ForeServlet extends BaseForeServlet {

    public String home(HttpServletRequest request, HttpServletResponse response, Page page){
        List<Category> cs = new CategoryDao().list();
        new ProductDao().fill(cs);
        new ProductDao().fillByRow(cs);
        request.setAttribute("cs",cs);
        return "home.jsp";
    }

    public String register(HttpServletRequest request, HttpServletResponse response, Page page){
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        System.out.println(name);
        name = HtmlUtils.htmlEscape(name);
        System.out.println(name);
        boolean exist = userDao.isExist(name);

        if (exist) {
            request.setAttribute("msg", "用户名已经被使用,不能使用");
            return "register.jsp";
        }

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        userDao.add(user);
        return "@registerSuccess.jsp";
    }

    public String login(HttpServletRequest request, HttpServletResponse response, Page page){
        String name = request.getParameter("name");
        name = HtmlUtils.htmlEscape(name);
        String password = request.getParameter("password");

        User user = userDao.get(name,password);

        if (null==user) {
            request.setAttribute("msg", "账号密码错误");
            return "login.jsp";
        }
        request.getSession().setAttribute("user",user);
        return "@forehome";
    }

    public String logout(HttpServletRequest request, HttpServletResponse response, Page page){
        request.getSession().removeAttribute("user");
        return "@forehome";
    }

    public String product(HttpServletRequest request, HttpServletResponse response, Page page){
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDao.get(pid);

        List<ProductImage> productSingleImages = productImageDao.list(p,ProductImageDao.type_single);
        List<ProductImage> productDetailImages = productImageDao.list(p,ProductImageDao.type_detail);
        p.setProductSingleImages(productSingleImages);
        p.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueDao.list(pid);
        List<Review> reviews = reviewDao.list(pid);

        productDao.setSaleAndReviewNumber(p);

        request.setAttribute("p",p);
        request.setAttribute("pvs",pvs);
        request.setAttribute("reviews",reviews);
        return "product.jsp";
    }

    public String checkLogin(HttpServletRequest request, HttpServletResponse response, Page page){
        User user = (User) request.getSession().getAttribute("user");
        if (null!=user)
            return "%success";
        return "%fail";
    }

    public String loginAjax(HttpServletRequest request, HttpServletResponse response, Page page){
        String name = request.getParameter("name");
        name = HtmlUtils.htmlEscape(name);
        String password = request.getParameter("password");

        User user = userDao.get(name,password);
        if (null==user) {
            request.setAttribute("msg","账号密码错误");
            return "%fail";
        }
        request.getSession().setAttribute("user",user);
        return "%success";
    }

    public String category(HttpServletRequest request, HttpServletResponse response, Page page){
        int cid = Integer.parseInt(request.getParameter("cid"));

        Category c = new CategoryDao().get(cid);
        new ProductDao().fill(c);
        new ProductDao().setSaleAndReviewNumber(c.getProducts());

        String sort = request.getParameter("sort");
        if (null!=sort) {
            switch (sort) {
                case "review" :Collections.sort(c.getProducts(),new ProductReviewComparator());break;
                case "date" :Collections.sort(c.getProducts(),new ProductDateComparator());break;
                case "saleCount" :Collections.sort(c.getProducts(),new ProductSaleCountComparator());break;
                case "price" :Collections.sort(c.getProducts(),new ProductPriceComparator());break;
                case "all" :Collections.sort(c.getProducts(),new ProductAllComparator());break;
            }
        }
        request.setAttribute("c",c);
        return "category.jsp";
    }

    public String search(HttpServletRequest request, HttpServletResponse response, Page page){
        String keyword = request.getParameter("keyword");
        List<Product> ps = productDao.search(keyword,0,20);
        productDao.setSaleAndReviewNumber(ps);
        request.setAttribute("ps",ps);
        return "searchResult.jsp";
    }

    public String buyone(HttpServletRequest request, HttpServletResponse response, Page page){
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        Product p = productDao.get(pid);
        int oiid = 0;

        User user = (User) request.getSession().getAttribute("user");
        List<OrderItem> ois = orderItemDao.listByUser(user.getId());
        boolean found = false;
        for (OrderItem oi:ois) {
            if (oi.getProduct().getId()==pid) {
                oi.setNumber(oi.getNumber()+num);
                orderItemDao.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }

        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(p);
            orderItemDao.add(oi);
            oiid = oi.getId();
        }
        return "@forebuy?oiid="+oiid;
    }

    public String buy(HttpServletRequest request, HttpServletResponse response, Page page){
        String[] oiids = request.getParameterValues("oiid");
        //String k = request.getParameter("oiid");
        List<OrderItem> ois = new ArrayList<>();
        float total = 0;
        //System.out.println(oiids);
        //System.out.println(k);

        for (String strid:oiids) {
            int oiid = Integer.parseInt(strid);
            OrderItem oi = orderItemDao.get(oiid);
            total += oi.getNumber()*oi.getProduct().getPromotePrice();
            ois.add(oi);
        }

        request.getSession().setAttribute("ois",ois);
        request.setAttribute("total",total);
        return "buy.jsp";
    }

    public String addCart(HttpServletRequest request, HttpServletResponse response, Page page){
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        Product p = productDao.get(pid);

        User user = (User) request.getSession().getAttribute("user");
        List<OrderItem> ois = orderItemDao.listByUser(user.getId());
        boolean found = false;

        for (OrderItem oi : ois) {
            if (oi.getId()==pid) {
                oi.setNumber(oi.getNumber()+num);
                orderItemDao.update(oi);
                found = true;
                break;
            }
        }

        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setNumber(num);
            oi.setProduct(p);
            oi.setUser(user);
            orderItemDao.update(oi);
        }
        return "%success";
    }

    public String cart(HttpServletRequest request, HttpServletResponse response, Page page){
        User user = (User) request.getSession().getAttribute("user");
        List<OrderItem> ois = orderItemDao.listByUser(user.getId());
        request.setAttribute("ois",ois);
        return "cart.jsp";
    }

    public String changeOrderItem(HttpServletRequest request, HttpServletResponse response, Page page){
        User user = (User) request.getSession().getAttribute("user");
        if (null==user) {
            return "%fail";
        }
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("number"));

        List<OrderItem> ois = orderItemDao.listByUser(user.getId());
        for (OrderItem oi:ois) {
            if (oi.getProduct().getId()==pid) {
                oi.setNumber(num);
                orderItemDao.update(oi);
                break;
            }
        }
        return "%success";
    }

    public String deleteOrderItem(HttpServletRequest request, HttpServletResponse response, Page page){
        User user = (User) request.getSession().getAttribute("user");
        if (null==user) {
            return "%fail";
        }
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        orderItemDao.delete(oiid);
        return "%success";
    }

    public String createOrder(HttpServletRequest request, HttpServletResponse response, Page page){
        User user = (User) request.getSession().getAttribute("user");

        List<OrderItem> ois = (List<OrderItem>) request.getSession().getAttribute("ois");
        if (ois.isEmpty()) {
            return "login.jsp";
        }

        String address = request.getParameter("address");
        String post = request.getParameter("post");
        String receiver = request.getParameter("receiver");
        String mobile = request.getParameter("mobile");
        String userMessage = request.getParameter("userMessage");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
                +RandomUtils.nextInt(10000);

        Order order = new Order();
        order.setAddress(address);
        order.setPost(post);
        order.setReceiver(receiver);
        order.setMobile(mobile);
        order.setUserMessage(userMessage);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderDao.waitPay);
        orderDao.add(order);

        float total = 0;
        for (OrderItem oi : ois) {
            oi.setOrder(order);
            orderItemDao.update(oi);
            total = oi.getProduct().getPromotePrice()*oi.getNumber();
        }
        return "@forealipay?oid="+order.getId()+"&total="+total;
    }

    public String alipay(HttpServletRequest request, HttpServletResponse response, Page page){
        return "alipay.jsp";
    }

    public String payed(HttpServletRequest request, HttpServletResponse response, Page page){
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order order = orderDao.get(oid);
        order.setStatus(OrderDao.waitDelivery);
        order.setPayDate(new Date());
        new OrderDao().update(order);
        request.setAttribute("o",order);
        return "payed.jsp";
    }

    public String bought(HttpServletRequest request, HttpServletResponse response, Page page){
        User user = (User) request.getSession().getAttribute("user");
        List<Order> os = orderDao.list(user.getId(),OrderDao.delete);

        orderItemDao.fill(os);
        request.setAttribute("os",os);
        return "bought.jsp";
    }

    public String confirmPay(HttpServletRequest request, HttpServletResponse response, Page page){
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDao.get(oid);
        orderItemDao.fill(o);
        request.setAttribute("o",o);
        return "confirmPay.jsp";
    }

    public String orderConfirmed(HttpServletRequest request, HttpServletResponse response, Page page){
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDao.get(oid);
        o.setStatus(OrderDao.waitReview);
        o.setConfirmDate(new Date());
        orderDao.update(o);
        return "orderConfirmed.jsp";
    }

    public String deleteOrder(HttpServletRequest request, HttpServletResponse response, Page page){
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDao.get(oid);
        o.setStatus(OrderDao.delete);
        orderDao.update(o);
        return "%success";
    }

    public String review(HttpServletRequest request, HttpServletResponse response, Page page){
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDao.get(oid);
        orderItemDao.fill(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewDao.list(p.getId());
        productDao.setSaleAndReviewNumber(p);
        request.setAttribute("p",p);
        request.setAttribute("o",o);
        request.setAttribute("reviews",reviews);
        return "review.jsp";
    }

    public String doreview(HttpServletRequest request, HttpServletResponse response, Page page){
        int oid = Integer.parseInt(request.getParameter("oid"));
        Order o = orderDao.get(oid);
        o.setStatus(OrderDao.finish);
        orderDao.update(o);
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDao.get(pid);

        String content = request.getParameter("content");

        content = HtmlUtils.htmlEscape(content);

        User user = (User) request.getSession().getAttribute("user");
        Review review = new Review();
        review.setCreateDate(new Date());
        review.setProduct(p);
        review.setUser(user);
        review.setContent(content);
        reviewDao.update(review);
        return "@forereview?oid="+oid+"&showonly=true";
    }

}
