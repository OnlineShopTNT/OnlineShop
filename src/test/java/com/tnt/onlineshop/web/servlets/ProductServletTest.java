package com.tnt.onlineshop.web.servlets;

import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.json.JsonConverter;
import com.tnt.onlineshop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServletTest {

    private final ProductService mockedProductService = mock(ProductService.class);
    private final ProductServlet productServlet = new ProductServlet(mockedProductService);
    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final InOrder inOrderResponse = inOrder(mockedResponse);
    private final InOrder inOrderRequest = inOrder(mockedRequest);
    private final Product mockedProduct = mock(Product.class);
    private final JsonConverter mockedJsonConverter = mock(JsonConverter.class);
    private final PrintWriter mockedPrintWriter = mock(PrintWriter.class);
    private List<Product> productList = new ArrayList<>();
    private StringBuilder stringBuilder = mock(StringBuilder.class);

    @BeforeAll
    void beforeAll() {
        for (int i = 0; i < 3; i++) {
            Product product = new Product();
            product.setId(i);
            product.setName("product" + i);
            product.setPrice(i * 2 + 10);
            product.setLastModifiedTime(Timestamp.from(Instant.now()));
            productList.add(product);
        }
    }

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedProduct);
        reset(mockedPrintWriter);
    }

    @Test
    @DisplayName("Get all products from db")
    void doGetAllProductsTest() throws IOException {
        //prepare
        when(mockedRequest.getRequestURI()).thenReturn("/products");
        when(mockedProductService.findAll()).thenReturn(productList);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        productServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getRequestURI();
        verify(mockedProductService).findAll();
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).setContentType("application/json; charset=UTF-8");
        inOrderResponse.verify(mockedResponse).getWriter();
        verify(mockedPrintWriter).write("[{\"lastModifiedTime\":\"2012-12-02 06:12:08.0\",\"price\":13,\"name\":\"product2\",\"id\":2},{\"lastModifiedTime\":\"2021-05-21 15:09:56.291\",\"price\":700,\"name\":\"prod1\",\"id\":4},{\"lastModifiedTime\":\"2021-05-21 15:20:41.277\",\"price\":700,\"name\":\"prodN\",\"id\":5}]");
    }

}