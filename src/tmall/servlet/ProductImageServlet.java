package tmall.servlet;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.dao.ProductImageDao;
import tmall.util.ImageUtil;
import tmall.util.Page;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductImageServlet extends BaseBackServlet {
    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product p = productDao.get(pid);
        List<ProductImage> pisSingle = productImageDao.list(p, ProductImageDao.type_single);
        List<ProductImage> pisDetail = productImageDao.list(p, ProductImageDao.type_detail);

        request.setAttribute("p",p);
        request.setAttribute("pisSingle",pisSingle);
        request.setAttribute("pisDetail",pisDetail);
        return "admin/listProductImage.jsp";
    }

    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        //得到文件输入流
        Map<String,String> params = new HashMap<>();
        InputStream is = super.parseUpload(request,params);

        //得到上传productImage表的pid与type字段
        int pid = Integer.parseInt(params.get("pid"));
        Product p = productDao.get(pid);
        String type = params.get("type");

        //将数据存入表中
        ProductImage pi = new ProductImage();
        pi.setProduct(p);
        pi.setType(type);
        productImageDao.add(pi);

        //创建文件路径及文件名
        String fileName = pi.getId()+".jpg";
        String imageFolder;
        String imageFolder_small = null;
        String imageFolder_middle = null;
        if (ProductImageDao.type_single.equals(type)) {
            imageFolder = request.getSession().getServletContext().getRealPath("img/productSingle");
            imageFolder_small = request.getSession().getServletContext().getRealPath("img/productSingle_small");
            imageFolder_middle = request.getSession().getServletContext().getRealPath("img/productSingle_middle");
        } else
            imageFolder = request.getSession().getServletContext().getRealPath("img/productDetail");

        File f = new File(imageFolder,fileName);
        f.getParentFile().mkdirs();

        //复制文件
        try {
            if (null!=is&&0!=is.available()) {
                try (FileOutputStream fos = new FileOutputStream(f)) {
                    byte[] b = new byte[1024*10240];
                    int length = 0;
                    while (-1!=(length=is.read(b))) {
                        fos.write(b,0,length);
                    }
                    fos.flush();

                    BufferedImage buffimg = ImageUtil.change2jpg(f);
                    ImageIO.write(buffimg,"jpg",f);

                    if (ProductImageDao.type_single.equals(type)) {
                        File f_small = new File(imageFolder_small,fileName);
                        f_small.getParentFile().mkdirs();
                        File f_middle = new File(imageFolder_middle,fileName);
                        f_middle.getParentFile().mkdirs();

                        ImageUtil.resizeImage(f,56,56,f_small);
                        ImageUtil.resizeImage(f,217,190,f_middle);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "@admin_productImage_list?pid="+pid;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        ProductImage pi = productImageDao.get(id);
        productImageDao.delete(id);

        if (ProductImageDao.type_single.equals(pi.getType())) {
            String imageFolder_single = request.getSession().getServletContext().getRealPath("img/productSingle");
            String imageFolder_small = request.getSession().getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle = request.getSession().getServletContext().getRealPath("img/productSingle_middle");

            File f_single = new File(imageFolder_single,pi.getId()+".jpg");
            f_single.delete();
            File f_small = new File(imageFolder_small,pi.getId()+".jpg");
            f_small.delete();
            File f_middle = new File(imageFolder_middle,pi.getId()+".jpg");
            f_middle.delete();
        } else {
            String imageFolder_detail = request.getSession().getServletContext().getRealPath("img/productDetail");
            File f_detail = new File(imageFolder_detail,pi.getId()+".jpg");
            f_detail.delete();
        }
        return "@admin_productImage_list?pid="+pi.getProduct().getId();
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }
}
