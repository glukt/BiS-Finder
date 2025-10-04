# Best in Slot (BiS) Finder Plugin

A RuneLite plugin to help Old School RuneScape players, especially Ironmen, find their next best gear upgrade.

## Overview

The Best in Slot (BiS) Finder helps you discover the optimal gear progression path directly within the game. By right-clicking an equipped or inventoried item, you can get immediate, context-aware upgrade suggestions, taking the guesswork out of gearing up.

 <img width="234" height="658" alt="image" src="https://github.com/user-attachments/assets/57d4c3d6-8f10-4dc8-87f3-1a6193fe294d" />

## Features

-   **Side Panel Integration:** Adds a convenient navigation button to your sidebar.
-   **Upgrade Suggestions:** Right-click a weapon and select "Find BiS" to see a list of potential upgrades.
  
<img width="246" height="246" alt="image" src="https://github.com/user-attachments/assets/c50614a8-3ee7-46a1-9cbc-c056fba9d578" />

-   **Detailed Stats:** View key combat stats for each suggested item, including Stab, Slash, Crush, Strength, and Attack Speed.
-   **Wiki Integration:** Each item links directly to its official OSRS Wiki page for more details.
-   **Source Information:** The plugin scrapes the wiki to show you how to obtain each upgrade:
    -   **Monster Drops:** See the monster, its combat level, drop quantity, and rarity.
    -   **Shop Locations:** Find the seller, location, stock, and price.
-   **Responsive UI:** All web scraping and data parsing run on a background thread to ensure the game client remains smooth and responsive.

## Current Status (as of Oct 4, 2025)

The plugin is in active development. Here is a snapshot of our current work:

-   **Known Bug:** The wiki scraper does not yet parse "Creation" information for crafted items (e.g., Abyssal tentacle).
-   **Next Up:** We are working on a more intelligent upgrade algorithm beyond simple stat comparison.
-   **UI Polish:** We plan to improve the UI for displaying source information, inspired by plugins like the Loot Lookup plugin.

## Future Goals

-   Expand upgrade suggestions to all gear slots (armor, jewelry, etc.).
-   Implement a side-by-side stat comparison view.

## Installation

This plugin will be available on the RuneLite Plugin Hub. To install, simply search for "Best in Slot" in the Plugin Hub and click "Install".
