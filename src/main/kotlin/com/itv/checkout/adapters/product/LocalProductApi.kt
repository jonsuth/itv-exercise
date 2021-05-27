package com.itv.checkout.adapters.product

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.itv.checkout.domain.model.Product
import com.itv.checkout.domain.model.SkuId
import com.itv.checkout.domain.model.exceptions.ProductNotFoundException
import com.itv.checkout.domain.services.ProductRepository

class LocalProductApi(private val cache: Cache<SkuId, Product> = Caffeine.newBuilder().build()) : ProductRepository {

    override fun get(skuId: SkuId): Product {
        return cache.getIfPresent(skuId) ?: throw ProductNotFoundException
    }

    override fun update(skuId: SkuId, product: Product) {
        cache.put(skuId, product)
    }
}
