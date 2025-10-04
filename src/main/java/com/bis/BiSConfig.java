package com.bis;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

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
}
