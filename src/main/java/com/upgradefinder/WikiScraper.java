package com.upgradefinder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class WikiScraper {

    private static final String API_URL_FORMAT = "https://oldschool.runescape.wiki/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=%s";
    private final OkHttpClient client;
    private final Gson gson;

    private static final List<String> POTENTIAL_UPGRADES = Arrays.asList(
            "Bronze sword", "Iron sword", "Steel sword", "Black sword", "Mithril sword", "Adamant sword", "Rune sword",
            "Dragon sword", "Dragon longsword", "Dragon scimitar", "Brine sabre", "Abyssal whip", "Abyssal tentacle", "Ghrazi rapier"
    );

    public WikiScraper(OkHttpClient client) {
        this.client = client;
        this.gson = new Gson();
    }

    public Optional<Weapon> getWeaponStats(String weaponName) {
        String url = String.format(API_URL_FORMAT, weaponName.replace(" ", "_"));
        log.info("Querying API for {} from URL: {}", weaponName, url);

        try {
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("API request failed for {}: {}", weaponName, response.code());
                    return Optional.empty();
                }

                String jsonResponse = response.body().string();
                JsonObject root = new JsonParser().parse(jsonResponse).getAsJsonObject();
                JsonObject pages = root.getAsJsonObject("query").getAsJsonObject("pages");
                JsonObject page = pages.entrySet().iterator().next().getValue().getAsJsonObject();

                if (page.has("missing")) {
                    log.warn("API reports page is missing for {}", weaponName);
                    return Optional.empty();
                }

                String wikitext = page.getAsJsonArray("revisions").get(0).getAsJsonObject().get("*").getAsString();

                Weapon weapon = new Weapon();
                weapon.setName(weaponName);

                // Use the correct stat names from the wikitext (e.g., astab, aslash, str)
                weapon.setStabAttack(parseStatFromWikitext(wikitext, "astab"));
                weapon.setSlashAttack(parseStatFromWikitext(wikitext, "aslash"));
                weapon.setCrushAttack(parseStatFromWikitext(wikitext, "acrush"));
                weapon.setMagicAttack(parseStatFromWikitext(wikitext, "amagic"));
                weapon.setRangedAttack(parseStatFromWikitext(wikitext, "arange"));
                weapon.setStrengthBonus(parseStatFromWikitext(wikitext, "str"));
                weapon.setAttackSpeed(parseStatFromWikitext(wikitext, "speed"));

                log.info("Finished parsing {}: {}", weaponName, weapon.toString());
                return Optional.of(weapon);
            }
        } catch (Exception e) {
            log.error("Exception while fetching/parsing API data for {}", weaponName, e);
            return Optional.empty();
        }
    }

    private int parseStatFromWikitext(String wikitext, String statName) {
        // This regex correctly handles optional whitespace and leading +/- signs.
        Pattern pattern = Pattern.compile("\\|\\s*" + statName + "\\s*=\\s*([+-]?\\d+)");
        Matcher matcher = pattern.matcher(wikitext);
        if (matcher.find()) {
            try {
                String valueStr = matcher.group(1);
                log.info("Found stat: '{}' with value: '{}'", statName, valueStr);
                return Integer.parseInt(valueStr);
            } catch (NumberFormatException e) {
                log.warn("Could not parse found value for stat: {}", statName);
            }
        }
        return 0;
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