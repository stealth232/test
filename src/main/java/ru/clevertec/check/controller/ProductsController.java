package ru.clevertec.check.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.check.model.product.Product;
import ru.clevertec.check.service.ControllerService;
import ru.clevertec.check.service.DataOrderService;
import ru.clevertec.check.service.ProductService;
import ru.clevertec.check.service.WarehouseService;
import ru.clevertec.check.validators.ProductValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static ru.clevertec.check.service.CheckConstants.NO_PARAMS;

@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/admin/products", produces = "application/json")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductService productService;
    private final WarehouseService ws;
    private final ControllerService cs;
    private final DataOrderService dos;
    private final ProductValidator pv;

    @GetMapping
    public ResponseEntity<?> getProducts() {
        List<Product> products = productService.findAll();
        return new ResponseEntity<>(products, cs.generateHttpStatusForView(products));
    }

    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody @Valid Product product, BindingResult bindingResult) {
        pv.validate(product, bindingResult);
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.NOT_ACCEPTABLE);
        }
        Product savedProduct = productService.save(product);
        return Objects.nonNull(savedProduct) ?
                new ResponseEntity<>(savedProduct, cs.generateHttpStatusForSave(savedProduct)) :
                new ResponseEntity<>(NO_PARAMS, cs.generateHttpStatusForSave(savedProduct));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> changeStock(@PathVariable Integer id) {
        return new ResponseEntity<>(cs.generateHttpStatus(productService.changeStockById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        return new ResponseEntity<>(cs.generateHttpStatusForDeletion(productService.deleteProductById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> setCost(@RequestParam("cost") double cost,
                                     @PathVariable Integer id) {
        return new ResponseEntity<>(cs.generateHttpStatus(productService.updateCost(cost, id)));
    }

    @PutMapping("/warehouse/{id}")
    public ResponseEntity<?> setProductsQuantity(@RequestParam("quantity") Integer quantity,
                                                 @PathVariable Integer id) {
        Integer update = ws.updateQuantityById(quantity, id);
        return new ResponseEntity<>(ws.updateQuantityById(quantity, id), cs.generateHttpStatus(update));
    }

    @GetMapping("/warehouse")
    public ResponseEntity<?> getWarehouse() {
        return new ResponseEntity<>(ws.findAll(), cs.generateHttpStatusForView(ws.findAll()));
    }
}
