package com.upgradefinder;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@PluginDescriptor(
        name = "Upgrade Finder",
        description = "A plugin to find the best gear upgrades.",
        tags = {"upgrade", "finder", "gear"}
)
public class UpgradeFinderPlugin extends Plugin {
    private static final String UPGRADE_FINDER_ACTION = "Check Upgrades";

    @Inject
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private UpgradeFinderConfig config;

    private OkHttpClient okHttpClient;
    private WikiScraper wikiScraper;
    private UpgradeFinderPanel panel;
    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {
        this.panel = new UpgradeFinderPanel();
        this.okHttpClient = new OkHttpClient();
        this.wikiScraper = new WikiScraper(okHttpClient);

        BufferedImage icon = null;
        try {
            icon = ImageUtil.loadImageResource(getClass(), "/upgrade_icon.png");
        } catch (IllegalArgumentException e) {
            log.warn("Upgrade Finder icon not found, plugin will load without it.");
        }

        if (icon != null) {
            navButton = NavigationButton.builder()
                    .tooltip("Upgrade Finder")
                    .icon(icon)
                    .priority(5)
                    .panel(panel)
                    .build();
        } else {
            navButton = NavigationButton.builder()
                    .tooltip("Upgrade Finder")
                    .priority(5)
                    .panel(panel)
                    .build();
        }

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (config.enablePlugin()) {
            if (event.getOption().equals("Wield") || event.getOption().equals("Wear") || event.getOption().equals("Remove")) {
                client.createMenuEntry(-1)
                        .setOption(UPGRADE_FINDER_ACTION)
                        .setTarget(event.getTarget())
                        .setType(MenuAction.RUNELITE)
                        .setIdentifier(event.getIdentifier());
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuOption().equals(UPGRADE_FINDER_ACTION)) {
            String itemName = Text.removeTags(event.getMenuTarget());
            findAndDisplayUpgrade(itemName, "slash");
        }
    }

    private void findAndDisplayUpgrade(String itemName, String style) {
        SwingUtilities.invokeLater(() -> {
            panel.showLoading();
            clientToolbar.openPanel(navButton);
        });

        new Thread(() -> {
            log.info("Finding upgrades for {} with style {}", itemName, style);

            Optional<Weapon> currentWeaponOpt = wikiScraper.getWeaponStats(itemName);
            if (!currentWeaponOpt.isPresent()) {
                log.warn("Could not retrieve stats for current weapon: {}", itemName);
                panel.displayUpgrades(itemName, new ArrayList<>()); // Show empty panel
                return;
            }

            List<Weapon> upgrades = wikiScraper.findUpgrades(currentWeaponOpt.get(), style);
            panel.displayUpgrades(itemName, upgrades);
        }).start();
    }

    @Provides
    UpgradeFinderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(UpgradeFinderConfig.class);
    }
}