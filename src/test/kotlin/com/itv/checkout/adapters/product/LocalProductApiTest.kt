package com.itv.checkout.adapters.product

import com.github.benmanes.caffeine.cache.Cache
import com.itv.checkout.domain.model.Product
import com.itv.checkout.domain.model.SkuId
import com.itv.checkout.domain.model.exceptions.ProductNotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThrows

internal class LocalProductApiTest {
    private val cache = mockk<Cache<SkuId, Product>>()
    private val localProductApi = LocalProductApi(cache)

    @Test
    fun `should throw exception if cart does not exist`() {
        //given
        every { cache.getIfPresent(SkuId("A")) } returns null

        //when and then
        expectThrows<ProductNotFoundException> { localProductApi.get(SkuId("A")) }
    }
}
