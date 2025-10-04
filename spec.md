# Specification for the RuneLite Upgrade Finder Plugin

## 1. Project Vision & Goals

-   **Vision:** To provide Ironman Old School RuneScape players with a simple and intuitive way to find the next best gear upgrade for their equipped or inventoried items.
-   **Core Problem:** Players often don't know the optimal gear progression path. This plugin aims to solve that directly within the game interface by providing immediate, context-aware upgrade suggestions.

## 2. Current Functionality (as of Oct 4, 2025)

-   The plugin successfully loads and adds a navigation button to the side toolbar.
-   Right-clicking a weapon and selecting "Check Upgrades" opens the side panel.
-   The panel displays a list of potential upgrades in a collapsible accordion view.
-   Each item correctly displays its inventory icon and combat stats (Stab, Slash, Crush, Strength, Attack Speed).
-   A clickable arrow icon links directly to the item's official OSRS Wiki page.
-   The plugin scrapes the wiki to find and display detailed source information for each item, including:
    -   **Monster Drops:** Shows the primary monster source, its combat level, drop quantity, and rarity.
    -   **Shop Locations:** Shows the seller, location, stock, and price.
-   The UI displays multiple sources (e.g., both drops and shops) in separate, titled panels.
-   All web requests and parsing happen on a background thread to ensure the client UI remains responsive.

## 3. Active Tasks & Known Issues

-   **Bug:** The scraper does not currently parse "Creation" information for items that are crafted rather than dropped or bought (e.g., Abyssal tentacle).
-   **Next Task:** Implement more intelligent upgrade logic. The current logic is a simple stat comparison. The next step is to create a more sophisticated algorithm to determine what a true "upgrade" is.
-   **Visual Polish:** Improve the UI for displaying source information. We can take inspiration from the `loot-lookup-plugin` to add coloring for rarity and other visual cues to make the data easier to read at a glance.

## 4. Future Goals

-   Expand coverage to other gear slots (armor, jewelry, etc.).
-   Provide a side-by-side stat comparison view.