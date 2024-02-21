package server;

import pak.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.jdbc.Blob;

public class ToMySQLLogIn {
    String id;
    String pwd;
    public static setting settingLocal=new setting();
    account user;

    public ToMySQLLogIn(String id, String pwd) {
        this.id = id;
        this.pwd = pwd;
    }

    public static setting setSetting() throws IOException
    {
        File setFile=new File("setting.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(setFile)));
        String jsonStr=reader.readLine();
        reader.close();
        return JSONObject.parseObject(jsonStr, setting.class);
    }

    public boolean ifright() {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        byte[] buffer = new byte[4096];
    	FileOutputStream outputImage = null;
    	InputStream is = null;
        
        try {
            //get connection to the sql
            conn = DBUtill.getConnection();
            String sql = "select * from t_user where id = ? and password = ?";
            ps = conn.prepareStatement(sql);
            //Assign a value to the placeholder
            ps.setInt(1, Integer.parseInt(id));
            ps.setString(2, pwd);

            //Execute SQL statements
            res = ps.executeQuery();

            if (res.next()) {
                check = true;
                settingLocal=setSetting();
                File avatarPath=new File(settingLocal.dataPath+id);
                if(!avatarPath.exists())
                {
                    avatarPath.mkdirs();
                }
                File avatar = new File(settingLocal.dataPath+id+"/avatar.png");
                if (!avatar.exists()) {
                    avatar.createNewFile();   	    
                }
                outputImage = new FileOutputStream(avatar);
                Blob blob = (Blob) res.getBlob("avatar");
    	        is = blob.getBinaryStream();
                int size = 0; 	   
    	        while ((size = is.read(buffer)) != -1) {  	     
    	    	    outputImage.write(buffer, 0, size);   	     
    	        }
                String name=res.getString("nickName");
                user=new account(Integer.parseInt(id),pwd,name);
            }
            outputImage.close();
            is.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            //close the resources
            DBUtill.close(conn, ps, res);
        }
        return check;
    }

    public account getAccount()
    {
        return user;
    }
}
