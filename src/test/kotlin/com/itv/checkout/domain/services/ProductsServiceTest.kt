package com.itv.checkout.domain.services

import com.itv.checkout.domain.model.Product
import com.itv.checkout.domain.model.SkuId
import com.itv.checkout.domain.model.SpecialPrice
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class ProductsServiceTest {
    private val productRepository = mockk<ProductRepository> {
        every { update(any(), any()) } just runs
    }
    private val productsService = ProductsService(productRepository)

    @Test
    fun `should load products`() {
        //given
        val productsRequest = listOf(Product(SkuId("A"), 200), Product(SkuId("B"), 150, SpecialPrice(100, 3)))

        //when
        productsService.loadProduct(productsRequest)

        //then
        verify {
            productRepository.update(SkuId("A"), Product(SkuId("A"), 200))
            productRepository.update(SkuId("B"), Product(SkuId("B"), 150, SpecialPrice(100, 3)))
        }
    }
}
