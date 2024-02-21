package server;

import pak.DBUtill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectMySQLFriends {
    String friends = "";
    StringBuilder f = new StringBuilder();

    public String getFriends() {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        try {
            conn = DBUtill.getConnection();

            String sql = "select id from t_user";

            ps = conn.prepareStatement(sql);

            res = ps.executeQuery();

            while (res.next()) {
                f.append("&" + res.getString("id"));
            }
            friends = f.toString();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }
}
