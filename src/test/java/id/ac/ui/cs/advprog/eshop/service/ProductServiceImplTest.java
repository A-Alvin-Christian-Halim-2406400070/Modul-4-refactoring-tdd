package id.ac.ui.cs.advprog.eshop.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.RepositoryInterface;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    RepositoryInterface<Product, String> productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    Product sample;

    @BeforeEach
    void setUp() {
        sample = new Product();
        sample.setProductId(UUID.randomUUID().toString());
        sample.setProductName("Sample");
        sample.setProductQuantity(1);
    }

    @Test
    void testCreateForwardsToRepositoryAndReturnsProduct() {
        when(productRepository.create(sample)).thenReturn(sample);
        Product result = productService.create(sample);
        assertEquals(sample, result);
        verify(productRepository).create(sample);
    }

    @Test
    void testFindAllConvertsIteratorToList() {
        List<Product> products = new ArrayList<>();
        Product p2 = new Product();
        p2.setProductId(UUID.randomUUID().toString());
        p2.setProductName("P2");
        p2.setProductQuantity(2);
        products.add(sample);
        products.add(p2);
        Iterator<Product> it = products.iterator();
        when(productRepository.findAll()).thenReturn(it);
        List<Product> result = productService.findAll();
        assertEquals(2, result.size());
        assertEquals(sample.getProductId(), result.get(0).getProductId());
        assertEquals(p2.getProductId(), result.get(1).getProductId());
    }

    @Test
    void testFindAllWhenEmpty() {
        List<Product> products = new ArrayList<>();
        Iterator<Product> it = products.iterator();
        when(productRepository.findAll()).thenReturn(it);
        List<Product> result = productService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testConstructor() {
        @SuppressWarnings("unchecked")
        RepositoryInterface<Product, String> mockRepo = mock(RepositoryInterface.class);
        ProductServiceImpl svc = new ProductServiceImpl(mockRepo);
        when(mockRepo.findAll()).thenReturn(new ArrayList<Product>().iterator());
        List<Product> result = svc.findAll();
        assertNotNull(result);
        verify(mockRepo).findAll();
    }

    @Test
    void testCreateWithNullProductReturnsNull() {
        Product result = productService.create(null);
        assertNull(result);
    }

    @Test
    void testCreateWithNonPositiveQuantityReturnsNull() {
        Product invalid = new Product();
        invalid.setProductId(UUID.randomUUID().toString());
        invalid.setProductName("Invalid");
        invalid.setProductQuantity(0);

        Product result = productService.create(invalid);
        assertNull(result);
    }

    @Test
    void testDeleteProductByIdWithNullIdReturnsNull() {
        assertNull(productService.deleteProductById(null));
    }

    @Test
    void testDeleteProductByIdWhenProductNotFoundReturnsNull() {
        when(productRepository.findById("missing")).thenReturn(null);
        assertNull(productService.deleteProductById("missing"));
    }

    @Test
    void testFindByIdWithNullIdReturnsNull() {
        assertNull(productService.findById(null));
    }

    @Test
    void testUpdateWithNullIdReturnsNull() {
        Product p = new Product();
        p.setProductId(UUID.randomUUID().toString());
        p.setProductName("Sample");
        p.setProductQuantity(1);

        assertNull(productService.update(null, p));
    }

    @Test
    void testUpdateWithNullProductReturnsNull() {
        assertNull(productService.update("id", null));
    }

}
