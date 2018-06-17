package tmall.servlet;

import tmall.bean.Category;
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

public class CategoryServlet extends BaseBackServlet {
    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        List<Category> cs = categoryDao.list(page.getStart(),page.getCount());
        /*if (cs.size()==0){
            Category category = new Category();
            category.setId(1);
            cs.add(category);
        }*/
        int total = categoryDao.getTotal();
        page.setTotal(total);

        request.setAttribute("thecs",cs);
        request.setAttribute("page",page);
        return "admin/listCategory.jsp";
    }

    @Override
    public String add(HttpServletRequest request, HttpServletResponse response,Page page) {
        Map<String ,String> params = new HashMap<>();
        InputStream is = super.parseUpload(request,params);

        Category category = new Category();
        String name = params.get("name");
        category.setName(name);
        categoryDao.add(category);

        File imageFolder = new File(request.getSession().getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,category.getId()+".jpg");

        writePic(is, file);

        return "@admin_category_list";
    }

    private void writePic(InputStream is, File file) {
        try{
            if (null!=is&&0!=is.available()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] b = new byte[1024*10240];
                    int length ;
                    while (-1!=(length=is.read(b))) {
                        fos.write(b,0,length);
                    }
                    fos.flush();

                    //把图片保存为jpg格式
                    BufferedImage img = ImageUtil.change2jpg(file);
                    ImageIO.write(img,"jpg",file);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        categoryDao.delete(id);
        return "@admin_category_list";
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Category category = categoryDao.get(id);
        request.setAttribute("c",category);
        return "admin/editCategory.jsp";
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        Map<String,String> params = new HashMap<>();
        InputStream is = super.parseUpload(request,params);

        String name = params.get("name");
        int id = Integer.parseInt(params.get("id"));

        Category category = new Category();
        category.setName(name);
        category.setId(id);
        categoryDao.update(category);

        File imageFolder = new File(request.getSession().getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,id+".jpg");

        writePic(is, file);
        return "@admin_category_list";
    }
}
