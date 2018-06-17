package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductDao {

    public int getTotal(int cid){
        int total = 0;
        try(Connection c = DBUtil.getConnection();
            Statement s = c.createStatement()){

            String sql = "select count(*) from product where cid = "+cid;
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()){
                total = rs.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return total;
    }

    public void add(Product bean){
        String sql = "insert into product values(null,?,?,?,?,?,?,?)";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){

            ps.setString(1,bean.getName());
            ps.setString(2,bean.getSubTitle());
            ps.setFloat(3,bean.getOriginalPrice());
            ps.setFloat(4,bean.getPromotePrice());
            ps.setInt(5,bean.getStock());
            ps.setInt(6,bean.getCategory().getId());
            ps.setTimestamp(7,DateUtil.d2t(bean.getCreateDate()));
            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                bean.setId(rs.getInt(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void update(Product bean){
        String sql = "update product set name= ?, subTitle=?, " +
                "originalPrice=?,promotePrice=?,stock=?, cid = ?, createDate=? where id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setString(1,bean.getName());
            ps.setString(2,bean.getSubTitle());
            ps.setFloat(3,bean.getOriginalPrice());
            ps.setFloat(4,bean.getPromotePrice());
            ps.setInt(5,bean.getStock());
            ps.setInt(6,bean.getCategory().getId());
            ps.setTimestamp(7,DateUtil.d2t(bean.getCreateDate()));
            ps.setInt(8,bean.getId());
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(int id){
        try(Connection c = DBUtil.getConnection();
            Statement s = c.createStatement()){

            String sql = "delete from product where id ="+id;
            s.execute(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Product get(int id){
        Product bean = null;
        try(Connection c = DBUtil.getConnection();
            Statement s = c.createStatement()){

            String sql = "select * from product where id = "+id;
            ResultSet rs = s.executeQuery(sql);
            if(rs.next()){
                bean = new Product();
                Category category = new CategoryDao().get(rs.getInt("cid"));
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));

                bean.setId(id);
                bean.setName(rs.getString("name"));
                bean.setSubTitle(rs.getString("subTitle"));
                bean.setOriginalPrice(rs.getFloat("originalPrice"));
                bean.setPromotePrice(rs.getFloat("promotePrice"));
                bean.setStock(rs.getInt("stock"));
                bean.setCategory(category);
                bean.setCreateDate(createDate);
                this.setFirstProductImage(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }

    public void setFirstProductImage(Product p){
        List<ProductImage> pis = new ProductImageDao().list(p,ProductImageDao.type_single);
        if(!pis.isEmpty()){
            p.setFirstProductImage(pis.get(0));
        }
    }

    public List<Product> list(int cid){
        return list(cid,0,  Short.MAX_VALUE);
    }

    public List<Product> list(int cid,int start,int count){
        List<Product> beans = new ArrayList<>();
        String sql = "select * from product where cid = ? order by id desc limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1,cid);
            ps.setInt(2,start);
            ps.setInt(3,count);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Product bean = new Product();
                Category category = new CategoryDao().get(cid);
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));

                bean.setId(rs.getInt("id"));
                bean.setName(rs.getString("name"));
                bean.setSubTitle(rs.getString("subTitle"));
                bean.setOriginalPrice(rs.getFloat("originalPrice"));
                bean.setPromotePrice(rs.getFloat("promotePrice"));
                bean.setStock(rs.getInt("stock"));
                bean.setCategory(category);
                bean.setCreateDate(createDate);
                this.setFirstProductImage(bean);

                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }

    public List<Product> list(){
        return list(0,Short.MAX_VALUE);
    }

    public List<Product> list(int start ,int count){
        List<Product> beans = new ArrayList<>();
        String sql = "select * from product limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1,start);
            ps.setInt(2,count);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Product bean = new Product();
                Category category = new CategoryDao().get(rs.getInt("cid"));
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));

                bean.setId(rs.getInt("id"));
                bean.setName(rs.getString("name"));
                bean.setSubTitle(rs.getString("subTitle"));
                bean.setOriginalPrice(rs.getFloat("originalPrice"));
                bean.setPromotePrice(rs.getFloat("promotePrice"));
                bean.setStock(rs.getInt("stock"));
                bean.setCategory(category);
                bean.setCreateDate(createDate);

                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }

    public void fill(List<Category> cs){
        for(Category c: cs){
            fill(c);
        }
    }

    public void fill(Category c){
        List<Product> ps = this.list(c.getId());
        if(!ps.isEmpty()){
            c.setProducts(ps);
        }
    }

    public void fillByRow(List<Category> cs){
        int productNumberEachRow = 8;
        for(Category c:cs){
            List<Product> products = new ProductDao().list(c.getId());
            List<List<Product>> productByRows = new ArrayList<>();
            for(int i=0;i<products.size();i += productNumberEachRow){
                int size = i+productNumberEachRow;
                size = size>products.size()?products.size():size;
                List<Product> productByRow = products.subList(i,size);
                productByRows.add(productByRow);
            }
            c.setProductsByRow(productByRows);
        }
    }

    public void setSaleAndReviewNumber(Product p){
        int saleCount = new OrderItemDao().getSaleCount(p.getId());
        p.setSaleCount(saleCount);

        int reviewCount = new ReviewDao().getCount(p.getId());
        p.setReviewCount(reviewCount);
    }

    public void setSaleAndReviewNumber(List<Product> ps){
        for(Product p:ps){
            setSaleAndReviewNumber(p);
        }
    }

    public List<Product> search(String keyword,int start,int count){
        List<Product> beans = new ArrayList<>();

        if(null==keyword||0==keyword.trim().length()){
            return beans;
        }

        String sql = "select * from product where name like ? limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setString(1,"%"+keyword.trim()+"%");
            ps.setInt(2,start);
            ps.setInt(3,count);

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Product bean = new Product();
                Category category = new CategoryDao().get(rs.getInt("cid"));
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));

                bean.setId(rs.getInt("id"));
                bean.setName(rs.getString("name"));
                bean.setSubTitle(rs.getString("subTitle"));
                bean.setOriginalPrice(rs.getFloat("originalPrice"));
                bean.setPromotePrice(rs.getFloat("promotePrice"));
                bean.setStock(rs.getInt("stock"));
                bean.setCategory(category);
                bean.setCreateDate(createDate);
                this.setFirstProductImage(bean);

                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }
}
