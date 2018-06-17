package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDao {

    public int getTotal(int cid){
        int total = 0;
        try(Connection c = DBUtil.getConnection();
            Statement s = c.createStatement()){

            String sql = "select count(*) from property where cid="+cid;
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()){
                total = rs.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return total;
    }

    public void add(Property bean){
        String sql = "insert into property values(null,?,?)";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){

            ps.setInt(1,bean.getCategory().getId());
            ps.setString(2,bean.getName());

            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                bean.setId(rs.getInt(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void update(Property bean){
        String sql = "update property set cid=?,name=? where id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1,bean.getCategory().getId());
            ps.setString(2,bean.getName());
            ps.setInt(3,bean.getId());

            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(int id){
        try(Connection c = DBUtil.getConnection();
            Statement s = c.createStatement()){

            String sql = "delete from property where id="+id;
            s.execute(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Property get(int id){
        Property bean = null;
        try(Connection c = DBUtil.getConnection();
            Statement s = c.createStatement()){

            String sql = "select * from property where id="+id;
            ResultSet rs = s.executeQuery(sql);

            if(rs.next()){
                bean = new Property();
                bean.setId(id);
                Category category = new CategoryDao().get(rs.getInt("cid"));
                bean.setCategory(category);
                bean.setName(rs.getString("name"));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }

    public List<Property> list(int cid){
        return list(cid,0,Short.MAX_VALUE);
    }

    public List<Property> list(int cid,int start,int count){
        List<Property> beans = new ArrayList<>();
        String sql = "select * from property where cid=? order by id desc limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1,cid);
            ps.setInt(2,start);
            ps.setInt(3,count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Property bean = new Property();
                Category category = new CategoryDao().get(cid);
                bean.setId(rs.getInt("id"));
                bean.setName(rs.getString("name"));
                bean.setCategory(category);
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }

}
