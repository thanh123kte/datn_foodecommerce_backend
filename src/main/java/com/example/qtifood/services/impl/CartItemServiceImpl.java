package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.CartItems.CreateCartItemDto;
import com.example.qtifood.dtos.CartItems.UpdateCartItemDto;
import com.example.qtifood.dtos.CartItems.CartItemResponseDto;
import com.example.qtifood.dtos.CartItems.CartSummaryDto;
import com.example.qtifood.entities.CartItem;
import com.example.qtifood.entities.Product;
import com.example.qtifood.entities.User;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.mappers.CartItemMapper;
import com.example.qtifood.repositories.CartItemRepository;
import com.example.qtifood.repositories.ProductRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.services.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Override
    public CartItemResponseDto addToCart(String customerId, CreateCartItemDto dto) {
        // Validate customer exists
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // Validate product exists and is available
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));

        // Check if item already exists in cart
        Optional<CartItem> existingCartItem = cartItemRepository
                .findByCustomerIdAndProductId(customerId, dto.getProductId());

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            // Update quantity if item already exists
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + dto.getQuantity());
            cartItem.setNote(dto.getNote());
        } else {
            // Create new cart item
            cartItem = CartItem.builder()
                    .customer(customer)
                    .store(product.getStore())
                    .product(product)
                    .quantity(dto.getQuantity())
                    .note(dto.getNote())
                    .build();
        }

        cartItem = cartItemRepository.save(cartItem);
        return CartItemMapper.toDto(cartItem);
    }

    @Override
    public CartItemResponseDto updateCartItem(String customerId, Long cartItemId, UpdateCartItemDto dto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Verify the cart item belongs to the customer
        if (!cartItem.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Cart item not found for this customer");
        }

        cartItem.setQuantity(dto.getQuantity());
        cartItem.setNote(dto.getNote());

        cartItem = cartItemRepository.save(cartItem);
        return CartItemMapper.toDto(cartItem);
    }

    @Override
    public void removeFromCart(String customerId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Verify the cart item belongs to the customer
        if (!cartItem.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Cart item not found for this customer");
        }

        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(String customerId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        cartItemRepository.deleteByCustomerId(customerId);
    }

    @Override
    public void clearCartByStore(String customerId, Long storeId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        // Validate store exists
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Store not found with id: " + storeId);
        }

        cartItemRepository.deleteByCustomerIdAndStoreId(customerId, storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponseDto> getCartItems(String customerId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        List<CartItem> cartItems = cartItemRepository.findByCustomerIdWithDetails(customerId);
        return CartItemMapper.toDtoList(cartItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponseDto> getCartItemsByStore(String customerId, Long storeId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        // Validate store exists
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Store not found with id: " + storeId);
        }

        List<CartItem> cartItems = cartItemRepository.findByCustomerIdAndStoreId(customerId, storeId);
        return CartItemMapper.toDtoList(cartItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartSummaryDto> getCartSummary(String customerId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        List<CartItem> cartItems = cartItemRepository.findByCustomerIdWithDetails(customerId);
        return CartItemMapper.toCartSummaryList(cartItems);
    }

    @Override
    @Transactional(readOnly = true)
    public CartItemResponseDto getCartItem(String customerId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Verify the cart item belongs to the customer
        if (!cartItem.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Cart item not found for this customer");
        }

        return CartItemMapper.toDto(cartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCartItemsCount(String customerId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        return cartItemRepository.countByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInCart(String customerId, Long productId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        return cartItemRepository.existsByCustomerIdAndProductId(customerId, productId);
    }
}