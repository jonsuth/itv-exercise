package com.itv.checkout.adapters.cart

import com.github.benmanes.caffeine.cache.Cache
import com.itv.checkout.domain.model.Cart
import com.itv.checkout.domain.model.CartId
import com.itv.checkout.domain.model.exceptions.CartNotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThrows

internal class LocalCartApiTest {
    private val cache = mockk<Cache<CartId, Cart>>()
    private val localCartApi = LocalCartApi(cache)

    @Test
    fun `should throw exception if cart does not exist`() {
        //given
        every { cache.getIfPresent(CartId("123")) } returns null

        //when and then
        expectThrows<CartNotFoundException> { localCartApi.get(CartId("123")) }
    }
}
