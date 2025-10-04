package com.bis;

import lombok.Data;

@Data
public class ShopSource implements Source {
    private String seller;
    private String location;
    private String stock;
    private String price;
    private String sellerWikiUrl;

    @Override
    public String getDisplayString() {
        return String.format("Seller: %s, Loc: %s, Stock: %s, Price: %s",
            seller, location, stock, price);
    }
}
