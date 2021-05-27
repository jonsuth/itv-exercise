package com.itv.checkout.domain.services

import com.itv.checkout.domain.model.Cart
import com.itv.checkout.domain.model.CartId
import com.itv.checkout.domain.model.requests.AddLineItemRequest
import java.util.UUID

class CheckoutService(private val cartRepository: CartRepository,
                      private val cartModifierService: CartModifierService,
                      private val cartIdGenerator: () -> CartId = { CartId(UUID.randomUUID().toString()) }) {

    fun createCart(): Cart {
        return Cart(id = cartIdGenerator.invoke(), lineItems = mutableListOf(), totalPrice = 0)
                .also { cartRepository.update(it.id, it) }
    }

    fun addLineItem(cartId: CartId, request: AddLineItemRequest): Cart {
        val cart = cartRepository.get(cartId)
        val itemAlreadyInCart = cart.lineItems.find { it.sku == request.sku } != null

        val updatedCart = when (itemAlreadyInCart) {
            true -> cartModifierService.update(request.sku, cart)
            false -> cartModifierService.add(request.sku, cart)
        }

        cartRepository.update(cartId, updatedCart)

        return updatedCart
    }
}
