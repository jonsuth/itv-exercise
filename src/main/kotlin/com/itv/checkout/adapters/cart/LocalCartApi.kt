package com.itv.checkout.adapters.cart

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.itv.checkout.domain.model.Cart
import com.itv.checkout.domain.model.CartId
import com.itv.checkout.domain.model.exceptions.CartNotFoundException
import com.itv.checkout.domain.services.CartRepository

class LocalCartApi(private val cache: Cache<CartId, Cart> = Caffeine.newBuilder().build()) : CartRepository {

    override fun get(cartId: CartId): Cart {
        return cache.getIfPresent(cartId) ?: throw CartNotFoundException
    }

    override fun update(cartId: CartId, cart: Cart) {
        cache.put(cartId, cart)
    }
}
