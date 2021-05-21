package com.tnt.onlineshop.web.servlets;

import com.google.gson.Gson;
import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.json.JsonConverter;
import com.tnt.onlineshop.service.ProductService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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
        String requestUri = request.getRequestURI();

        int productId = requestUri.lastIndexOf("/");
        String substring = requestUri.substring(productId + 1);
        if ("products".equals(substring)) {
            productList = productService.findAll();
            jsonProducts.append(jsonConverter.toJson(productList));
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            productId = Integer.parseInt(substring);
            Optional<Product> product = productService.findById(productId);
            if (product.isPresent()) {
                productList.add(product.get());
                jsonProducts.append(jsonConverter.toJson(productList));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(jsonProducts.toString());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Product product = jsonConverter.toProduct(request.getReader());
        if (productService.add(product)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.setContentType("application/json; charset=UTF-8");
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Product product = jsonConverter.toProduct(request.getReader());
        if (productService.update(product)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.setContentType("application/json; charset=UTF-8");
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        int productId = gson.fromJson(request.getReader(), int.class);
        if (productService.delete(productId)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.setContentType("application/json; charset=UTF-8");
    }
}
