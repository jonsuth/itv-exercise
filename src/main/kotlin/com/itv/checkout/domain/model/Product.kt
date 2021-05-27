package com.itv.checkout.domain.model

data class Product(val sku: SkuId, val unitPrice: Int, val specialPrice: SpecialPrice? = null)

data class SpecialPrice(val price: Int, val requiredQuantity: Int)

