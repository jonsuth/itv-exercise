package com.itv.checkout.domain.services

import com.itv.checkout.domain.model.Product

class ProductsService(private val productRepository: ProductRepository) {

    fun loadProduct(productsRequest: List<Product>) = productsRequest.forEach { productRepository.update(it.sku, it) }
}
