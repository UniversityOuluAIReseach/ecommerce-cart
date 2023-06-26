package ecommerce.cart.controller;

import ecommerce.cart.model.Cart;
import ecommerce.cart.model.CartItem;
import ecommerce.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @PostMapping
    public Cart createCart(@RequestBody Cart cart) {
        return cartService.saveCart(cart);
    }

    @GetMapping("/{cartId}")
    public Cart getCart(@PathVariable Long cartId) {
        return cartService.getCartByCartId(cartId);
    }

    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
    }

    // Retrieve items in a cart
    @GetMapping("/{cartId}/items")
    public List<CartItem> getItemsInCart(@PathVariable Long cartId) {
        return cartService.getCartByCartId(cartId).getItems();
    }

    // Add a new item to a cart
    @PostMapping("/{cartId}/items")
    public Cart addItem(@PathVariable Long cartId, @RequestBody CartItem item) {
        return cartService.addToCart(cartId, item);
    }

    // Update the quantity of an item in a cart
    @PutMapping("/{cartId}/items/{itemId}")
    public Cart updateItemQuantity(@PathVariable Long cartId, @PathVariable Long itemId, @RequestParam int quantity) {
        Cart cart = cartService.getCartByCartId(cartId);
        if (cart != null) {
            cart.getItems().stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(quantity));
            return cartService.saveCart(cart);
        }
        return null;
    }

    // Remove an item from a cart
    @DeleteMapping("/{cartId}/items/{itemId}")
    public Cart deleteItem(@PathVariable Long cartId, @PathVariable Long itemId) {
        Cart cart = cartService.getCartByCartId(cartId);
        if (cart != null) {
            cart.getItems().removeIf(item -> item.getId().equals(itemId));
            return cartService.saveCart(cart);
        }
        return null;
    }


}
