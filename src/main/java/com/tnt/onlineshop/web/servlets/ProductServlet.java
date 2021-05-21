package com.tnt.onlineshop.web.servlets;

import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.json.JsonConverter;
import com.tnt.onlineshop.service.ProductService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductServlet extends HttpServlet {

    private final JsonConverter jsonConverter = new JsonConverter();
    private final ProductService productService;

    public ProductServlet(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder jsonProducts = new StringBuilder();
        List<Product> productList = new ArrayList<>();
        String requestUri = request.getRequestURI().substring(request.getContextPath().length());

        int productId = requestUri.lastIndexOf("/");
        String substring = requestUri.substring(productId + 1);
        if ("products".equals(substring)){
            productList = productService.findAll();
            jsonProducts.append(jsonConverter.toJson(productList));
        } else {
            productId = Integer.parseInt(substring);
            Optional<Product> product = productService.findById(productId);
            if (product.isPresent()){
                productList.add(product.get());
                jsonProducts.append(jsonConverter.toJson(productList));
            }
        }

        response.getWriter().write(jsonProducts.toString());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Product product = jsonConverter.toProduct(request.getReader());
        productService.add(product);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {

    }
}
