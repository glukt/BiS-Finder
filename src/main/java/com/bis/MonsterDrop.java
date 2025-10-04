package com.bis;

import lombok.Data;

@Data
public class MonsterDrop implements Source {
    private String monsterName;
    private String level;
    private String quantity;
    private String rarity;
    private double rarityValue;
    private String monsterWikiUrl;

    @Override
    public String getDisplayString() {
        return String.format("Source: %s, Lvl: %s, Qty: %s, Rarity: %s", 
            monsterName, level, quantity, rarity);
    }
}
