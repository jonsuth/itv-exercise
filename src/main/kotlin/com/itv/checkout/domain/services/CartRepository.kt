package com.itv.checkout.domain.services

import com.itv.checkout.domain.model.Cart
import com.itv.checkout.domain.model.CartId

interface CartRepository {
    fun get(cartId: CartId): Cart
    fun update(cartId: CartId, cart: Cart)
}
