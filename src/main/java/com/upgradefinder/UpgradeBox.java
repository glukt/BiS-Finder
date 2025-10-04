package com.upgradefinder;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;

@Slf4j
public class UpgradeBox extends JPanel {

    private final JPanel contentPanel;

    public UpgradeBox(Weapon weapon) {
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
                    addStat(sourceInfoPanel, sourceC, "Source:", drop.getMonsterName());
                    addStat(sourceInfoPanel, sourceC, "Level:", drop.getLevel());
                    addStat(sourceInfoPanel, sourceC, "Quantity:", drop.getQuantity());
                    addStat(sourceInfoPanel, sourceC, "Rarity:", drop.getRarity());
                } else if (source instanceof ShopSource) {
                    sourceInfoPanel.setBorder(BorderFactory.createTitledBorder("Shop Location"));
                    ShopSource shop = (ShopSource) source;
                    addStat(sourceInfoPanel, sourceC, "Seller:", shop.getSeller());
                    addStat(sourceInfoPanel, sourceC, "Location:", shop.getLocation());
                    addStat(sourceInfoPanel, sourceC, "Stock:", shop.getStock());
                    addStat(sourceInfoPanel, sourceC, "Price:", shop.getPrice());
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

    private void addStat(JPanel panel, GridBagConstraints c, String name, String value) {
        c.gridx = 0;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(name), c);

        c.gridx = 1;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.EAST;
        panel.add(createStatLabel(value), c);

        c.gridy++;
    }

    private JLabel createStatLabel(String value) {
        JLabel label = new JLabel(value);
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }
}