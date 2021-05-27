package com.itv.checkout.domain.model.exceptions

sealed class CheckoutException : Exception()

object CartNotFoundException : CheckoutException()
object ProductNotFoundException : CheckoutException()
