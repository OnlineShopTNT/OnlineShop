package com.tnt.onlineshop.web.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.json.JsonConverter;
import com.tnt.onlineshop.service.ProductService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class ProductServlet extends HttpServlet {

    private static final JsonConverter jsonConverter = new JsonConverter();
    private static final Gson gson = new Gson();
    private final ProductService productService;

    public ProductServlet(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder jsonFormatProducts = new StringBuilder();
        String requestUri = request.getRequestURI();
        int responseStatus = HttpServletResponse.SC_OK;

        int lastSlashIndex = requestUri.lastIndexOf("/");
        String substringAfterLastSlash = requestUri.substring(lastSlashIndex + 1);
        if ("products".equals(substringAfterLastSlash)) {
            jsonFormatProducts.append(jsonConverter.toJson(productService.findAll()));
        } else {
            try {
                int id = Integer.parseInt(substringAfterLastSlash);
                Optional<Product> product = productService.findById(id);
                if (product.isPresent()) {
                    jsonFormatProducts.append(gson.toJson(product.get()));
                } else {
                    responseStatus = HttpServletResponse.SC_NOT_FOUND;
                }
            } catch (NumberFormatException e) {
                responseStatus = HttpServletResponse.SC_BAD_REQUEST;
            }
        }
        response.setStatus(responseStatus);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(jsonFormatProducts.toString());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Product product = jsonConverter.toProduct(request.getReader());
        if (productService.add(product)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Reader reader = request.getReader();
        String requestUri = request.getRequestURI();
        int responseStatus = HttpServletResponse.SC_OK;
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

        try {
            int id = getIdFromUri(requestUri);
            if (jsonObject.keySet().contains("name") || jsonObject.keySet().contains("price")) {
                Optional<Product> productOptional = productService.findById(id);
                if (productOptional.isEmpty()) {
                    responseStatus = HttpServletResponse.SC_NOT_FOUND;
                } else {
                    Product updatedProduct = productOptional.get();
                    if (jsonObject.has("name")) {
                        updatedProduct.setName(jsonObject.get("name").getAsString());
                    }
                    if (jsonObject.has("price")) {
                        updatedProduct.setPrice(jsonObject.get("price").getAsDouble());
                    }
                    if (!productService.update(updatedProduct)) {
                        responseStatus = HttpServletResponse.SC_BAD_REQUEST;
                    }
                }
            } else {
                responseStatus = HttpServletResponse.SC_BAD_REQUEST;
            }
        } catch (NumberFormatException e) {
            responseStatus = HttpServletResponse.SC_BAD_REQUEST;
        }
        response.setStatus(responseStatus);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String requestUri = request.getRequestURI();
        int responseStatus = HttpServletResponse.SC_OK;
        try {
            int productId = getIdFromUri(requestUri);
            if (!productService.delete(productId)) {
                responseStatus = HttpServletResponse.SC_NOT_FOUND;
            }
        } catch (NumberFormatException e) {
            responseStatus = HttpServletResponse.SC_BAD_REQUEST;
        }
        response.setStatus(responseStatus);
    }

    int getIdFromUri(String uri) {
        int lastSlashIndex = uri.lastIndexOf("/");
        String substringAfterLastSlash = uri.substring(lastSlashIndex + 1);
        int id = Integer.parseInt(substringAfterLastSlash);
        return id;
    }

}
