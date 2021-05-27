## Checkout Api

Usage:

1. Start app via main()
2. Load products using `POST /products` (see [here](#add-products-example-request-body) for body example)
    * Returns `200` on success
3. Start checkout session using `POST /carts`
    * Returns `201` on success with body
    ```bazaar
    {
        "id": "newly-generated-cart-id",
        "lineItems": [],
        "totalPrice": 0
    }
    ```
4. Add Item to cart using `POST /cart/newly-generated-cart-id` (see [here](#add-item-to-cart-request-body) for body example)
    * Returns `200` on success with body
    ```bazaar
    {
        "id": "newly-generated-cart-id",
        "lineItems": [
            {
                "sku": "A",
                "quantity": 1,
                "unitPrice": 50,
                "totalPrice": 50
            }
        ],
        "totalPrice": 50
    }
    ```

#### Add products example request body
```bazaar
[
  {
    "sku": "A",
    "unitPrice": 50,
    "specialPrice": {
      "price": 130,
      "requiredQuantity": 3
    }
  },
  {
    "sku": "B",
    "unitPrice": 30,
    "specialPrice": {
      "price": 45,
      "requiredQuantity": 2
    }
  },
  {
    "sku": "C",
    "unitPrice": 20
  },
  {
    "sku": "D",
    "unitPrice": 15
  }
]
```

#### Add item to cart request body
```bazaar
{
  "sku": "A"
}
```
