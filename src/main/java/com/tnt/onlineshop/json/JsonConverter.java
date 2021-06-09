package com.tnt.onlineshop.json;

import com.google.gson.Gson;
import com.tnt.onlineshop.entity.Product;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class JsonConverter {
    public String toJson(List<Product> productList) {
        JSONArray jsonArray = new JSONArray();
        for (Product product : productList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", product.getId());
            jsonObject.put("name", product.getName());
            jsonObject.put("price", product.getPrice());
            jsonObject.put("lastModifiedTime", product.getLastModifiedTime());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    public Product toProduct(Reader reader) {
        Gson gson = new Gson();
        return gson.fromJson(reader, Product.class);
    }

    public JSONObject getJsonObject(BufferedReader reader) {
        StringBuilder jsonCredentials = new StringBuilder();
        String jsonReadLine;
        JSONObject jsonObject;
        try {
            while ((jsonReadLine = reader.readLine()) != null) {
                jsonCredentials.append(jsonReadLine);
            }
            String userCredentials = jsonCredentials.toString();
            jsonObject = new JSONObject(userCredentials);
        } catch (IOException e) {
            throw new RuntimeException("Error read json from request", e);
        } catch (JSONException e) {
            throw new JSONException("Error parsing JSON request string", e);
        }
        return jsonObject;
    }
}
