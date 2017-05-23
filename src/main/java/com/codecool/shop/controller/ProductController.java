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

/**
 * Controls the rendering of products.
 * Rendering products based on filter conditions.
 */

public class ProductController {
    private static ProductDao productDataStore;
    private static ProductCategoryDao productCategoryDataStore;
    private static SupplierDao supplierDataStore;

    private static void checkAndSetupStaticVar(){
        if (productDataStore == null) productDataStore = DaoProvider.productDao;
        if (productCategoryDataStore == null) productCategoryDataStore = DaoProvider.productCategoryDao;
        if (supplierDataStore == null) supplierDataStore = DaoProvider.supplierDao;
    }

    /**
     * Renders all products from database.
     * @param req request object
     * @param res response object
     * @return populated ModelAndView
     */

    public static ModelAndView renderProducts(Request req, Response res) {
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

    /**
     * Renders products, based on productsCategory from request params.
     * @param req request object
     * @param res response object
     * @return populated ModelAndView
     */

    public static ModelAndView renderByCategory(Request req, Response res) {
        checkAndSetupStaticVar();
        int searchedId = Integer.parseInt(req.params(":id"));
        ShoppingCart currentSession = req.session().attribute("shoppingCart");
        int shoppingListSize = currentSession.getShoppingList().size();
        Map params = new HashMap<>();
        params.put("categories", productCategoryDataStore.getAll());
        params.put("suppliers", supplierDataStore.getAll());
        params.put("products", productDataStore.getBy(productCategoryDataStore.find(searchedId)));
        params.put("shoppingListSize", shoppingListSize);
        return new ModelAndView(params, "product/index");
    }

    /**
     * Renders products, based on supplier from request params.
     * @param req request object
     * @param res response object
     * @return populated ModelAndView
     */

    public static ModelAndView renderBySupplier(Request req, Response res) {
        checkAndSetupStaticVar();
        int searchedId = Integer.parseInt(req.params(":id"));
        Map params = new HashMap<>();
        ShoppingCart currentSession = req.session().attribute("shoppingCart");
        int shoppingListSize = currentSession.getShoppingList().size();
        params.put("suppliers", supplierDataStore.getAll());
        params.put("categories", productCategoryDataStore.getAll());
        params.put("products", productDataStore.getBy(supplierDataStore.find(searchedId)));
        params.put("shoppingListSize", shoppingListSize);
        return new ModelAndView(params, "product/index");
    }

    /**
     * Renders shoppingcart page with the given lineitems.
     * @param req request object
     * @param res response object
     * @param shoppingCart object
     * @return populated ModelAndView
     */

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

    /**
     * Renders checkout page.
     * @param req request object
     * @param res response object
     * @return populated ModelAndView
     */

    public static ModelAndView renderToCheckout(Request req, Response res) {
        checkAndSetupStaticVar();
        Map params = new HashMap<>();
        return new ModelAndView(params, "product/checkout");
    }

}
