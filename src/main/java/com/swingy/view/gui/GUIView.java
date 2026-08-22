package com.swingy.view.gui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.swingy.model.DirectionType;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroType;
import com.swingy.model.map.GameMap;
import com.swingy.view.GameView;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.concurrent.CountDownLatch;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GUIView implements GameView {

    private final JFrame frame;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private final JPanel startPanel;
    private final JPanel heroPanel;
    private final JPanel heroNamePanel;
    private final JPanel gamePanel;
    private final JPanel battlePanel;
    private final UIFactory uiFactory;

    private final AtomicReference<Boolean> newHeroChoice;
    private final AtomicReference<String> heroNameInput;
    private final AtomicReference<HeroType> HeroTypeInput;
    private CountDownLatch newHeroLatch;
    private CountDownLatch heroNameLatch;
    private boolean screensReady;

    public GUIView() {
        frame = new JFrame();
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        startPanel = new JPanel();
        heroPanel = new JPanel();
        heroNamePanel = new JPanel();
        gamePanel = new JPanel();
        battlePanel = new JPanel();

        cardPanel.add(startPanel, "start");
        cardPanel.add(heroPanel, "hero");
        cardPanel.add(heroNamePanel, "heroName");
        cardPanel.add(gamePanel, "game");
        cardPanel.add(battlePanel, "battle");

        newHeroChoice = new AtomicReference<>(null);
        heroNameInput = new AtomicReference<>(null);
        HeroTypeInput = new AtomicReference<>(null);
        uiFactory = new SwingUIFactory();
        screensReady = false;

        frame.setContentPane(cardPanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void startGame() {
        buildStartScreen();
        buildHeroChoiceScreen();
        screensReady = true;
        cardLayout.show(cardPanel, "start");
    }

    private GridBagConstraints centeredConstraints(int row, int bottomInset) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, bottomInset, 0);
        return constraints;
    }

    private void buildStartScreen() {
        uiFactory.configureScreenPanel(startPanel);

        JLabel title = new JLabel("SWINGY");
        uiFactory.applyTitleStyle(title, 28f);

        JButton startButton = uiFactory.createButton("START", UIFactory.Components.PRIMARY);
        startButton.addActionListener(e -> cardLayout.show(cardPanel, "hero"));

        startPanel.add(title, centeredConstraints(0, 20));
        startPanel.add(startButton, centeredConstraints(1, 0));
        startPanel.revalidate();
        startPanel.repaint();
    }

    private void buildHeroChoiceScreen() {
        heroPanel.removeAll();
        uiFactory.configureScreenPanel(heroPanel);

        JLabel heroTitle = new JLabel("Choose hero setup");
        uiFactory.applyTitleStyle(heroTitle, 24f);

        JButton createHeroButton = uiFactory.createButton("Create New Hero",
                UIFactory.Components.PRIMARY);
        createHeroButton.addActionListener(e -> {
            newHeroChoice.set(Boolean.TRUE);
            if (newHeroLatch != null) {
                newHeroLatch.countDown();
            }
        });

        JButton loadHeroButton = uiFactory.createButton("Select Existing Hero",
                UIFactory.Components.SECONDARY);
        loadHeroButton.addActionListener(e -> {
            newHeroChoice.set(Boolean.FALSE);
            if (newHeroLatch != null) {
                newHeroLatch.countDown();
            }
        });

        heroPanel.add(heroTitle, centeredConstraints(0, 20));
        heroPanel.add(createHeroButton, centeredConstraints(1, 12));
        heroPanel.add(loadHeroButton, centeredConstraints(2, 0));
        heroPanel.revalidate();
        heroPanel.repaint();
    }

    private void buildHeroCreationScreen() {
        heroNamePanel.removeAll();
        uiFactory.configureScreenPanel(heroNamePanel);

        // Create Title
        JLabel heroNameTitle = new JLabel("Name your hero");
        uiFactory.applyTitleStyle(heroNameTitle, 24f);

        // Create Input Field and Selector
        JTextField heroNameField = uiFactory.createTextField(18, UIFactory.Components.FIELD);
        JLabel HeroTypeTitle = new JLabel("Choose hero class");
        uiFactory.applyTitleStyle(HeroTypeTitle, 18f);

        // Create HeroType Selector
        JComboBox<HeroType> HeroTypeSelector = uiFactory.createSelector(HeroType.values(),
                UIFactory.Components.SELECTOR);

        // Create Submit Button
        JButton submitButton = uiFactory.createButton("Continue", UIFactory.Components.PRIMARY);
        submitButton.addActionListener(e -> {
            String name = heroNameField.getText().trim();
            HeroType selectedClass = (HeroType) HeroTypeSelector.getSelectedItem();
            if (!name.isEmpty() && selectedClass != null) {
                heroNameInput.set(name);
                HeroTypeInput.set(selectedClass);
                if (heroNameLatch != null) {
                    heroNameLatch.countDown();
                }
            }

        });

        heroNamePanel.add(heroNameTitle, centeredConstraints(0, 20));
        heroNamePanel.add(heroNameField, centeredConstraints(1, 12));
        heroNamePanel.add(HeroTypeTitle, centeredConstraints(2, 12));
        heroNamePanel.add(HeroTypeSelector, centeredConstraints(3, 12));
        heroNamePanel.add(submitButton, centeredConstraints(4, 0));
        heroNamePanel.revalidate();
        heroNamePanel.repaint();
    }

    @Override
    public boolean askNewHero() {
        if (!screensReady) {
            startGame();
        } else {
            cardLayout.show(cardPanel, "start");
        }

        newHeroChoice.set(null);
        newHeroLatch = new CountDownLatch(1);

        try {
            newHeroLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }

        return Boolean.TRUE.equals(newHeroChoice.get());
    }

    @Override
    public String askHeroName() {
        if (!screensReady) {
            startGame();
        }

        buildHeroCreationScreen();
        heroNameInput.set(null);
        HeroTypeInput.set(HeroType.values()[0]);
        heroNameLatch = new CountDownLatch(1);
        cardLayout.show(cardPanel, "heroName");

        try {
            heroNameLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        return heroNameInput.get();
    }

    @Override
    public HeroType askHeroType() {
        return HeroTypeInput.get();
    }

    @Override
    public Hero askSelectHero(List<Hero> heroes) {
        return null;
    }

    @Override
    public void showHeroDetails(Hero hero) {
        heroNamePanel.removeAll();
        uiFactory.configureScreenPanel(heroNamePanel);
        JLabel heroDetails = new JLabel("<html>Hero Details:<br/>" +
                "Name: " + hero.getName() + "<br/>" +
                "Class: " + hero.getHeroType().toString() + "<br/>" +
                "Level: " + hero.getLevel() + "<br/>" +
                "XP: " + hero.getXp() + "<br/>" +
                "Attack: " + hero.getAttack() + "<br/>" +
                "Defense: " + hero.getDefense() + "<br/>" +
                "Hit Points: " + hero.getHitPoints() + "</html>");
        uiFactory.applyTitleStyle(heroDetails, 18f);
        heroNamePanel.add(heroDetails, centeredConstraints(0, 20));
        cardLayout.show(cardPanel, "heroName");
        // JOptionPane.showMessageDialog(frame, heroDetails, "Hero Details",
        // JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public DirectionType askDirection() {
        return null;
    }

    @Override
    public boolean askFight() {
        return false;
    }

    @Override
    public boolean askArtifactPickup(Artifact artifactDetails) {
        return false;
    }

    @Override
    public void drawMap(GameMap map, Hero hero) {
        Timer timer = new Timer(2000, event -> {
            gamePanel.removeAll();
            uiFactory.configureScreenPanel(gamePanel);

            JLabel heroDetails = new JLabel("<html>Hero Details:<br/>" +
                    "Name: " + hero.getName() + "<br/>" +
                    "Class: " + hero.getHeroType().toString() + "<br/>" +
                    "Level: " + hero.getLevel() + "<br/>" +
                    "XP: " + hero.getXp() + "<br/>" +
                    "Attack: " + hero.getAttack() + "<br/>" +
                    "Defense: " + hero.getDefense() + "<br/>" +
                    "Hit Points: " + hero.getHitPoints() + "</html>");
            uiFactory.applyTitleStyle(heroDetails, 16f);

            JTextArea mapArea = new JTextArea(buildMapText(map));
            mapArea.setEditable(false);
            mapArea.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 16));

            gamePanel.add(heroDetails, centeredConstraints(0, 20));
            gamePanel.add(mapArea, centeredConstraints(1, 0));
            gamePanel.revalidate();
            gamePanel.repaint();
            cardLayout.show(cardPanel, "game");
        });
        timer.setRepeats(false);
        timer.start();

    }

    private String buildMapText(GameMap map) {
        StringBuilder builder = new StringBuilder();
        int size = map.getSize();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (x == map.getHeroX() && y == map.getHeroY()) {
                    builder.append("H ");
                } else if (map.hasVillain(x, y)) {
                    builder.append("V ");
                } else {
                    builder.append(". ");
                }
            }
            builder.append('\n');
        }

        return builder.toString();
    }

    @Override
    public void showMessage(String message) {
        // Implementation for showing message in GUI
    }

    @Override
    public void showBattleResult(BattleResult result) {
    }

    public void showVictory(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public void showGameOver(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    @Override
    public boolean askPlayAgain() {
        return false;
    }
}
