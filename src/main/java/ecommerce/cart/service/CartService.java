package ecommerce.cart.service;

import ecommerce.cart.model.Cart;
import ecommerce.cart.model.CartItem;
import ecommerce.cart.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import java.util.NoSuchElementException;


@Service
public class CartService {

    private final JmsTemplate jmsTemplate;
    private final CartRepository cartRepository;

    public CartService(JmsTemplate jmsTemplate, CartRepository cartRepository) {
        this.jmsTemplate = jmsTemplate;
        this.cartRepository = cartRepository;
    }

    public Cart getCartByCartId(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found with id: " + cartId));
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }
    public Cart addToCart(Long cartId, CartItem item) {
        Cart cart = getCartByCartId(cartId);
        if (cart != null) {
            // send productId to the queue
            jmsTemplate.convertAndSend("ProductValidationQueue", item.getProductId());

            // receive response (this will block until a message is received)
            Boolean productExists = (Boolean) jmsTemplate.receiveAndConvert("ProductValidationResponseQueue");

            if (productExists != null && productExists) {
                cart.getItems().add(item);
            } else {
                throw new NoSuchElementException("Product not found in catalog");
            }
            return saveCart(cart);
        }
        return null;
    }

    public Cart createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    public void deleteCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}
