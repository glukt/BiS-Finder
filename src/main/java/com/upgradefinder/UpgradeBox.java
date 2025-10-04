package com.upgradefinder;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UpgradeBox extends JPanel {

    private final JPanel contentPanel;

    public UpgradeBox(Weapon weapon) {
        super(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        headerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel nameLabel = new JLabel(weapon.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(FontManager.getRunescapeBoldFont());
        headerPanel.add(nameLabel, BorderLayout.CENTER);

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

        // Add stat labels using GridBagLayout
        addStat(contentPanel, c, "Slash Attack:", String.valueOf(weapon.getSlashAttack()));
        addStat(contentPanel, c, "Stab Attack:", String.valueOf(weapon.getStabAttack()));
        addStat(contentPanel, c, "Crush Attack:", String.valueOf(weapon.getCrushAttack()));
        addStat(contentPanel, c, "Strength Bonus:", String.valueOf(weapon.getStrengthBonus()));
        addStat(contentPanel, c, "Attack Speed:", String.valueOf(weapon.getAttackSpeed()));

        contentPanel.setVisible(false); // Collapsed by default

        // Toggle visibility on header click
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                contentPanel.setVisible(!contentPanel.isVisible());
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