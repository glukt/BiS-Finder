package com.bis;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;

@Slf4j
public class BiSBox extends JPanel {

    private final JPanel contentPanel;

    public BiSBox(Weapon weapon, BiSConfig config, ItemManager itemManager) {
        super(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        headerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel imageLabel = new JLabel("Loading...");
        imageLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        ImageUtil.loadImageAsync(weapon.getImageUrl()).thenAccept(image -> {
            if (image != null) {
                imageLabel.setIcon(new ImageIcon(image.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
                imageLabel.setText(null);
            }
        });
        headerPanel.add(imageLabel, BorderLayout.WEST);

        JLabel nameLabel = new JLabel(weapon.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(FontManager.getRunescapeBoldFont());
        headerPanel.add(nameLabel, BorderLayout.CENTER);

        // Add wiki link arrow
        JLabel linkLabel = new JLabel("->");
        linkLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        linkLabel.setToolTipText("Open wiki page");
        linkLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(weapon.getWikiUrl()));
                } catch (Exception ex) {
                    log.error("Could not open wiki link", ex);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                linkLabel.setForeground(Color.CYAN);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                linkLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            }
        });
        headerPanel.add(linkLabel, BorderLayout.EAST);

        // Content Panel
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;

        // Add stat labels
        addStat(contentPanel, c, "Slash Attack:", String.valueOf(weapon.getSlashAttack()));
        addStat(contentPanel, c, "Stab Attack:", String.valueOf(weapon.getStabAttack()));
        addStat(contentPanel, c, "Crush Attack:", String.valueOf(weapon.getCrushAttack()));
        addStat(contentPanel, c, "Strength Bonus:", String.valueOf(weapon.getStrengthBonus()));
        addStat(contentPanel, c, "Attack Speed:", String.valueOf(weapon.getAttackSpeed()));

        // Add source information panels
        List<Source> sources = weapon.getSources();
        if (sources != null && !sources.isEmpty()) {
            c.gridy++;
            c.gridwidth = 2; // Span across both columns
            c.gridx = 0;
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)), c);
            c.gridy++;

            for (Source source : sources) {
                JPanel sourceInfoPanel = new JPanel(new GridBagLayout());
                sourceInfoPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

                GridBagConstraints sourceC = new GridBagConstraints();
                sourceC.fill = GridBagConstraints.HORIZONTAL;
                sourceC.weightx = 1.0;
                sourceC.gridx = 0;
                sourceC.gridy = 0;
                sourceC.anchor = GridBagConstraints.WEST;

                if (source instanceof MonsterDrop) {
                    sourceInfoPanel.setBorder(BorderFactory.createTitledBorder("Drop Source"));
                    MonsterDrop drop = (MonsterDrop) source;
                    JComponent sourceComponent = (drop.getMonsterWikiUrl() != null)
                            ? createHyperlinkLabel(drop.getMonsterName(), drop.getMonsterWikiUrl())
                            : createStatLabel(drop.getMonsterName());
                    addStat(sourceInfoPanel, sourceC, "Source:", sourceComponent);
                    addStat(sourceInfoPanel, sourceC, "Level:", drop.getLevel());
                    addStat(sourceInfoPanel, sourceC, "Quantity:", drop.getQuantity());
                    addStat(sourceInfoPanel, sourceC, "Rarity:", createColoredRarityLabel(drop, config));
                } else if (source instanceof ShopSource) {
                    sourceInfoPanel.setBorder(BorderFactory.createTitledBorder("Shop Location"));
                    ShopSource shop = (ShopSource) source;
                    JComponent sellerComponent = (shop.getSellerWikiUrl() != null)
                            ? createHyperlinkLabel(shop.getSeller(), shop.getSellerWikiUrl())
                            : createStatLabel(shop.getSeller());
                    addStat(sourceInfoPanel, sourceC, "Seller:", sellerComponent);
                    addStat(sourceInfoPanel, sourceC, "Location:", shop.getLocation());
                    addStat(sourceInfoPanel, sourceC, "Stock:", shop.getStock());

                    // Custom handling for price to fix alignment
                    sourceC.gridx = 0;
                    sourceC.weightx = 0.5;
                    sourceC.anchor = GridBagConstraints.WEST;
                    sourceInfoPanel.add(new JLabel("Price:"), sourceC);

                    sourceC.gridx = 1;
                    sourceC.weightx = 0.5;
                    sourceC.anchor = GridBagConstraints.WEST;
                    sourceInfoPanel.add(createPriceComponent(shop, config, itemManager), sourceC);
                    sourceC.gridy++;
                }
                contentPanel.add(sourceInfoPanel, c);
                c.gridy++;
            }
        }

        contentPanel.setVisible(false); // Collapsed by default

        // Toggle visibility on header click
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (! (e.getSource() instanceof JLabel)) {
                    contentPanel.setVisible(!contentPanel.isVisible());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                headerPanel.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
            }
        });

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addStat(JPanel panel, GridBagConstraints c, String name, JComponent valueComponent) {
        c.gridx = 0;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(name), c);

        c.gridx = 1;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.EAST;
        panel.add(valueComponent, c);

        c.gridy++;
    }

    private void addStat(JPanel panel, GridBagConstraints c, String name, String value) {
        addStat(panel, c, name, createStatLabel(value));
    }

    private JLabel createStatLabel(String value) {
        JLabel label = new JLabel(value);
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    private JLabel createHyperlinkLabel(String text, String url) {
        JLabel linkLabel = new JLabel(text);
        linkLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        linkLabel.setToolTipText("Open wiki page for " + text);
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    log.error("Could not open wiki link", ex);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                linkLabel.setForeground(Color.CYAN);
                linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                linkLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                linkLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return linkLabel;
    }

    private JLabel createColoredRarityLabel(MonsterDrop drop, BiSConfig config) {
        JLabel label = new JLabel(drop.getRarity());
        double rarity = drop.getRarityValue();
        if (rarity > 0) {
            if (rarity <= 0.0001) { // Ultra Rare (1/10000)
                label.setForeground(config.ultraRareColor());
            } else if (rarity <= 0.001) { // Super Rare (e.g., 1/1000)
                label.setForeground(config.superRareColor());
            } else if (rarity <= 0.01) { // Rare (e.g., 1/100)
                label.setForeground(config.rareColor());
            } else { // Common
                label.setForeground(config.commonColor());
            }
        } else {
            label.setForeground(config.commonColor());
        }
        return label;
    }

    private JPanel createPriceComponent(ShopSource shop, BiSConfig config, ItemManager itemManager) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel priceLabel = new JLabel(shop.getPrice());
        priceLabel.setForeground(config.priceColor());

        BufferedImage coinImage = itemManager.getImage(ItemID.COINS_995);
        if (coinImage != null) {
            ImageIcon icon = new ImageIcon(coinImage.getScaledInstance(12, 12, Image.SCALE_SMOOTH));
            JLabel iconLabel = new JLabel(icon);
            panel.add(iconLabel);
        }

        panel.add(priceLabel);
        return panel;
    }
}
