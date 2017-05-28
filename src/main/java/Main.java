import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

import com.codecool.shop.controller.DaoProvider;
import com.codecool.shop.controller.ProductController;
import com.codecool.shop.dao.*;
import com.codecool.shop.model.*;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import org.json.simple.JSONObject;
import java.util.List;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    private static ProductDao productDataStore;
    private static LineItemDao lineItemDataStore;
    private static OrderDao orderDataStore;
    private static SupplierDao supplierDataStore;
    private static ShoppingCart shoppingCart = new ShoppingCart();
    private static ProductCategoryDao productCategoryDataStore;

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    //static final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);



    private static boolean isDb = true;     // change this to state!!

    private static void setupInstances(){
        //System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        //final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);
        DaoProvider.setup(Main.isDb);
        productDataStore = DaoProvider.productDao;
        logger.debug("Creating new {}", productDataStore.getClass().getSimpleName());
        lineItemDataStore = DaoProvider.lineItemDao;
        logger.debug("Creating new {}", lineItemDataStore.getClass().getSimpleName());
        orderDataStore = DaoProvider.orderDao;
        logger.debug("Creating new {}", orderDataStore.getClass().getSimpleName());
        supplierDataStore = DaoProvider.supplierDao;
        logger.debug("Creating new {}", supplierDataStore.getClass().getSimpleName());
        productCategoryDataStore = DaoProvider.productCategoryDao;
        logger.debug("Creating new {}", productCategoryDataStore.getClass().getSimpleName());
    }

    public static void main(String[] args) {

        //System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
        //final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

        setupInstances();
        populateData();

//        logger.trace("trace");
//        logger.debug("debug");
//        logger.info("info");
//        logger.warn("warning");
//        logger.error("error");


        // default server settings
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        redirect.get("/", "/index");

        before("/index", (req, res) -> {
            logger.info("Rendering products.");
            req.session().attribute("shoppingCart", shoppingCart);
        });

        get("/", ProductController::renderProducts, new ThymeleafTemplateEngine());

        // Equivalent with above
        get("/index", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render( ProductController.renderProducts(req, res) );
        });

        get("/index/category/:id", ProductController::renderByCategory, new ThymeleafTemplateEngine());

        get("/index/supplier/:id", ProductController::renderBySupplier, new ThymeleafTemplateEngine());

        //get("/cart", ProductController::renderToCart, new ThymeleafTemplateEngine());
        // Add this line to your project to enable the debug screen

        post("/increase-lineitem/:lineitem-id", (req, res) -> {
            LineItem lineItem = lineItemDataStore.find(Integer.parseInt(req.params(":lineitem-id")));
            lineItem.setQuantity(lineItem.getQuantity() + 1);
            JSONObject jsonObj = CreateLineItemJSONObject(lineItem);
            logger.info("increasing lineitem: {}", lineItem.getProduct().getName());
            logger.debug("increasing lineitem: {} to {}", lineItem.getProduct().getName(), lineItem.getQuantity());
            res.type("application/json");
            return jsonObj;
        });

        post("/decrease-lineitem/:lineitem-id", (req, res) -> {
            Integer ID = Integer.parseInt(req.params(":lineitem-id"));
            LineItem lineItem = lineItemDataStore.find(ID);
            lineItem.setQuantity(lineItem.getQuantity() - 1);
            if (lineItem.getQuantity() == 0) {
                shoppingCart.remove(lineItem);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("status", "deleted");
                jsonObj.put("id", ID);
                jsonObj.put("totalprice", shoppingCart.getTotalPrice());
                res.type("application/json");
                logger.info("Deleting lineitem");
                logger.debug("Deleting lineitem: {}", lineItem.getProduct().getName());
                return jsonObj;
            }
            else {
                JSONObject jsonObj = CreateLineItemJSONObject(lineItem);
                res.type("application/json");
                logger.info("Decreasing lineitem: {}", lineItem.getProduct().getName());
                logger.debug("Decreasing lineitem: {} to {}", lineItem.getProduct().getName(), lineItem.getQuantity());
                return jsonObj;
            }
        });

        post("/add-product/:product-id", (req, res) -> {
            Product product = productDataStore.find(Integer.parseInt(req.params(":product-id")));
            Boolean lineItemFound = false;
            for (LineItem lineItem : lineItemDataStore.getAll()) {
                if (lineItem.getProduct() == product) {
                    lineItemFound = true;
                    lineItem.setQuantity(lineItem.getQuantity()+1);
                }
            }
            if (!lineItemFound) {
                LineItem lineItem = new LineItem(product);
                lineItemDataStore.add(lineItem);
                shoppingCart.add(lineItem);
            }
            Integer numOfLineItems = shoppingCart.getShoppingList().size();
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("numOfLineItems", numOfLineItems);
            res.type("application/json");
            logger.info("Adding products to cart: {}", product.getName());
            return jsonObj;
        });

        get("/cart", (Request req, Response res) -> {
            logger.info("Rendering shopping cart.");
            return new ThymeleafTemplateEngine().render( ProductController.renderToCart(req, res, shoppingCart) );
        });

        get("/checkout", (Request req, Response res) -> {
            logger.info("Rendering checkout.");
            return new ThymeleafTemplateEngine().render( ProductController.renderToCheckout(req, res) );
        });

        post("/checkout/done", (Request req, Response res) -> {
            Address orderAddress = new Address(req.queryParams("billingCountry"), req.queryParams("billingCity"),
                    req.queryParams("billingZip"), req.queryParams("billingAddress"));
            String name = req.queryParams("name");
            ShoppingCart currentSession = req.session().attribute("shoppingCart");
            List<LineItem> lineItems = currentSession.getShoppingList();
            Order order = new Order(name, orderAddress, lineItems);
            orderDataStore.add(order);
            logger.info("Adding new order: {}", order.getName());
            logger.debug("Adding Address {} to order: {}", order.getAddress(), order.getName());
            return req.queryParams("email");
        });

        enableDebugScreen();
    }


    public static JSONObject CreateLineItemJSONObject(LineItem lineItem) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("quantity", lineItem.getQuantity());
        jsonObj.put("price", lineItem.getPrice());
        jsonObj.put("currency", lineItem.getProduct().getDefaultCurrency().toString());
        jsonObj.put("status", "exists");
        jsonObj.put("totalprice", shoppingCart.getTotalPrice());
        return jsonObj;
    }


    public static void populateData() {
        //setting up a new supplier

        logger.info("Populating database");
        Supplier amazon = new Supplier("Amazon", "Digital content and services");
        amazon.setId(1);
        supplierDataStore.add(amazon);
        Supplier lenovo = new Supplier("Lenovo", "Computers");
        lenovo.setId(2);
        supplierDataStore.add(lenovo);
        Supplier lajos = new Supplier("Lajos", "Potatoes");
        lajos.setId(3);
        supplierDataStore.add(lajos);

        //setting up a new product category
        ProductCategory tablet = new ProductCategory("Tablet", "Hardware", "A tablet computer, commonly shortened to tablet, is a thin, flat mobile computer with a touchscreen display.");
        tablet.setId(1);
        productCategoryDataStore.add(tablet);

        ProductCategory potato = new ProductCategory("Potato", "Food", "A very delicious dish. Edible. Vegan. No sugar, no lactose. Much healthy.");
        potato.setId(2);
        productCategoryDataStore.add(potato);

        //setting up products and printing it
        productDataStore.add(new Product("Amazon Fire", 49.9f, "USD", "Fantastic price. Large content ecosystem. Good parental controls. Helpful technical support.", tablet, amazon));
        productDataStore.add(new Product("Lenovo IdeaPad Miix 700", 479, "USD", "Keyboard cover is included. Fanless Core m5 processor. Full-size USB ports. Adjustable kickstand.", tablet, lenovo));
        productDataStore.add(new Product("Amazon Fire HD 8", 89, "USD", "Amazon's latest Fire HD 8 tablet is a great value for media consumption.", tablet, amazon));
        Product potato2 = new Product("Happy Potato", 3, "USD", "A potato who is very very happy.", potato, lajos);
        potato2.setId(4);
        productDataStore.add(potato2);
    }


}
