package com.codecool.shop.controller;

import com.codecool.shop.dao.*;
import com.codecool.shop.dao.implementation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by keli on 2017.05.18..
 */
public class DaoProvider {
    public static ProductDao productDao;
    public static LineItemDao lineItemDao;
    public static OrderDao orderDao;
    public static ProductCategoryDao productCategoryDao;
    public static SupplierDao supplierDao;
    private static final Logger logger = LoggerFactory.getLogger(DaoProvider.class);

    public static void setup(boolean isDb) {
        if (!isDb) {setupDaoMem();
            logger.debug("Creating new DaoMem");}
        else {setupDaoJdbc();
            logger.debug("Creating new DaoJdbc");}
    }

    private static void setupDaoJdbc(){
        productDao = new ProductDaoImplJdbc();
        lineItemDao = new LineItemDaoImplJdbc();
        orderDao = new OrderDaoJdbc();
        productCategoryDao = new ProductCategoryDaoImplJdbc();
        supplierDao = new SupplierDaoJdbc();
    }

    private static void setupDaoMem(){
        productDao = ProductDaoMem.getInstance();
        lineItemDao = LineItemDaoMem.getInstance();
        orderDao = OrderDaoMem.getInstance();
        productCategoryDao = ProductCategoryDaoMem.getInstance();
        supplierDao = SupplierDaoMem.getInstance();
    }
}
