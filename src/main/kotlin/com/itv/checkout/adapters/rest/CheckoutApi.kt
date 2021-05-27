package com.itv.checkout.adapters.rest

import com.itv.checkout.application.CustomJackson.auto
import com.itv.checkout.domain.model.Cart
import com.itv.checkout.domain.model.CartId
import com.itv.checkout.domain.model.Product
import com.itv.checkout.domain.model.exceptions.CartNotFoundException
import com.itv.checkout.domain.model.exceptions.CheckoutException
import com.itv.checkout.domain.model.exceptions.ProductNotFoundException
import com.itv.checkout.domain.model.requests.AddLineItemRequest
import com.itv.checkout.domain.services.CheckoutService
import com.itv.checkout.domain.services.ProductsService
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.lens.Path
import org.http4k.routing.bind
import org.http4k.routing.routes

class CheckoutApi(private val checkoutService: CheckoutService,
                  private val productsService: ProductsService) {

    private val cartIdLens = Path.map(::CartId).of("cartId")
    private val addLineItemRequestLens = Body.auto<AddLineItemRequest>().toLens()
    private val loadProductsRequestLens = Body.auto<List<Product>>().toLens()
    private val cartsLens = Body.auto<Cart>().toLens()

    fun endpoints() = routes(
            "products" bind POST to ::loadProducts,
            "carts" bind routes(
                    "/" bind POST to ::createCart,
                    "/{cartId}" bind POST to ::updateCart)
    ).withFilter(ServerFilters.CatchLensFailure)

    private fun createCart(request: Request): Response {
        return Response(CREATED).with(cartsLens of checkoutService.createCart())
    }

    private fun updateCart(request: Request): Response {
        val cartId = cartIdLens(request)
        val addLineItemRequest = addLineItemRequestLens(request)
        return try {
            Response(OK).with(cartsLens of checkoutService.addLineItem(cartId, addLineItemRequest))
        } catch (exception: CheckoutException) {
            exception.toErrorResponse()
        }
    }

    private fun loadProducts(request: Request): Response {
        val productsRequest = loadProductsRequestLens(request)
        return productsService.loadProduct(productsRequest).run { Response(OK) }
    }

    private fun CheckoutException.toErrorResponse(): Response {
        return when (this) {
            is CartNotFoundException -> Response(NOT_FOUND).body("Cart not found")
            is ProductNotFoundException -> Response(NOT_FOUND).body("Product not found")
        }
    }
}
