package com.itv.checkout.domain.services

import com.itv.checkout.domain.model.Product
import com.itv.checkout.domain.model.SkuId

interface ProductRepository {
    fun get(skuId: SkuId): Product
    fun update(skuId: SkuId, product: Product)
}
