package com.intern.backendettaba.designpattern.revenuestrategy;

import com.intern.backendettaba.entities.Product;
import com.intern.backendettaba.interfaces.RevenueStrategy;

public class ProductRevenue  implements RevenueStrategy {
    private final Product product;

    public ProductRevenue(Product product) {
        this.product = product;
    }

    @Override
    public float calculerRevenu() {
        System.out.println("!!!!!Revenue produit!!!!!!!!!!!!!!!"+ product.getWeight()*(product.getSoldPrice()- product.getBoughtPrice()) );

        return product.getWeight()*(product.getSoldPrice()- product.getBoughtPrice());
    }
}
