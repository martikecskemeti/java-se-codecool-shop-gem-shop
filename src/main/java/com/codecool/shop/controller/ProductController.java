package com.codecool.shop.controller;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.model.ShoppingCart;
import spark.Request;
import spark.Response;
import spark.ModelAndView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductController {
    private static ProductDao productDataStore;
    private static ProductCategoryDao productCategoryDataStore;
    private static SupplierDao supplierDataStore;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class.getSimpleName());

    private static void checkAndSetupStaticVar(){
        if (productDataStore == null) productDataStore = DaoProvider.productDao;
        if (productCategoryDataStore == null) productCategoryDataStore = DaoProvider.productCategoryDao;
        if (supplierDataStore == null) supplierDataStore = DaoProvider.supplierDao;
    }

    public static ModelAndView renderProducts(Request req, Response res) throws SQLException {
        checkAndSetupStaticVar();
        Map params = new HashMap<>();
        ShoppingCart currentSession = req.session().attribute("shoppingCart");
        int shoppingListSize = currentSession.getShoppingList().size();
        params.put("categories", productCategoryDataStore.getAll());
        params.put("suppliers", supplierDataStore.getAll());
        params.put("products", productDataStore.getAll());
        params.put("shoppingListSize", shoppingListSize);
        return new ModelAndView(params, "product/index");
    }

    public static ModelAndView renderByCategory(Request req, Response res) throws SQLException {
        checkAndSetupStaticVar();
        int searchedId = Integer.parseInt(req.params(":id"));
        ShoppingCart currentSession = req.session().attribute("shoppingCart");
        int shoppingListSize = currentSession.getShoppingList().size();
        Map params = new HashMap<>();
        params.put("categories", productCategoryDataStore.getAll());
        params.put("suppliers", supplierDataStore.getAll());
        params.put("products", productDataStore.getBy(productCategoryDataStore.find(searchedId)));
        params.put("shoppingListSize", shoppingListSize);
        logger.info("Filtering products by category: {}", productCategoryDataStore.find(searchedId).getName());
        return new ModelAndView(params, "product/index");
    }

    public static ModelAndView renderBySupplier(Request req, Response res) throws SQLException {
        checkAndSetupStaticVar();
        int searchedId = Integer.parseInt(req.params(":id"));
        Map params = new HashMap<>();
        ShoppingCart currentSession = req.session().attribute("shoppingCart");
        int shoppingListSize = currentSession.getShoppingList().size();
        params.put("suppliers", supplierDataStore.getAll());
        params.put("categories", productCategoryDataStore.getAll());
        params.put("products", productDataStore.getBy(supplierDataStore.find(searchedId)));
        params.put("shoppingListSize", shoppingListSize);
        logger.info("Filtering products by supplier: {}", productDataStore.find(searchedId).getName());
        return new ModelAndView(params, "product/index");
    }

    public static ModelAndView renderToCart(Request req, Response res, ShoppingCart shoppingCart) {
        checkAndSetupStaticVar();
        Map params = new HashMap<>();
        params.put("lineitems", shoppingCart.getShoppingList());
        if (0 < shoppingCart.getShoppingList().size()) {
            params.put("totalprice", shoppingCart.getTotalPrice());
            params.put("currency", shoppingCart.getShoppingList().get(0).getProduct().getDefaultCurrency());
        } else {
            params.put("totalprice", 0);
            params.put("currency", "USD");
        }
        return new ModelAndView(params, "product/cart");
    }

    public static ModelAndView renderToCheckout(Request req, Response res) {
        checkAndSetupStaticVar();
        Map params = new HashMap<>();
        return new ModelAndView(params, "product/checkout");
    }

}
