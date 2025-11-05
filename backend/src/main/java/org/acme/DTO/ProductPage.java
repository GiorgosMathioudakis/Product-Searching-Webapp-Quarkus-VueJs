package org.acme.DTO;

import org.acme.Model.Product;

import java.util.List;

public class ProductPage {
    private List<Product> products;
    private boolean hasNext;

    public ProductPage() {}

    public ProductPage(List<Product> products, int pageSize) {
        if(products.size()<pageSize){
            this.products = products;
            this.hasNext = false;
        }else{
            this.products = products.subList(0,pageSize);
            this.hasNext = true;
        }
    }
}
