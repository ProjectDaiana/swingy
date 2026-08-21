package com.swingy.view.gui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
import java.util.List;
import java.util.concurrent.CountDownLatch;
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

        JLabel heroNameTitle = new JLabel("Name your hero");
        uiFactory.applyTitleStyle(heroNameTitle, 24f);

        JTextField heroNameField = uiFactory.createTextField(18, UIFactory.Components.FIELD);
        JLabel HeroTypeTitle = new JLabel("Choose hero class");
        uiFactory.applyTitleStyle(HeroTypeTitle, 18f);

        JComboBox<HeroType> HeroTypeSelector = uiFactory.createSelector(HeroType.values(),
                UIFactory.Components.SELECTOR);
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
