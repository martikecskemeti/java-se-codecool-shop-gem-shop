package com.codecool.shop.controller;

import com.codecool.shop.dao.*;
import com.codecool.shop.dao.implementation.*;

/**
 * Created by keli on 2017.05.18..
 */
public class DaoProvider {
    public static ProductDao productDao;
    public static LineItemDao lineItemDao;
    public static OrderDao orderDao;
    public static ProductCategoryDao productCategoryDao;
    public static SupplierDao supplierDao;

    public static void setup(boolean isDb) {
        if (!isDb) setupDaoMem();
        else setupDaoJdbc();
    }

    /**
     * Creates database DAO-s.
     */

    private static void setupDaoJdbc(){
        productDao = new ProductDaoImplJdbc();
        lineItemDao = new LineItemDaoImplJdbc();
        orderDao = new OrderDaoJdbc();
        productCategoryDao = new ProductCategoryDaoImplJdbc();
        supplierDao = new SupplierDaoJdbc();
    }

    /**
     * Creates mem DAO-s.
     */

    private static void setupDaoMem(){
        productDao = ProductDaoMem.getInstance();
        lineItemDao = LineItemDaoMem.getInstance();
        orderDao = OrderDaoMem.getInstance();
        productCategoryDao = ProductCategoryDaoMem.getInstance();
        supplierDao = SupplierDaoMem.getInstance();
    }
}
