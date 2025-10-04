package com.bis;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.Color;

@ConfigGroup("bestinslot")
public interface BiSConfig extends Config
{
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
            position = 1,
            keyName = "commonColor",
            name = "Common Color",
            description = "Color for common drops"
    )
    default Color commonColor()
    {
        return BiSConstants.DEFAULT_COMMON_COLOR;
    }

    @ConfigItem(
            position = 2,
            keyName = "rareColor",
            name = "Rare Color",
            description = "Color for rare drops (better than 1/100)"
    )
    default Color rareColor()
    {
        return BiSConstants.DEFAULT_RARE_COLOR;
    }

    @ConfigItem(
            position = 3,
            keyName = "superRareColor",
            name = "Super Rare Color",
            description = "Color for super rare drops (better than 1/1000)"
    )
    default Color superRareColor()
    {
        return BiSConstants.DEFAULT_SUPER_RARE_COLOR;
    }

    @ConfigItem(
            position = 4,
            keyName = "ultraRareColor",
            name = "Ultra Rare Color",
            description = "Color for ultra rare drops (better than 1/10000)"
    )
    default Color ultraRareColor()
    {
        return BiSConstants.DEFAULT_ULTRA_RARE_COLOR;
    }

    @ConfigItem(
            position = 5,
            keyName = "priceColor",
            name = "Price Color",
            description = "Color for item prices"
    )
    default Color priceColor()
    {
        return BiSConstants.DEFAULT_PRICE_COLOR;
    }
}
