# Specification for the RuneLite Upgrade Finder Plugin

## 1. Project Vision & Goals

-   **Vision:** To provide Ironman Old School RuneScape players with a simple and intuitive way to find the next best gear upgrade for their equipped or inventoried items.
-   **Core Problem:** Players often don't know the optimal gear progression path. This plugin aims to solve that directly within the game interface by providing immediate, context-aware upgrade suggestions with their potential Best-in-slot upgrade.

## 2. Current Functionality (as of Oct 3, 2025)

-   The plugin successfully loads and adds a navigation button to the side toolbar.
-   Right-clicking a weapon and selecting "Check Upgrades" opens the side panel.
-   The panel displays a list of potential upgrades, and each item in the accordion view correctly shows the weapon's combat stats (Stab, Slash, Crush, Strength, and Attack Speed).
-   All API calls and processing happen on a background thread to ensure the client UI remains responsive.

## 3. Limitations & Active Development Tasks

-   KEY: ALWAYS look to @loot-lookup-plugin/ for project reference. This plugin has parsed almost all of the data already.

-   **Task 1: Robust Item Information**
    -   Parse respective wiki pages for Item sources and Shop locations tables, giving all necessary information for each in the side bar.

**  Bug Symptom: **
- 
-   **Future Task Developments: Expand Feature Set**
    -   Expand coverage to other gear slots (armor, jewelry, etc.).
    -   Provide an image corresponding with each item in the list, allowing the user to click the item and go to the corresponding wiki page.
    -   Provide a side-by-side stat comparison view. 
    -   Implement more intelligent upgrade logic beyond simple stat comparison.
