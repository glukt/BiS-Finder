package com.upgradefinder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class WikiScraper {

    private static final String API_URL_FORMAT = "https://oldschool.runescape.wiki/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=%s";
    private static final String WIKI_BASE_URL = "https://oldschool.runescape.wiki";
    private static final String WIKI_PAGE_URL_FORMAT = WIKI_BASE_URL + "/w/%s";
    private final OkHttpClient client;

    private static final List<String> POTENTIAL_UPGRADES = Arrays.asList(
            "Bronze sword", "Iron sword", "Steel sword", "Black sword", "Mithril sword", "Adamant sword", "Rune sword",
            "Dragon sword", "Dragon longsword", "Dragon scimitar", "Brine sabre", "Abyssal whip", "Abyssal tentacle", "Ghrazi rapier"
    );

    public WikiScraper(OkHttpClient client) {
        this.client = client;
    }

    public Optional<Weapon> getWeaponStats(String weaponName) {
        String encodedWeaponName = URLEncoder.encode(weaponName.replace(" ", "_"), StandardCharsets.UTF_8);
        String url = String.format(API_URL_FORMAT, encodedWeaponName);

        try {
            Request apiRequest = new Request.Builder().url(url).build();
            try (Response apiResponse = client.newCall(apiRequest).execute()) {
                if (!apiResponse.isSuccessful()) return Optional.empty();

                String jsonResponse = apiResponse.body().string();
                JsonObject root = new JsonParser().parse(jsonResponse).getAsJsonObject();
                JsonObject pages = root.getAsJsonObject("query").getAsJsonObject("pages");
                JsonObject page = pages.entrySet().iterator().next().getValue().getAsJsonObject();

                if (page.has("missing")) return Optional.empty();

                String wikitext = page.getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").getAsString();
                Weapon weapon = parseWikitext(weaponName, wikitext);
                weapon.setWikiUrl(String.format(WIKI_PAGE_URL_FORMAT, encodedWeaponName));

                fetchAndParseSourceDetails(weapon);

                log.info("Finished parsing {}: {}", weaponName, weapon.toString());
                return Optional.of(weapon);
            }
        } catch (Exception e) {
            log.error("Exception while fetching/parsing data for {}", weaponName, e);
            return Optional.empty();
        }
    }

    private void fetchAndParseSourceDetails(Weapon weapon) {
        try {
            Request itemPageRequest = new Request.Builder().url(weapon.getWikiUrl()).build();
            try (Response itemPageResponse = client.newCall(itemPageRequest).execute()) {
                if (!itemPageResponse.isSuccessful()) return;

                Document doc = Jsoup.parse(itemPageResponse.body().string(), weapon.getWikiUrl());
                Element sourceHeader = doc.selectFirst("h2:has(span#Item_sources), h2:has(span#Obtainment), h2:has(span#Creation)");
                if (sourceHeader == null) {
                    log.warn("Could not find a relevant source header for {}", weapon.getName());
                    return;
                }

                Element currentElement = sourceHeader.nextElementSibling();
                Source dropSource = null;
                Source shopSource = null;

                while (currentElement != null && !currentElement.tagName().equals("h2")) {
                    if (currentElement.is("table.wikitable")) {
                        Map<String, Integer> headerMap = parseTableHeader(currentElement);
                        if (headerMap.containsKey("Rarity") || headerMap.containsKey("Drop rate")) {
                            dropSource = parseDropsTable(currentElement, headerMap);
                        } else if (headerMap.containsKey("Seller") || headerMap.containsKey("Shop")) {
                            shopSource = parseShopTable(currentElement, headerMap);
                        }
                    }
                    currentElement = currentElement.nextElementSibling();
                }

                if (dropSource != null) {
                    weapon.setSource(dropSource);
                } else if (shopSource != null) {
                    weapon.setSource(shopSource);
                }
            }
        } catch (IOException e) {
            log.warn("Could not fetch source details for {}", weapon.getName(), e);
        }
    }

    private MonsterDrop parseDropsTable(Element table, Map<String, Integer> headerMap) {
        Element firstDataRow = table.selectFirst("tbody tr:has(td)");
        if (firstDataRow == null) return null;

        Elements dataCells = firstDataRow.select("td");
        MonsterDrop drop = new MonsterDrop();
        drop.setMonsterName(getCellText(dataCells, headerMap, Arrays.asList("Source", "Monster")));
        drop.setLevel(getCellText(dataCells, headerMap, Arrays.asList("Level", "Combat level")));
        drop.setQuantity(getCellText(dataCells, headerMap, Arrays.asList("Quantity")));
        drop.setRarity(getCellText(dataCells, headerMap, Arrays.asList("Rarity", "Drop rate")));
        return drop;
    }

    private ShopSource parseShopTable(Element table, Map<String, Integer> headerMap) {
        Element firstDataRow = table.selectFirst("tbody tr:has(td)");
        if (firstDataRow == null) return null;

        Elements dataCells = firstDataRow.select("td");
        ShopSource shop = new ShopSource();
        shop.setSeller(getCellText(dataCells, headerMap, Arrays.asList("Seller", "Shop")));
        shop.setLocation(getCellText(dataCells, headerMap, Arrays.asList("Location")));
        shop.setStock(getCellText(dataCells, headerMap, Arrays.asList("Stock", "Initial stock")));
        shop.setPrice(getCellText(dataCells, headerMap, Arrays.asList("Price", "Price sold at")));
        return shop;
    }

    private Map<String, Integer> parseTableHeader(Element table) {
        Elements headerCells = table.selectFirst("thead tr, tbody tr, tr").select("th");
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headerCells.size(); i++) {
            headerMap.put(headerCells.get(i).text().trim(), i);
        }
        return headerMap;
    }

    private String getCellText(Elements cells, Map<String, Integer> headerMap, List<String> headerNames) {
        for (String headerName : headerNames) {
            Integer index = headerMap.get(headerName);
            if (index != null && index < cells.size()) {
                return cells.get(index).text();
            }
        }
        return "N/A";
    }

    private Weapon parseWikitext(String weaponName, String wikitext) {
        Weapon weapon = new Weapon();
        weapon.setName(weaponName);
        boolean imageFound = false;

        String[] lines = wikitext.split("\\n");
        for (String line : lines) {
            if (!line.startsWith("|")) continue;

            String[] parts = line.substring(1).split("=", 2);
            if (parts.length != 2) continue;

            String key = parts[0].trim();
            String value = parts[1].trim();

            try {
                switch (key) {
                    case "astab": weapon.setStabAttack(Integer.parseInt(value)); break;
                    case "aslash": weapon.setSlashAttack(Integer.parseInt(value)); break;
                    case "acrush": weapon.setCrushAttack(Integer.parseInt(value)); break;
                    case "str": weapon.setStrengthBonus(Integer.parseInt(value)); break;
                    case "speed": weapon.setAttackSpeed(Integer.parseInt(value)); break;
                    case "image":
                        if (!imageFound) {
                            weapon.setImageUrl(parseImageValue(value));
                            imageFound = true;
                        }
                        break;
                }
            } catch (NumberFormatException e) {
                log.warn("Could not parse value '{}' for key '{}'", value, key);
            }
        }
        return weapon;
    }

    private String parseImageValue(String value) {
        String rawImageName = value.replace("[[", "").replace("]", "").replace("File:", "").trim();
        String imageName = rawImageName.split("[|]")[0].replace(" ", "_");
        return WIKI_BASE_URL + "/Special:Redirect/file/" + imageName;
    }

    public List<Weapon> findUpgrades(Weapon currentWeapon, String style) {
        List<Weapon> upgrades = new ArrayList<>();
        for (String upgradeName : POTENTIAL_UPGRADES) {
            if (upgradeName.equalsIgnoreCase(currentWeapon.getName())) {
                continue;
            }

            getWeaponStats(upgradeName).ifPresent(potentialUpgrade -> {
                boolean isUpgrade = false;
                switch (style.toLowerCase()) {
                    case "slash":
                        if (potentialUpgrade.getSlashAttack() > currentWeapon.getSlashAttack() &&
                                potentialUpgrade.getStrengthBonus() >= currentWeapon.getStrengthBonus()) {
                            isUpgrade = true;
                        }
                        break;
                    case "stab":
                        if (potentialUpgrade.getStabAttack() > currentWeapon.getStabAttack() &&
                                potentialUpgrade.getStrengthBonus() >= currentWeapon.getStrengthBonus()) {
                            isUpgrade = true;
                        }
                        break;
                    case "crush":
                        if (potentialUpgrade.getCrushAttack() > currentWeapon.getCrushAttack() &&
                                potentialUpgrade.getStrengthBonus() >= currentWeapon.getStrengthBonus()) {
                            isUpgrade = true;
                        }
                        break;
                }
                if (isUpgrade) {
                    upgrades.add(potentialUpgrade);
                }
            });
        }
        return upgrades;
    }
}
