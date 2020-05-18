/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.db;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author jiche
 */
public class H2DBSaver {

    Connection conn;
    public static String DBDriver = "org.h2.Driver";
    public static String DBConnectURL = "jdbc:h2:./data";
    private boolean isOpened = false;
    public final Lock dbLock = new ReentrantLock();

    public void SetDBPath(String filepath) {
        if (!filepath.endsWith("/")) {
            filepath += "/";
        }
        DBConnectURL = "jdbc:h2:" + filepath + "data";
    }

    /**
     * OPEN h2DB
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void OPEN() throws ClassNotFoundException, SQLException, Exception {
        if (!this.isOpened) {
            /* load db driver */
            Class.forName("org.h2.Driver");
            /* connect string, database name is data.h2db  username: nahon, password: nahong */
            conn = DriverManager.getConnection(DBConnectURL, "nahon", "nahon");
            isOpened = true;
        }
    }

    /**
     * CLOSE connect
     */
    public void CLOSE() throws SQLException {
        if (isOpened) {
            conn.close();
            isOpened = false;
        }
    }

    public boolean IsOpened() {
        return this.isOpened;
    }

    public boolean IsTableExist(String table_name) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, table_name, null)) {
            return rs.next();
        }
    }

    public boolean IsTableEmpty(String table_name) throws SQLException {
        String sql = "select * from " + table_name;
        CallableStatement prepareCall = conn.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        try (ResultSet rs = prepareCall.executeQuery()) {
            return !rs.next();
        }
    }

    public void DropTable(String table_name) throws SQLException {
        String DEL_TABLE = "drop table " + table_name;
        conn.createStatement().executeUpdate(DEL_TABLE);
    }

    public String[] GetAllTables() throws SQLException {
        ArrayList<String> tables = new ArrayList();
        try (ResultSet rs = conn.getMetaData().getTables(null, null, null, new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }

        return tables.toArray(new String[0]);
    }


    //return xx MB Label_DBSize.setText(WQAPlatform.GetInstance().GetDBHelper().GetDBFix().GetDBSize() + "MB");
    public String GetDBSize() {
        File file = new File("./data.h2.db");
        if (file.exists() && file.isFile()) {
//            System.out.println("fline" + file.length());
            return file.length() / 1024 / 1024 + "M";
        }

        return 0 + "M";
    }
}
