package client;

import pak.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.fastjson.JSONObject;

public class SearchInMySQL {
    String id;
    String name;
    public static setting settingLocal=new setting();
    User user;

    public SearchInMySQL(String id) {
        this.id = id;
    }

    public SearchInMySQL() {
    }

    public static setting setSetting() throws IOException
    {
        File setFile=new File("setting.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(setFile)));
        String jsonStr=reader.readLine();
        reader.close();
        return JSONObject.parseObject(jsonStr, setting.class);
    }

    public String getIDbyName(String n) 
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        
        try {
            //get connection to the sql
            conn = DBUtill.getConnection();
            String sql = "select * from t_user where nickName = ?";
            ps = conn.prepareStatement(sql);
            //Assign a value to the placeholder
            ps.setString(1, n);

            //Execute SQL statements
            res = ps.executeQuery();

            if (res.next()) {
                id=res.getString("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            //close the resources
            DBUtill.close(conn, ps, res);
        }
        return id;
    }

    public String getNamebyID(String i) 
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        
        try {
            //get connection to the sql
            conn = DBUtill.getConnection();
            String sql = "select * from t_user where id = ?";
            ps = conn.prepareStatement(sql);
            //Assign a value to the placeholder
            ps.setString(1, i);

            //Execute SQL statements
            res = ps.executeQuery();

            if (res.next()) {
                name=res.getString("nickName");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            //close the resources
            DBUtill.close(conn, ps, res);
        }
        return name;
    }
}
