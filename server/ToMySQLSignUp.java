package server;

import pak.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ToMySQLSignUp {
    private boolean result = false;
    long id;

    public boolean toMysqlSignUp(UserSignUp u) throws FileNotFoundException
    {

        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        try {
            //get connection with sql
            conn = DBUtill.getConnection();

            File avatarFile = new File(u.getAvatar());
            FileInputStream fis=new FileInputStream(avatarFile);
            
            String sql = "insert into t_user (password,nickName,avatar,type,phone) values (?,?,?,?,?)";
            //Get the precompiled database manipulation object
            ps = conn.prepareStatement(sql);
            //Assign a value to the placeholder(?,?,?)
            //ps.setLong(1,id);
            ps.setString(1,u.getPassword());
            ps.setString(2,u.getName());
            ps.setBinaryStream(3, fis, (int) avatarFile.length());
            ps.setString(4,"user");
            ps.setString(5,u.getPhone());

            //execute the SQL statement
            int i = ps.executeUpdate();
            if (i==1){
                result = true;
                String getid="select id from t_user where nickName='"+u.getName()+"' and password='"+u.getPassword()+"'";
                rs=ps.executeQuery(getid);
                if(rs.next())
                {
                    u.setID(rs.getInt("id"));
                }
            }
            fis.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            //close the resources
            DBUtill.close(conn, ps, rs);
        }
        return result;
    }
}
