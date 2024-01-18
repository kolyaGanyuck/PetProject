package customloginapplication.controllers;

import customloginapplication.models.Order;
import customloginapplication.models.Product;
import customloginapplication.models.User;
import customloginapplication.services.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Controller
public class UserController {
    @Autowired
    private final CookieService cookieService;
    private final OrderService orderService;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final UserDetailService userDetailsService;
    @Autowired
    private JwtService jwtService;


    public UserController(CookieService cookieService, OrderService orderService, ProductService productService, UserDetailService userDetailsService, UserService userService) {
        this.cookieService = cookieService;
        this.orderService = orderService;
        this.productService = productService;
        this.userDetailsService = userDetailsService;
    }


    @GetMapping("/")
    public String mainPage(HttpServletRequest request, Model model) {
        List<Product> products = productService.getProducts();
        Map<Long, List<String>> productImages = productService.resolveProducts(products);
        userDetailsService.handleAuthenticatedUser(model, request);
        model.addAttribute("productImages", productImages);
        model.addAttribute("products", products);
        return "mainPage";
    }

    //1
//    @GetMapping("/userProfile")
//    public String userProfile(Principal principal, Model model, HttpServletRequest request) {
//        if (principal != null) {
//            userDetailsService.handleAuthenticatedUser(model, request);
//            User user = userDetailsService.findByUsername(principal.getName());
//            model.addAttribute("user", user);
//            return "userProfile";
//        } else {
//            return "redirect:/login";
//        }
//    }
//
//    //2
//    @PostMapping("/updateUserInfo")
//    public String updateUserData(@ModelAttribute User user, HttpServletResponse response) {
//        List<String> userRoles = userDetailsService.getUserRolesByUsername(user.getUsername());
//        userDetailsService.updateUserById(user);
//        String token = jwtService.generateToken(user.getUsername(), userRoles);
//        response.addCookie(cookieService.createCookie("jwtToken", token));
//        return "redirect:/userProfile";
//    }

    @GetMapping("/bySkuter")
    public String BySkuter(Model model, HttpServletRequest request) {
        userDetailsService.handleAuthenticatedUser(model, request);
        List<Product> products = productService.getProducts();
        Map<Long, List<String>> productImages = productService.resolveProducts(products);
        model.addAttribute("productImages", productImages);
        model.addAttribute("products", products);
        return "bySkuter";
    }

    @PostMapping("/add")
    public String add(@RequestParam(value = "file1") MultipartFile file1,
                      @RequestParam(value = "file2") MultipartFile file2,
                      @RequestParam(value = "file3") MultipartFile file3,
                      @ModelAttribute Product product) throws IOException {
        productService.saveProduct(product, file1, file2, file3);
        return "redirect:/";
    }

    @GetMapping("/createAdd")
    public String getProducts(Model model, HttpServletRequest request) {
        model.addAttribute("products", productService.getProducts());
        userDetailsService.handleAuthenticatedUser(model, request);
        return "formForCreateAdd";
    }

    @GetMapping("/product/{id}")
    public String findById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        userDetailsService.handleAuthenticatedUser(model, request);
        Product product = productService.findById(id);

        model.addAttribute("product", product);
        model.addAttribute("images", product.getImages());
        return "productById";
    }


    @GetMapping("/buyproduct/{id}")
    public String addToBasket(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        try {
            String token = cookieService.extractUsernameFromToken(request);
            String username = jwtService.extractUsername(token);
            User user = userDetailsService.findByUsername(username);
            Product product = productService.findById(id);
            Order order = new Order(product.getId(), user.getId(), LocalDateTime.now(), product.getPrice());
            orderService.save(order);
            log.info("save order {}", order);
        } catch (NullPointerException exception) {
            return "redirect:/login";
        }
        return "redirect:/";
    }

    @GetMapping("/basket")
    public String basket(HttpServletRequest request, Model model) {
        try {
            String token = cookieService.extractUsernameFromToken(request);
            String username = jwtService.extractUsername(token);
            User user = userDetailsService.findByUsername(username);
            List<Order> ordersOfUser = orderService.findOrderByUserId(user.getId());
            List<Long> productIds = ordersOfUser.stream()
                    .map(Order::getProductId)
                    .collect(Collectors.toList());
            List<Product> productsOfUser = productService.findProductsByIds(productIds);
            Map<Long, List<String>> productImages = productService.resolveProducts(productsOfUser);
            model.addAttribute("productImages", productImages);
            model.addAttribute("products", productsOfUser);
            userDetailsService.handleAuthenticatedUser(model, request);
            model.addAttribute("orders", ordersOfUser);
            return "basket";
        } catch (NullPointerException exception) {
            return "redirect:/login";
        }
    }

    @PostMapping("/submit/{id}")
    public String submitOrder(@PathVariable("id") Long id) {
        orderService.reserveOrder(id);
        return "redirect:/basket";
    }

}






