package com.bis;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.Color;

@ConfigGroup("bestinslot")
public interface BiSConfig extends Config
{
    @ConfigSection(
            name = "Rarity Colors",
            description = "Colors for different drop rate tiers",
            position = 10
    )
    String rarityColors = "rarityColors";

    @ConfigItem(
            keyName = "enablePlugin",
            name = "Enable Best in Slot",
            description = "Toggles the plugin on or off"
    )
    default boolean enablePlugin()
    {
        return true;
    }

    @ConfigItem(
            position = 11,
            keyName = "alwaysColor",
            name = "Always Color",
            description = "Color for 'Always' drops",
            section = rarityColors
    )
    default Color alwaysColor()
    {
        return BiSConstants.ALWAYS_COLOR;
    }

    @ConfigItem(
            position = 12,
            keyName = "commonRarityColor",
            name = "Common Color",
            description = "Color for drops rarer than 1/25",
            section = rarityColors
    )
    default Color commonRarityColor()
    {
        return BiSConstants.COMMON_RARITY_COLOR;
    }

    @ConfigItem(
            position = 13,
            keyName = "mediumRarityColor",
            name = "Medium-Rare Color",
            description = "Color for drops between 1/25 and 1/75",
            section = rarityColors
    )
    default Color mediumRarityColor()
    {
        return BiSConstants.MEDIUM_RARITY_COLOR;
    }

    @ConfigItem(
            position = 14,
            keyName = "rareRarityColor",
            name = "Rare Color",
            description = "Color for drops between 1/75 and 1/999",
            section = rarityColors
    )
    default Color rareRarityColor()
    {
        return BiSConstants.RARE_RARITY_COLOR;
    }

    @ConfigItem(
            position = 15,
            keyName = "superRareRarityColor",
            name = "Super Rare Color",
            description = "Color for drops rarer than 1/999",
            section = rarityColors
    )
    default Color superRareRarityColor()
    {
        return BiSConstants.SUPER_RARE_RARITY_COLOR;
    }

    @ConfigItem(
            position = 16,
            keyName = "priceColor",
            name = "Price Color",
            description = "Color for item prices"
    )
    default Color priceColor()
    {
        return BiSConstants.DEFAULT_PRICE_COLOR;
    }
}