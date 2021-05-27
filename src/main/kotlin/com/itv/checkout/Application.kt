package com.itv.checkout

import com.itv.checkout.adapters.cart.LocalCartApi
import com.itv.checkout.adapters.product.LocalProductApi
import com.itv.checkout.adapters.rest.CheckoutApi
import com.itv.checkout.domain.services.CartModifierService
import com.itv.checkout.domain.services.CheckoutService
import com.itv.checkout.domain.services.ProductsService
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val cartRepository = LocalCartApi()
    val productRepository = LocalProductApi()
    val cartModifierService = CartModifierService(productRepository)
    val loadProductService = ProductsService(productRepository)
    val checkoutService = CheckoutService(cartRepository, cartModifierService)
    val checkoutApi = CheckoutApi(checkoutService, loadProductService)

    checkoutApi.endpoints().asServer(Jetty(8080)).start()
}

