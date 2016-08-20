package com.jsifuentes.core.database;

import com.jsifuentes.core.Configuration;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Jacob on 12/27/2014.
 */
public class Database {
    protected static java.sql.Connection Connection;

    public static java.sql.Connection getConnection() throws SQLException {
        if(Connection != null && !Connection.isClosed()) {
            return Connection;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch(ClassNotFoundException exc) {
            throw new SQLException("com.mysql.jdbc.Driver exception: " + exc.getMessage());
        }

        Configuration config = new Configuration();

        Map databaseConfig = (Map)config.get("database");

        if(!databaseConfig.containsKey("host") || !databaseConfig.containsKey("user") || !databaseConfig.containsKey("pass") || !databaseConfig.containsKey("database")) {
            throw new SQLException("Unable to create connection - database object is not complete. host, user, pass, database required!");
        } else {
            String host = (String)databaseConfig.get("host");
            String user = (String)databaseConfig.get("user");
            String pass = (String)databaseConfig.get("pass");
            String database = (String)databaseConfig.get("database");
            Integer port = 3306;

            if(databaseConfig.containsKey("port")) {
                port = Integer.parseInt((String)databaseConfig.get("port"));
            }

            if(databaseConfig.get("type").equals("mysql")) {
                Connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, pass);
            } else {
                throw new SQLException("Unable to create connection - database type is not supported.");
            }

            return Connection;
        }
    }
}
