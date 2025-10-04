package com.bis;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import net.runelite.client.game.ItemManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class BiSPanel extends PluginPanel {

    private final BiSConfig config;
    private final ItemManager itemManager;

    private final JPanel welcomePanel;
    private final JPanel resultsPanel;
    private final JScrollPane scrollPane;

    public BiSPanel(BiSConfig config, ItemManager itemManager) {
        super(false);
        this.config = config;
        this.itemManager = itemManager;
        setLayout(new GridBagLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Right-click an item and select 'Find BiS'");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
        wrapper.add(resultsPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        add(welcomePanel, gbc);
    }

    public void showLoading() {
        SwingUtilities.invokeLater(() -> {
            removeAll();
            resultsPanel.removeAll();
            resultsPanel.add(createCenteredLabel("Searching for upgrades..."));
            addScrollPane();
            revalidate();
            repaint();
        });
    }

    public void displayUpgrades(String itemName, List<Weapon> upgrades) {
        SwingUtilities.invokeLater(() -> {
            removeAll();
            resultsPanel.removeAll();

            if (upgrades.isEmpty()) {
                resultsPanel.add(createCenteredLabel("No upgrades found for " + itemName));
            } else {
                resultsPanel.add(createHeaderLabel("Upgrades for " + itemName));
                for (Weapon upgrade : upgrades) {
                    resultsPanel.add(new BiSBox(upgrade, config, itemManager));
                    resultsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
            addScrollPane();
            revalidate();
            repaint();
        });
    }

    private void addScrollPane() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(scrollPane, gbc);
    }

    private JLabel createCenteredLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        return label;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        return label;
    }
}
