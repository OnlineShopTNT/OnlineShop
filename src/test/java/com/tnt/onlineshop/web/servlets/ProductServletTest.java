package com.tnt.onlineshop.web.servlets;

import com.google.gson.Gson;
import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.json.JsonConverter;
import com.tnt.onlineshop.service.ProductService;
import com.tnt.onlineshop.service.SessionService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ProductServletTest {

    private final ProductService mockedProductService = mock(ProductService.class);
    private final SessionService mockedSessionService = mock(SessionService.class);
    private final ProductServlet productServlet = new ProductServlet();
    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final InOrder inOrderResponse = inOrder(mockedResponse);
    private final InOrder inOrderRequest = inOrder(mockedRequest);
    private final JsonConverter jsonConverter = new JsonConverter();
    private final Gson gson = new Gson();
    private final PrintWriter mockedPrintWriter = mock(PrintWriter.class);
    private final Product product = new Product();

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedPrintWriter);
    }

    @Test
    @DisplayName("Tests that right methods in doGet are called when getting all products from db")
    void doGetAllTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products");
        List<Product> productList = new ArrayList<>(List.of(product));
        when(mockedProductService.findAll()).thenReturn(productList);
        String responseJson = jsonConverter.toJson(productList);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        productServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService).findAll();
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).setContentType("application/json; charset=UTF-8");
        inOrderResponse.verify(mockedResponse).getWriter();
        verify(mockedPrintWriter).write(eq(responseJson));
    }

    @Test
    @DisplayName("Tests that right methods in doGet are called when getting by existing id")
    void doGetByExistingIdTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/1");
        when(mockedProductService.findById(1)).thenReturn(Optional.of(product));
        String responseJson = gson.toJson(product);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        productServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService).findById(1);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).setContentType("application/json; charset=UTF-8");
        inOrderResponse.verify(mockedResponse).getWriter();
        verify(mockedPrintWriter).write(eq(responseJson));
    }

    @Test
    @DisplayName("Tests that right methods in doGet are called when getting by not existing id")
    void doGetByNotExistingIdTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/6");
        when(mockedProductService.findById(6)).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        productServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService).findById(6);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        inOrderResponse.verify(mockedResponse).setContentType("application/json; charset=UTF-8");
        inOrderResponse.verify(mockedResponse).getWriter();
        verify(mockedPrintWriter).write(eq(""));
    }

    @Test
    @DisplayName("Tests that right methods in doGet are called when uri is invalid")
    void doGetInvalidUriTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/six");
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        productServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService, never()).findById(6);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).setContentType("application/json; charset=UTF-8");
        inOrderResponse.verify(mockedResponse).getWriter();
        verify(mockedPrintWriter).write(eq(""));
    }

    @Test
    @DisplayName("Tests that right methods in doPost are called when product is added successfully")
    void doPostTest() throws IOException {
        //prepare
        StringReader reader = new StringReader(gson.toJson(product));
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(reader));
        when(mockedProductService.add(eq(product))).thenReturn(true);
        //when
        productServlet.doPost(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getReader();
        verify(mockedProductService).add(eq(product));
        verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Tests that right methods in doPost are called when product is added successfully")
    void doPostFailedToAddTest() throws IOException {
        //prepare
        StringReader reader = new StringReader(gson.toJson(product));
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(reader));
        when(mockedProductService.add(eq(product))).thenReturn(false);
        //when
        productServlet.doPost(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getReader();
        verify(mockedProductService).add(eq(product));
        verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Tests that right methods in doPut are called when uri is invalid")
    void doPutInvalidUriTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/six");
        StringReader reader = new StringReader("");
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(reader));
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        productServlet.doPut(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getReader();
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService, never()).findById(6);
        verify(mockedProductService, never()).update(any());
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Tests that right methods in doPut are called when json doesn't have neither name nor price key")
    void doPutNoNameNoPriceJsonTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/1");
        StringReader reader = new StringReader("{}");
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(reader));
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        productServlet.doPut(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getReader();
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService, never()).findById(1);
        verify(mockedProductService, never()).update(any());
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Tests that right methods in doPut are called when product with that id doesn't exist in db")
    void doPutNotExistingIdTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/6");
        StringReader reader = new StringReader("{\"name\":\"products1Uda\"}");
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(reader));
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        when(mockedProductService.findById(1)).thenReturn(Optional.empty());
        //when
        productServlet.doPut(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getReader();
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService).findById(6);
        verify(mockedProductService, never()).update(any());
        verify(mockedResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Tests that right methods in doPut are called when product exists in db, but wasn't updated")
    void doPutExistingIdNotUpdatedTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/1");
        StringReader reader = new StringReader("{\"name\":\"products1Uda\"}");
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(reader));
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        when(mockedProductService.findById(1)).thenReturn(Optional.of(product));
        Product updatedProduct = new Product();
        updatedProduct.setName("products1Uda");
        when(mockedProductService.update(eq(updatedProduct))).thenReturn(false);
        //when
        productServlet.doPut(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getReader();
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService).findById(1);
        verify(mockedProductService).update(eq(updatedProduct));
        verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Tests that right methods in doPut are called product is successfully updated")
    void doPutTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/1");
        StringReader reader = new StringReader("{\"name\":\"products1Uda\"}");
        when(mockedRequest.getReader()).thenReturn(new BufferedReader(reader));
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        when(mockedProductService.findById(1)).thenReturn(Optional.of(product));
        Product updatedProduct = new Product();
        updatedProduct.setName("products1Uda");
        when(mockedProductService.update(eq(updatedProduct))).thenReturn(true);
        //when
        productServlet.doPut(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getReader();
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService).findById(1);
        verify(mockedProductService).update(eq(updatedProduct));
        verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Tests that right methods in doDelete are called when uri is invalid")
    void doDeleteInvalidUriTest() {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/six");
        //when
        productServlet.doDelete(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getRequestURI();
        verify(mockedProductService, never()).delete(6);
        verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Tests that right methods in doDelete are called when product with that id does not exist in db")
    void doDeleteNotExistingIdTest() {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/6");
        when(mockedProductService.delete(6)).thenReturn(false);
        //when
        productServlet.doDelete(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getRequestURI();
        verify(mockedProductService).delete(6);
        verify(mockedResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Tests that right methods in doDelete are called when product with that id does exist in db and gets deleted")
    void doDeleteTest() {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products/1");
        when(mockedProductService.delete(1)).thenReturn(true);
        //when
        productServlet.doDelete(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getRequestURI();
        verify(mockedProductService).delete(1);
        verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
    }

}