package com.tnt.onlineshop.web.servlets;

import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.json.JsonConverter;
import com.tnt.onlineshop.service.ProductService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class ProductServlet extends HttpServlet {

    private final JsonConverter jsonConverter = new JsonConverter();
    private final ProductService productService;

    public ProductServlet(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> productList = productService.findAll();
        String jsonProducts = jsonConverter.toJson(productList);
        response.getWriter().write(jsonProducts);
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
