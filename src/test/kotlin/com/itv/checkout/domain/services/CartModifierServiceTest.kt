package com.itv.checkout.domain.services

import com.itv.checkout.domain.model.LineItem
import com.itv.checkout.domain.model.Product
import com.itv.checkout.domain.model.SkuId
import com.itv.checkout.domain.model.SpecialPrice
import com.itv.checkout.testsupport.emptyCart
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.withElementAt

internal class CartModifierServiceTest {

    private val productRepository = mockk<ProductRepository>()
    private val cartModifierService = CartModifierService(productRepository)

    @Test
    fun `should add new line item on empty cart`() {
        //give
        val skuIdToAdd = SkuId("A")
        every { productRepository.get(skuIdToAdd) } returns Product(skuIdToAdd, 50)

        //when
        val updatedCart = cartModifierService.add(skuIdToAdd, emptyCart)

        //then
        expectThat(updatedCart) {
            get { totalPrice } isEqualTo 50
            get { lineItems } hasSize 1 and {
                withElementAt(0) {
                    get { sku.value } isEqualTo "A"
                    get { quantity } isEqualTo 1
                    get { unitPrice } isEqualTo 50
                }
            }
        }
    }

    @Test
    fun `should add new line item on cart with existing line items`() {
        //give
        val cartWithOneItem = emptyCart.copy(lineItems = mutableListOf(LineItem(SkuId("A"), 1, 50, 50)))
        val skuIdToAdd = SkuId("B")
        every { productRepository.get(skuIdToAdd) } returns Product(skuIdToAdd, 30)

        //when
        val updatedCart = cartModifierService.add(skuIdToAdd, cartWithOneItem)

        //then
        expectThat(updatedCart) {
            get { totalPrice } isEqualTo 80
            get { lineItems } hasSize 2 and {
                withElementAt(0) {
                    get { sku.value } isEqualTo "A"
                    get { quantity } isEqualTo 1
                    get { unitPrice } isEqualTo 50
                    get { totalPrice } isEqualTo 50
                }
                withElementAt(1) {
                    get { sku.value } isEqualTo "B"
                    get { quantity } isEqualTo 1
                    get { unitPrice } isEqualTo 30
                    get { totalPrice } isEqualTo 30
                }
            }
        }
    }

    @Test
    fun `should update existing line item on cart with single item`() {
        val cartWithOneItem = emptyCart.copy(lineItems = mutableListOf(LineItem(SkuId("A"), 1, 50, 50)))
        val skuIdToAdd = SkuId("A")
        every { productRepository.get(skuIdToAdd) } returns Product(skuIdToAdd, 50)

        //when
        val updatedCart = cartModifierService.update(skuIdToAdd, cartWithOneItem)

        //then
        expectThat(updatedCart) {
            get { totalPrice } isEqualTo 100
            get { lineItems } hasSize 1 and {
                withElementAt(0) {
                    get { sku.value } isEqualTo "A"
                    get { quantity } isEqualTo 2
                    get { unitPrice } isEqualTo 50
                    get { totalPrice } isEqualTo 100
                }
            }
        }
    }

    @Test
    fun `should update existing line item on cart with multiple items`() {
        val cartWithTwoItems = emptyCart.copy(
                lineItems = mutableListOf(LineItem(SkuId("A"), 1, 50, 50), LineItem(SkuId("B"), 1, 30, 30)))
        val skuIdToAdd = SkuId("A")
        every { productRepository.get(skuIdToAdd) } returns Product(skuIdToAdd, 200)

        //when
        val updatedCart = cartModifierService.update(skuIdToAdd, cartWithTwoItems)

        //then
        expectThat(updatedCart) {
            get { totalPrice } isEqualTo 130
            get { lineItems } hasSize 2 and {
                withElementAt(0) {
                    get { sku.value } isEqualTo "A"
                    get { quantity } isEqualTo 2
                    get { unitPrice } isEqualTo 50
                    get { totalPrice } isEqualTo 100
                }
                withElementAt(1) {
                    get { sku.value } isEqualTo "B"
                    get { quantity } isEqualTo 1
                    get { unitPrice } isEqualTo 30
                    get { totalPrice } isEqualTo 30
                }
            }
        }
    }

    @Test
    fun `should update existing line item on cart with special pricing - requested equals required`() {
        val cartWithOneItems = emptyCart.copy(lineItems = mutableListOf(LineItem(SkuId("A"), 2, 50, 100)))
        val skuIdToAdd = SkuId("A")
        every { productRepository.get(skuIdToAdd) } returns Product(skuIdToAdd, 50, SpecialPrice(130, 3))

        //when
        val updatedCart = cartModifierService.update(skuIdToAdd, cartWithOneItems)

        //then
        expectThat(updatedCart) {
            get { totalPrice } isEqualTo 130
            get { lineItems } hasSize 1 and {
                withElementAt(0) {
                    get { sku.value } isEqualTo "A"
                    get { quantity } isEqualTo 3
                    get { unitPrice } isEqualTo 50
                    get { totalPrice } isEqualTo 130
                }
            }
        }
    }

    @Test
    fun `should update existing line item on cart with special pricing - requested quantity greater than required`() {
        val cartWithOneItems = emptyCart.copy(lineItems = mutableListOf(LineItem(SkuId("A"), 3, 50, 150)))
        val skuIdToAdd = SkuId("A")
        every { productRepository.get(skuIdToAdd) } returns Product(skuIdToAdd, 50, SpecialPrice(130, 3))

        //when
        val updatedCart = cartModifierService.update(skuIdToAdd, cartWithOneItems)

        //then
        expectThat(updatedCart) {
            get { totalPrice } isEqualTo 180
            get { lineItems } hasSize 1 and {
                withElementAt(0) {
                    get { sku.value } isEqualTo "A"
                    get { quantity } isEqualTo 4
                    get { unitPrice } isEqualTo 50
                    get { totalPrice } isEqualTo 180
                }
            }
        }
    }
}
