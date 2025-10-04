package com.upgradefinder;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Weapon {
    private String name;
    private int stabAttack;
    private int slashAttack;
    private int crushAttack;
    private int magicAttack;
    private int rangedAttack;
    private int strengthBonus;
    private int attackSpeed;
    private String imageUrl;
    private String wikiUrl;
    private List<Source> sources = new ArrayList<>();

    @Override
    public String toString() {
        return "Weapon{" +
                "name='" + name + '\'' +
                ", stabAttack=" + stabAttack +
                ", slashAttack=" + slashAttack +
                ", crushAttack=" + crushAttack +
                ", magicAttack=" + magicAttack +
                ", rangedAttack=" + rangedAttack +
                ", strengthBonus=" + strengthBonus +
                ", attackSpeed=" + attackSpeed +
                ", imageUrl='" + imageUrl + '\'' +
                ", wikiUrl='" + wikiUrl + '\'' +
                ", sources=" + sources +
                '}';
    }
}