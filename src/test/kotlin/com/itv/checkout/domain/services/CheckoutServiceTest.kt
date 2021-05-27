package com.itv.checkout.domain.services

import com.itv.checkout.adapters.cart.LocalCartApi
import com.itv.checkout.domain.model.CartId
import com.itv.checkout.domain.model.LineItem
import com.itv.checkout.domain.model.SkuId
import com.itv.checkout.domain.model.requests.AddLineItemRequest
import com.itv.checkout.testsupport.emptyCart
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class CheckoutServiceTest {

    private val localCartApi = mockk<LocalCartApi>()
    private val cartModifierService = mockk<CartModifierService>()
    private val checkoutService = CheckoutService(localCartApi, cartModifierService)

    @Test
    fun `should create a new cart`() {
        //when
        val checkoutService = CheckoutService(localCartApi, cartModifierService) { CartId("123") }
        every { localCartApi.update(CartId("123"), any()) } just runs
        val cart = checkoutService.createCart()

        //then
        verify { localCartApi.update(CartId("123"), cart) }
    }

    @Test
    fun `should add item to empty cart`() {
        //given
        val request = AddLineItemRequest(SkuId("B"))

        every { localCartApi.get(any()) } returns emptyCart
        every { cartModifierService.add(any(), any()) } returns emptyCart

        //when
        checkoutService.addLineItem(CartId("123"), request)

        //then
        verify {
            localCartApi.get(CartId("123"))
            cartModifierService.add(SkuId("B"), emptyCart)
        }
    }

    @Test
    fun `should update item on cart`() {
        //given
        val cartWithOneItem = emptyCart.copy(lineItems = mutableListOf(LineItem(SkuId("A"), 2, 200, 400)))
        val request = AddLineItemRequest(SkuId("A"))

        every { localCartApi.get(any()) } returns cartWithOneItem
        every { cartModifierService.update(any(), any()) } returns emptyCart
        //when
        checkoutService.addLineItem(CartId("123"), request)

        //then
        verify {
            localCartApi.get(CartId("123"))
            cartModifierService.update(SkuId("A"), cartWithOneItem)
        }
    }
}
