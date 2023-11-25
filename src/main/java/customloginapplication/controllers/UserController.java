package customloginapplication.controllers;

import customloginapplication.dto.AuthRequest;
import customloginapplication.dto.UserDto;
import customloginapplication.models.Order;
import customloginapplication.models.Product;
import customloginapplication.models.User;
import customloginapplication.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
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
    @Autowired
    private AuthenticationManager authenticationManager;
    private UserService userService;


    public UserController(CookieService cookieService, OrderService orderService, ProductService productService, UserDetailService userDetailsService, UserService userService) {
        this.cookieService = cookieService;
        this.orderService = orderService;
        this.productService = productService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }


    @PostMapping("/authenticate")
    public String auth(@RequestParam("username") String username, RedirectAttributes redirectAttributes, @RequestParam("password") String password, Model model, HttpServletResponse response) {
        AuthRequest authRequest = new AuthRequest(password, username);
        List<String> userRoles = userDetailsService.getUserRolesByUsername(username);
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.getUsername(), userRoles);
                response.addCookie(cookieService.createCookie("jwtToken", token));
                response.addCookie(cookieService.createCookie("authenticated", "true"));
                model.addAttribute("key", true);
                log.info("User {} is authenticated", username);
                return "redirect:/";
            } else {
                return "register";
            }
        } catch (AuthenticationException e) {
            log.info("Invalid Username or password for user {}", username);
            throw new UsernameNotFoundException("Invalid user request!");
        }
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


    @GetMapping("/userProfile")
    public String userProfile(Principal principal, Model model, HttpServletRequest request) {
        if (principal != null) {
            userDetailsService.handleAuthenticatedUser(model, request);
            User user = userDetailsService.findByUsername(principal.getName());
            model.addAttribute("user", user);
            return "userProfile";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/updateUserInfo")
    public String updateUserData(@ModelAttribute User user, HttpServletResponse response) {
        List<String> userRoles = userDetailsService.getUserRolesByUsername(user.getUsername());
        userDetailsService.updateUserById(user);
        String token = jwtService.generateToken(user.getUsername(), userRoles);
        response.addCookie(cookieService.createCookie("jwtToken", token));
        return "redirect:/userProfile";
    }

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

    @GetMapping("/login")
    public String login(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "register";
    }

    @PostMapping("/register")
    public String registerSave(@ModelAttribute("user") UserDto userDto, Model model) {
        User user = userService.findByUsername(userDto.getUsername());
        if (user != null) {
            model.addAttribute("userexist", user);
            return "register";
        }
        userService.save(userDto);
        return "redirect:/register?success";
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//    private String extractUsernameFromToken(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("jwtToken".equals(cookie.getName())) {
//                    String token = cookie.getValue();
//                    log.info("{}", token);
//                    return token;
//                }
//            }
//        }
//        throw new NullPointerException("Куки 'jwtToken' не знайдено");
//    }

//    public HashMap<Long, List<String>> resolveProducts(List<Product> products) {
//        Map<Long, List<String>> productImages = new HashMap<>();
//        for (Product product : products) {
//            List<String> imageStrings = new ArrayList<>();
//            if (!product.getImages().isEmpty()) {
//                Image firstImage = product.getImages().get(0);
//                String imageString = Base64.getEncoder().encodeToString(firstImage.getBytes());
//                imageStrings.add("data:image/jpeg;base64, " + imageString);
//            } else {
//                return null;
//            }
//            productImages.put(product.getId(), imageStrings);
//        }
//        return (HashMap<Long, List<String>>) productImages;
//
//    }


//    private Cookie createCookie(String name, String value) {
//        Cookie cookie = new Cookie(name, value);
//        cookie.setMaxAge(60 * 30);
//        return cookie;
//    }

//    private void handleAuthenticatedUser(Model model, HttpServletRequest request) {
//        String auth = userDetailsService.getAuthenticatedValueFromCookie(request);
//        boolean isAuthenticated = "true".equals(auth);
//        model.addAttribute("key", isAuthenticated);
//    }

}






