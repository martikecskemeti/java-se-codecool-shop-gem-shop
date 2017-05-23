package com.codecool.shop.dao.implementation;

import com.codecool.shop.controller.DatabaseConnectionData;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Database implementation of {@link SupplierDao}.
 */
public class SupplierDaoJdbc extends JdbcDao implements SupplierDao {

    /**
     * Adds new supplier
     * @param supplier to add.
     */

    @Override
    public void add(Supplier supplier) {
        try {
            String query = "INSERT INTO suppliers (supplier_name, supplier_description) VALUES(?,?);";
            Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getDescription());
            executeQuery(stmt.toString());
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Supplier could not be added to the database.");
        }

    }

    /**
     * finds supplier based on id.
     * @param id of supplier to find.
     * @return supplier
     */

    @Override
    public Supplier find(int id)  {
        String query = "SELECT * FROM suppliers WHERE id = ?;";

        try {
        Connection connection = getConnection();
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);
        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()){
        Supplier supplier = new Supplier(resultSet.getString("supplier_name"),
                resultSet.getString("supplier_description"));
                supplier.setId(id);
        return supplier;
        }
        connection.close();
        return null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    /**
     * Removes supplier based on id.
     * @param id of supplier to remove.
     */

    @Override
    public void remove(int id) {

        try {
        String query = "DELETE FROM suppliers WHERE id = ?";

        Connection connection = getConnection();
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);
        executeQuery(stmt.toString());
        connection.close();}
        catch (SQLException e) {
            System.out.println("Could not remove supplier from database.");
        }

    }

    /**
     * Gets all suppliers.
     * @return list of all suppliers.
     */

    @Override
    public List<Supplier> getAll() {

        try {
        List<Supplier> results = new ArrayList<>();
        String query = "SELECT * FROM suppliers;";

        Connection connection = getConnection();
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet resultSet = stmt.executeQuery();

        while (resultSet.next()) {
            int dbId = resultSet.getInt("id");
            Supplier supplier = new Supplier(resultSet.getString("supplier_name"),
                    resultSet.getString("supplier_description"));
            supplier.setId(dbId);
            results.add(supplier);
        }
        connection.close();
        return results;}
        catch (SQLException e) {
            return null;
        }
    }

    /**
     * Returns a connection with default data.
     * @return database Connection
     * @throws SQLException when {@link DriverManager} fails.
     */

    @Override
    Connection getConnection() throws SQLException {
        DatabaseConnectionData dbConn = new DatabaseConnectionData("connection.properties");
        return DriverManager.getConnection(
                dbConn.getDb(),
                dbConn.getDbUser(),
                dbConn.getDbPassword());
    }

}


