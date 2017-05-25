import com.codecool.shop.controller.CartController;
import com.codecool.shop.controller.CustomerController;
import com.codecool.shop.controller.OrderController;
import com.codecool.shop.controller.ProductController;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // default server settings
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        // populate some data for the memory storage
        populateData();
        logger.info("Successful data population");

        // generating Supplier and Product category objects from the DB
        SupplierDaoJdbc suppliers = SupplierDaoJdbc.getInstance();
        ProductCategoryDaoJdbc prodCategs = ProductCategoryDaoJdbc.getInstance();
        suppliers.getAll();
        prodCategs.getAll();
        logger.info("Database initialization successful");

        // generating controller instances
        CartController cartController = new CartController();
        OrderController orderController = new OrderController();
        CustomerController customerController = new CustomerController();
        logger.info("Controller generation successful");

        // generating shopping cart
        ShoppingCart cart = ShoppingCart.getInstance();
        logger.info("Cart initialization successful");

        // Always add generic routes to the end
        get("/", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render(ProductController.renderAllProducts(req, res));
        });

        get("/addToCart", (req, res) -> {
            Integer productId = Integer.parseInt(req.queryParams("productId"));
            String size = cart.getCartSize();
            String userId = req.session().attribute("currentUser");
            if (userId == null) {
                logger.warn("User is not logged in");
                return "user is not logged in";
            } else {
                cartController.checkCartDB(userId, productId);
                logger.debug("Checked product: {} of user: {}.", productId, userId);
                return (size + " items");
            }
        });

        get("/addCartToOrder", (req, res) -> {
            String userId = req.session().attribute("currentUser");
            orderController.addCartToOrder(userId);
            logger.debug("{}'s cart was added to order.");
            return "success";
        });

        get("/getCartSize", (req, res) -> {
            String userID = req.session().attribute("currentUser");
            logger.debug("Cart size of {} user was checked", userID);
            return (cartController.getCartSize(userID));
        });

        get("/getTotalPrice", (req, res) -> {
            String userID = req.session().attribute("currentUser");
            String cartTotalPrice = cartController.getTotalPrice(userID);
            logger.debug("{} user's cart's total price: {}", userID, cartTotalPrice);
            return ("Total: " + cartTotalPrice + " $");
        });

        get("/getCartContentFromDB", (Request req, Response res) -> {
            String userID = req.session().attribute("currentUser");
            ArrayList<LineItem> items = cartController.getCartContentDB(userID);
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<String> result = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                LineItem prod = items.get(i);
                String productJson = mapper.writeValueAsString(prod);
                result.add(productJson);
            }
            logger.debug("User: {} cart is {}", userID, result);
            return result;
        });

        get("/checkout", (req, res) -> {
            HashMap<String, HashMap> cartContent = new HashMap<>();
            HashMap<String, String> totalPrice = new HashMap<>();
            totalPrice.put("totalPrice", cart.getTotalPrice());
            cartContent.put("cartContent", cart.getCartContent());
            cartContent.put("totalPrice", totalPrice);
            logger.debug("Checkout with {} content", cartContent);
            return renderTemplate("product/checkout", cartContent);
        });

        get("/payment", (req, res) -> {
            HashMap<String, ArrayList> dummyHashMap = new HashMap<>();
            return renderTemplate("product/payment", dummyHashMap);
        });

        get("/pay", (req, res) -> {
            HashMap<String, ArrayList> dummyHashMap = new HashMap<>();
            return renderTemplate("product/pay", dummyHashMap);
        });

        post("/payment", (req, res) -> {
            String name = req.queryParams("name");
            String email = req.queryParams("email");
            String phone = req.queryParams("phone");
            String billing = req.queryParams("billing");
            String shipping = req.queryParams("shipping");
            Order order = Order.getInstance(name, email, phone, billing, shipping);
            order.updateCart(cart);
            res.redirect("/payment");
            return "success";
        });

        get("/paid", (req, res) -> {
            HashMap<String, ArrayList> dummyHashMap = new HashMap<>();
            return renderTemplate("product/paid", dummyHashMap);
        });

        get("/updateShoppingCart", (req, res) -> {
            String productName = req.queryParams("productName");
            Integer quantity = Integer.parseInt(req.queryParams("quantity"));
            String userID = req.session().attribute("currentUser");
            cartController.updateQuantityDB(userID, productName, quantity);
            logger.debug("User: {}, product: {}, quantity: {} - updated.", userID, productName, quantity);
            return "success";
        });

        get("/login", (req, res) -> {
            HashMap<String, ArrayList> dummyHashMap = new HashMap<>();
            return renderTemplate("product/login", dummyHashMap);
        });

        get("/register", (req, res) -> {
            HashMap<String, ArrayList> dummyHashMap = new HashMap<>();
            return renderTemplate("product/register", dummyHashMap);
        });

        post("/register", (req, res) -> {
            String name = req.queryParams("name");
            String email = req.queryParams("email");
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String address = req.queryParams("address");
            String data = name + email + username + password + address;
            customerController.registerUser(name, email, username, password, address);
            res.redirect("/");
            logger.debug("User with {} registered.", data);
            return 1;
        });

        post("/login", (req, res) -> {
            req.session().removeAttribute("currentUser");
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            if (customerController.loginValidation(username, password)) {
                req.session().attribute("currentUser", customerController.getUserId(username));
                res.redirect("/");
                return "login successful";
            }
            res.redirect("/login");
            return "login failed";
        });

        get("/logout", (req, res) -> {
            req.session().removeAttribute("currentUser");
            HashMap<String, ArrayList> dummyHashMap = new HashMap<>();
            res.redirect("/");
            logger.debug("User is logged out.");
            return 1;
        });

        get("/checkUser", (req, res) -> {
            String user = req.session().attribute("currentUser");
            logger.debug("{} user is checked.", user);
            if (user == null) {
                return "null";
            } else {
                return user;
            }
        });

        // Add this line to your project to enable the debug screen
        enableDebugScreen();
    }

    private static void populateData() {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();

        // setting up new suppliers
        Supplier amazon = new Supplier("Amazon", "Digital content and services");
        supplierDataStore.add(amazon);
        Supplier lenovo = new Supplier("Lenovo", "Computers and laptops");
        supplierDataStore.add(lenovo);
        Supplier htc = new Supplier("HTC", "Mobile phones and accessories");
        supplierDataStore.add(htc);
        Supplier coverYourFur = new Supplier("Cover-Your-Fur", "High-quality animal shelters");
        supplierDataStore.add(coverYourFur);
        Supplier lg = new Supplier("LG", "Mobile phones and accessories");
        supplierDataStore.add(lg);
        Supplier apple = new Supplier("Apple", "Computers, laptops, tablets, mobile phones and accessories");
        supplierDataStore.add(apple);
        Supplier cornerVendor = new Supplier("CornerVendor", "Guess you'll have to figure this out yourself");
        supplierDataStore.add(cornerVendor);
        Supplier stupidMemes = new Supplier("StupidMemes", "Silly memes from all over the world");
        supplierDataStore.add(stupidMemes);
        Supplier pfizer = new Supplier("Pfizer", "One of the world's largest pharmaceutical companies");
        supplierDataStore.add(pfizer);

        // setting up new product categories
        ProductCategory tablet = new ProductCategory("Tablet", "Hardware", "A tablet computer, commonly shortened to tablet, is a thin, flat mobile computer with a touchscreen display.");
        productCategoryDataStore.add(tablet);
        ProductCategory laptop = new ProductCategory("Laptop", "Hardware", "Portable computers used for a variety of purposes.");
        productCategoryDataStore.add(laptop);
        ProductCategory mobile = new ProductCategory("Mobile", "Hardware", "Portable telephones used for a variety of purposes.");
        productCategoryDataStore.add(mobile);
        ProductCategory dogShelter = new ProductCategory("DogShelter", "Outdoor appliances", "Outdoor structures to protect your pets from the weather.");
        productCategoryDataStore.add(dogShelter);
        ProductCategory drugs = new ProductCategory("Drugs", "Recreational use", "u w4nt some djanga, m8");
        productCategoryDataStore.add(drugs);
        ProductCategory justWat = new ProductCategory("JustWat", "Fun", "Totally retarded things since '97.");
        productCategoryDataStore.add(justWat);
        ProductCategory memes = new ProductCategory("Memes", "Fun", "no explanation, fak u m8");
        productCategoryDataStore.add(memes);
        ProductCategory foodSupplement = new ProductCategory("FoodSupplement", "Health", "Protein without limits.");
        productCategoryDataStore.add(foodSupplement);

        // setting up products and printing them
        productDataStore.add(new Product("Amazon Fire", 49.9f, "USD", "Fantastic price. Large content ecosystem. Good parental controls.", tablet, amazon));
        productDataStore.add(new Product("Lenovo IdeaPad", 479, "USD", "Keyboard cover is included. Fanless Core m5 processor. Full-size USB ports.", tablet, lenovo));
        productDataStore.add(new Product("Amazon Fire HD 8", 220, "USD", "Amazon's latest Fire HD 8 tablet is a great value for media consumption.", tablet, amazon));
        productDataStore.add(new Product("Asus R13", 190, "USD", "Amazon's latest Fire HD 8 tablet is a great value for media consumption.", laptop, amazon));
        productDataStore.add(new Product("HTC H5", 520, "USD", "HTC's latest H5 mobile is a great value for media consumption.", mobile, htc));
        productDataStore.add(new Product("Lenovo K3", 410, "USD", "Lenovo's latest K3 mobile is a great value for media consumption.", mobile, lenovo));
        productDataStore.add(new Product("Papillon Mark II", 99.9f, "USD", "Basic dog shelter made from sturdy wood. 5 year warranty.", dogShelter, coverYourFur));
        productDataStore.add(new Product("Weed", 30, "USD", "you have the money, i have the means smoek weeeed evry daaaay", drugs, cornerVendor));
        productDataStore.add(new Product("LG G6", 560, "USD", "Big screen. Small Phone. Blue sky. Green planet.", mobile, lg));
        productDataStore.add(new Product("LG G5", 440, "USD", "5.3\" IPS Quantum QHD Display & Metal Body and shit", mobile, lg));
        productDataStore.add(new Product("Apple iPhone", 800, "USD", "I don't have enough money but I still wanna look rich", mobile, apple));
        productDataStore.add(new Product("Apple MacBook", 1199, "USD", "I have too much money and I have no idea how to spend it responsibly", laptop, apple));
        productDataStore.add(new Product("Flector Rapid", 10, "USD", "yo dawg i herd u liek pain so i brought yo soem painkillaz", drugs, pfizer));
        productDataStore.add(new Product("Unsureness", 3333, "USD", "General Inability To Decide, fresh from 'Murica", justWat, stupidMemes));
        productDataStore.add(new Product("Poo Puff", 0.5f, "USD", "'wat are you talking about, it definitely wasn't me'", justWat, stupidMemes));
        productDataStore.add(new Product("Obi-Wan Kenobi", 11.5f, "USD", "'these are definitely not the droids you are looking for'", memes, stupidMemes));
        productDataStore.add(new Product("BIO WHEY 100%", 12, "USD", "BiotechUSA protein at its finest. 100% refined beef collagen whey", foodSupplement, cornerVendor));
        productDataStore.add(new Product("Ultra-Super Pack", 122, "USD", "A fine selection of quality protein products. Arnold would surely buy this", foodSupplement, cornerVendor));
    }

    private static String renderTemplate(String view, HashMap model) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, view));
    }
}