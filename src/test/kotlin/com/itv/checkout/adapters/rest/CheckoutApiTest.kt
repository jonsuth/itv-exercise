package com.itv.checkout.adapters.rest

import com.itv.checkout.domain.model.CartId
import com.itv.checkout.domain.model.exceptions.CartNotFoundException
import com.itv.checkout.domain.model.exceptions.ProductNotFoundException
import com.itv.checkout.domain.services.CheckoutService
import com.itv.checkout.domain.services.ProductsService
import com.itv.checkout.testsupport.emptyCart
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class CheckoutApiTest {

    private val checkoutService = mockk<CheckoutService>()
    private val productsService = mockk<ProductsService>()
    private val checkoutApi = CheckoutApi(checkoutService, productsService).endpoints()

    @Test
    fun `should return 200 for load products request`() {
        //given
        every { productsService.loadProduct(any()) } just runs
        val request = Request(POST, "/products")
                .body("""
                    [
                      {
                        "sku": "123",
                        "unitPrice": 12
                      },
                      {
                        "sku": "123",
                        "unitPrice": 12,
                        "specialPrice": {
                          "price": 10,
                          "requiredQuantity": 3
                        }
                      }
                    ]
                """.trimIndent())

        //when
        val response = checkoutApi(request)

        //then
        expectThat(response) {
            get { status } isEqualTo OK
        }
    }

    @Test
    fun `should return 400 for invalid load products request`() {
        //given
        val request = Request(POST, "/products")
                .body("""{"sku": "123"}""")

        //when
        val response = checkoutApi(request)

        //then
        expectThat(response) {
            get { status } isEqualTo BAD_REQUEST
        }
    }

    @Test
    fun `should return 201 for create cart request`() {
        //given
        every { checkoutService.createCart() } returns emptyCart
        val request = Request(POST, "/carts")

        //when
        val response = checkoutApi(request)

        //then
        expectThat(response) {
            get { status } isEqualTo CREATED
        }
    }

    @Test
    fun `should return 200 for update cart request`() {
        //given
        every { checkoutService.addLineItem(CartId("cart1"), any()) } returns emptyCart
        val request = Request(POST, "/carts/cart1")
                .body("""{"sku":"A"}""")

        //when
        val response = checkoutApi(request)

        //then
        expectThat(response) {
            get { status } isEqualTo OK
        }
    }

    @Test
    fun `should return 400 for invalid update cart request`() {
        //given
        val request = Request(POST, "/carts/cart1")
                .body("""{"cartId":"123"}""")

        //when
        val response = checkoutApi(request)

        //then
        expectThat(response) {
            get { status } isEqualTo BAD_REQUEST
        }
    }

    @Test
    fun `should return 404 when cart does not exist`() {
        //given
        every { checkoutService.addLineItem(CartId("cart1"), any()) } throws CartNotFoundException
        val request = Request(POST, "/carts/cart1")
                .body("""{"sku":"A"}""")

        //when
        val response = checkoutApi(request)

        //then
        expectThat(response) {
            get { status } isEqualTo NOT_FOUND
        }
    }

    @Test
    fun `should return 404 when product does not exist`() {
        //given
        every { checkoutService.addLineItem(CartId("cart1"), any()) } throws ProductNotFoundException
        val request = Request(POST, "/carts/cart1")
                .body("""{"sku":"A"}""")

        //when
        val response = checkoutApi(request)

        //then
        expectThat(response) {
            get { status } isEqualTo NOT_FOUND
        }
    }
}
