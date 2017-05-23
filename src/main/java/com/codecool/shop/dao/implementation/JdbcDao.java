package com.codecool.shop.dao.implementation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by marti on 2017.05.15..
 */


public abstract class JdbcDao {

    /**
     * Generates connection with database.
     */
    abstract Connection getConnection() throws SQLException;

    void executeQuery(String query) {
        try (Connection connection = getConnection();
             Statement statement =connection.createStatement();
        ){
            statement.execute(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}