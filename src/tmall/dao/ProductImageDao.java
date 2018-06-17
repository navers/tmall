package tmall.dao;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductImageDao {

    public static final String type_single = "type_single";
    public static final String type_detail = "type_detail";

    public int getTotal(){
        int total = 0;
        try(Connection c= DBUtil.getConnection();
            Statement s= c.createStatement()){

            String sql = "select count(*) from productimage";
            ResultSet rs = s.executeQuery(sql);

            if (rs.next()){
                total = rs.getInt(1);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return total;
    }

    public void add(ProductImage bean){
        String sql = "insert into productimage values(null,?,?)";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){

            ps.setInt(1,bean.getProduct().getId());
            ps.setString(2,bean.getType());
            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next()){
                bean.setId(rs.getInt(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void update(ProductImage bean){
        String sql = "update productimage set pid=?,type=? where id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1,bean.getProduct().getId());
            ps.setString(2,bean.getType());
            ps.setInt(3,bean.getId());

            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(int id){
        try(Connection c= DBUtil.getConnection();
            Statement s= c.createStatement()){

            String sql = "delete from productimage where id="+id;
            s.execute(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ProductImage get(int id){
        ProductImage bean = null;
        try(Connection c= DBUtil.getConnection();
            Statement s= c.createStatement()){

            String sql = "select * from productimage where id="+id;
            ResultSet rs = s.executeQuery(sql);

            if(rs.next()){
                bean = new ProductImage();
                bean.setId(id);
                Product product = new ProductDao().get(rs.getInt("pid"));
                bean.setProduct(product);
                bean.setType(rs.getString("type"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }

    public List<ProductImage> list(Product product,String type){
        return list(product,type,0,Short.MAX_VALUE);
    }

    public List<ProductImage> list(Product product, String type, int start,int count){
        List<ProductImage> beans = new ArrayList<>();
        String sql = "select * from productimage where pid=? and type=? order by id desc limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1,product.getId());
            ps.setString(2,type);
            ps.setInt(3,start);
            ps.setInt(4,count);

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ProductImage productImage = new ProductImage();
                productImage.setId(rs.getInt("id"));
                productImage.setType(type);
                productImage.setProduct(product);
                beans.add(productImage);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }
}
