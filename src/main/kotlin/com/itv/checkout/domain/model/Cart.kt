package com.itv.checkout.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING
import com.fasterxml.jackson.annotation.JsonValue

data class CartId @JsonCreator(mode = DELEGATING) constructor(@get:JsonValue val value: String)

data class Cart(val id: CartId, val lineItems: MutableList<LineItem>, val totalPrice: Int)

data class LineItem(val sku: SkuId, val quantity: Int, val unitPrice: Int, val totalPrice: Int)

data class SkuId @JsonCreator(mode = DELEGATING) constructor(@get:JsonValue val value: String)
