package id.ac.ui.cs.advprog.eshop.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import org.mockito.Mock;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService service;

    @Mock
    private Model model;

    private ProductController controller;

    @BeforeEach
    void setUp() throws Exception {
        controller = new ProductController();
        Field f = ProductController.class.getDeclaredField("service");
        f.setAccessible(true);
        f.set(controller, service);
    }

    @Test
    void createProductPageAddsEmptyProductToModel() {
        String view = controller.createProductPage(model);

        assertEquals("CreateProduct", view);
        verify(model).addAttribute(eq("product"), any(Product.class));
        verifyNoInteractions(service);
    }

    @Test
    void createProductWithNonIntegerQuantityReturnsCreateProductWithErrors() {
        String view = controller.createProductPost("Phone", "abc", model);

        assertEquals("CreateProduct", view);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(model).addAttribute(eq("product"), productCaptor.capture());
        verify(model).addAttribute("productQuantityRaw", "abc");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> errorsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(model).addAttribute(eq("errors"), errorsCaptor.capture());

        assertEquals("Phone", productCaptor.getValue().getProductName());
        assertEquals("Quantity must be an integer", errorsCaptor.getValue().get("productQuantity"));
        verify(service, never()).create(any(Product.class));
    }

    @Test
    void createProductWithNonPositiveQuantityReturnsCreateProductWithErrors() {
        String view = controller.createProductPost("Phone", "0", model);

        assertEquals("CreateProduct", view);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> errorsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(model).addAttribute(eq("errors"), errorsCaptor.capture());
        assertEquals("Quantity must be positive", errorsCaptor.getValue().get("productQuantity"));

        verify(service, never()).create(any(Product.class));
    }

    @Test
    void createProductWithValidInputsPersistsProductAndRedirects() {
        when(service.create(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String view = controller.createProductPost("Phone", "7", model);

        assertEquals("redirect:list", view);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(service).create(productCaptor.capture());
        assertEquals("Phone", productCaptor.getValue().getProductName());
        assertEquals(7, productCaptor.getValue().getProductQuantity());
    }

    @Test
    void createProductWhenServiceReturnsNullShowsCreateProductWithInvalidQuantityError() {
        when(service.create(any(Product.class))).thenReturn(null);

        String view = controller.createProductPost("Phone", "7", model);

        assertEquals("CreateProduct", view);
        verify(service).create(any(Product.class));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(model).addAttribute(eq("product"), productCaptor.capture());
        verify(model).addAttribute("productQuantityRaw", "7");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> errorsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(model).addAttribute(eq("errors"), errorsCaptor.capture());

        assertEquals("Phone", productCaptor.getValue().getProductName());
        assertEquals("Invalid quantity", errorsCaptor.getValue().get("productQuantity"));
    }

    @Test
    void editProductPageWithInvalidIdReturnsEditProduct() {
        String view = controller.editProductPage("not-a-uuid", model);

        assertEquals("EditProduct", view);
        verify(service).findById("not-a-uuid");
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(model).addAttribute(eq("product"), productCaptor.capture());
        assertNull(productCaptor.getValue());
    }

    @Test
    void editProductPageWithMissingProductAddsNullProduct() {
        String id = UUID.randomUUID().toString();
        when(service.findById(id)).thenReturn(null);

        String view = controller.editProductPage(id, model);

        assertEquals("EditProduct", view);
        verify(service).findById(id);
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(model).addAttribute(eq("product"), productCaptor.capture());
        assertNull(productCaptor.getValue());
    }

    @Test
    void editProductPageWithValidProductReturnsEditProduct() {
        String id = UUID.randomUUID().toString();
        Product existing = new Product();
        existing.setProductId(id);
        when(service.findById(id)).thenReturn(existing);

        String view = controller.editProductPage(id, model);

        assertEquals("EditProduct", view);
        verify(service).findById(id);
        verify(model).addAttribute("product", existing);
    }

    @Test
    void editProductWithNonExistentIdReturnsEditProduct() {
        when(service.update(eq("not-a-uuid"), any(Product.class))).thenReturn(null);

        String view = controller.editProductPost("not-a-uuid", "Laptop", "10", model);

        assertEquals("EditProduct", view);
        verify(service).update(eq("not-a-uuid"), any(Product.class));
    }

    @Test
    void editProductWhenUpdateReturnsNullShowsEditProduct() {
        String id = UUID.randomUUID().toString();
        when(service.update(eq(id), any(Product.class))).thenReturn(null);

        String view = controller.editProductPost(id, "Laptop", "10", model);

        assertEquals("EditProduct", view);
        verify(service).update(eq(id), any(Product.class));
    }

    @Test
    void editProductWithInvalidQuantityReturnsEditProductWithErrors() {
        String id = UUID.randomUUID().toString();

        String view = controller.editProductPost(id, "Laptop", "nope", model);

        assertEquals("EditProduct", view);
        verify(service, never()).update(anyString(), any(Product.class));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(model).addAttribute(eq("product"), productCaptor.capture());
        verify(model).addAttribute("productQuantityRaw", "nope");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> errorsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(model).addAttribute(eq("errors"), errorsCaptor.capture());

        assertEquals(id, productCaptor.getValue().getProductId());
        assertEquals("Laptop", productCaptor.getValue().getProductName());
        assertEquals("Quantity must be an integer", errorsCaptor.getValue().get("productQuantity"));
    }

    @Test
    void editProductWithValidInputsUpdatesProductAndRedirects() {
        String id = UUID.randomUUID().toString();
        Product existing = new Product();
        existing.setProductId(id);
        existing.setProductName("Old");
        existing.setProductQuantity(1);
        when(service.update(eq(id), any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(1);
            existing.setProductName(p.getProductName());
            existing.setProductQuantity(p.getProductQuantity());
            return existing;
        });

        String view = controller.editProductPost(id, "Laptop", "10", model);

        assertEquals("redirect:list", view);
        verify(service).update(eq(id), any(Product.class));
        assertEquals("Laptop", existing.getProductName());
        assertEquals(10, existing.getProductQuantity());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void deleteProductWithNullOrEmptyIdDoesNothing(String id) {
        String view = controller.deleteProduct(id);

        assertEquals("redirect:list", view);
        verifyNoInteractions(service);
    }

    @Test
    void deleteProductWithAnyStringIdCallsService() {
        String id = "any-string-id";

        String view = controller.deleteProduct(id);

        assertEquals("redirect:list", view);
        verify(service).deleteProductById(id);
    }

    @Test
    void deleteProductWithExistingProductDeletesAndRedirects() {
        String id = UUID.randomUUID().toString();
        when(service.deleteProductById(id)).thenReturn(new Product());

        String view = controller.deleteProduct(id);

        assertEquals("redirect:list", view);
        verify(service).deleteProductById(id);
    }

    @Test
    void productListPageAddsProductsAndReturnsProductListView() {
        List<Product> products = List.of(new Product(), new Product());
        when(service.findAll()).thenReturn(products);

        String view = controller.productListPage(model);

        assertEquals("ProductList", view);
        verify(service).findAll();
        verify(model).addAttribute("products", products);
    }
}
