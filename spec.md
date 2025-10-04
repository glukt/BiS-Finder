# Specification for the RuneLite Upgrade Finder Plugin

## 1. Project Vision & Goals

-   **Vision:** To provide Ironman Old School RuneScape players with a simple and intuitive way to find the next best gear upgrade for their equipped or inventoried items.
-   **Core Problem:** Players often don't know the optimal gear progression path. This plugin aims to solve that directly within the game interface by providing immediate, context-aware upgrade suggestions with their potential Best-in-slot upgrade.

## 2. Current Functionality (as of Oct 3, 2025)

-   The plugin successfully loads and adds a navigation button to the side toolbar.
-   The data scraper now uses the official OSRS Wiki API to fetch raw item data, ensuring stats are parsed reliably.
-   Right-clicking a weapon and selecting "Check Upgrades" opens the side panel.
-   The panel displays a list of potential upgrades, and each item in the accordion view correctly shows the weapon's combat stats (Stab, Slash, Crush, Strength, and Attack Speed).
-   All API calls and processing happen on a background thread to ensure the client UI remains responsive.

## 3. Limitations & Active Development Tasks

-   **Task: Fix UI Bugs**
    -   **Symptom 1 (Panel Resizing):** The side panel is not horizontally resizable. When the main RuneLite sidebar is resized, the content within the plugin panel does not expand or reflow, making it difficult to read. This is the last remaining known bug.

-   **Task: Expand Feature Set**
    -   Expand coverage to other gear slots (armor, jewelry, etc.).
    -   Provide an image corresponding with each item in the list, allowing the user to click the item and go to the corresponding wiki page.
    -   Implement more intelligent upgrade logic beyond simple stat comparison.
    -   Provide a side-by-side stat comparison view. 