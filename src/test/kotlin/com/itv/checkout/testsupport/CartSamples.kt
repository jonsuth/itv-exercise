package com.itv.checkout.testsupport

import com.itv.checkout.domain.model.Cart
import com.itv.checkout.domain.model.CartId

val emptyCart = Cart(CartId("123"), mutableListOf(), 0)
