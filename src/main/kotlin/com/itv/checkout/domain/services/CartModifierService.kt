package com.itv.checkout.domain.services

import com.itv.checkout.domain.model.Cart
import com.itv.checkout.domain.model.LineItem
import com.itv.checkout.domain.model.Product
import com.itv.checkout.domain.model.SkuId

class CartModifierService(private val productRepository: ProductRepository) {

    fun add(skuId: SkuId, cart: Cart): Cart {
        val lineItems = cart.lineItems
        val product = productRepository.get(skuId)
        val itemToAdd = LineItem(product.sku, 1, product.unitPrice, product.unitPrice)
        lineItems.add(itemToAdd)

        return cart.copy(
                lineItems = lineItems,
                totalPrice = lineItems.sum()
        )
    }

    fun update(skuId: SkuId, cart: Cart): Cart {
        val lineItems = cart.lineItems
        val product = productRepository.get(skuId)
        val existingItem = lineItems.first { it.sku == skuId }
        val indexOfExistingItem = lineItems.indexOf(existingItem)
        val updatedItem = updateItem(existingItem, product)
        lineItems[indexOfExistingItem] = updatedItem

        return cart.copy(
                lineItems = lineItems,
                totalPrice = lineItems.sum()
        )
    }

    private fun updateItem(existingItem: LineItem, product: Product): LineItem {
        val requestedQuantity = existingItem.quantity + 1
        val updatedItem = existingItem.copy(quantity = requestedQuantity, totalPrice = existingItem.unitPrice * requestedQuantity)
        val specialPrice = product.specialPrice ?: return updatedItem

        return if (requestedQuantity >= specialPrice.requiredQuantity) {
            val groupsEligibleForSpecialPrice = requestedQuantity / specialPrice.requiredQuantity
            val remainingNormalPriceQuantity = requestedQuantity - (specialPrice.requiredQuantity * groupsEligibleForSpecialPrice)
            val totalPrice = (groupsEligibleForSpecialPrice * specialPrice.price) + (remainingNormalPriceQuantity * product.unitPrice)
            updatedItem.copy(totalPrice = totalPrice)
        } else {
            updatedItem
        }
    }

    private fun List<LineItem>.sum() = this.sumOf { it.totalPrice }
}
