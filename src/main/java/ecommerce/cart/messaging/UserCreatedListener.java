package ecommerce.cart.messaging;

import ecommerce.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedListener {

    private final CartService cartService;

    @Autowired
    public UserCreatedListener(CartService cartService) {
        this.cartService = cartService;
    }

    @JmsListener(destination = "userCreatedQueue")
    public void handleUserCreated(Long userId) {
        // Create a new cart for the user
        cartService.createCart(userId);
    }
}
