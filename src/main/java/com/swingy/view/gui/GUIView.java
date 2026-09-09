package com.swingy.view.gui;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import com.swingy.controller.Controller;
import com.swingy.model.DirectionType;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.hero.HeroType;
import com.swingy.view.GameView;
import com.swingy.view.HeroStats;
import com.swingy.view.MapState;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.awt.Color;

public class GUIView implements GameView {
    Controller controller;
    private final JFrame frame;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private final JPanel startPanel;
    private final JPanel heroPanel;
    private final JPanel heroNamePanel;
    private final JPanel gamePanel;
    private final UIFactory uiFactory;

    private List<HeroStats> loadedHeroes;
    private ImageIcon heroIcon;
    private ImageIcon[] villainIcons;
    private JLabel[][] gridCells;
    private ImageIcon[][] baseIcons;
    private int lastDrawnHeroX = -1;
    private int lastDrawnHeroY = -1;
    private Timer fightTimer;

    private boolean gameStarted = false;

    public GUIView() {
        frame = new JFrame();
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        startPanel = new JPanel();
        heroPanel = new JPanel();
        heroNamePanel = new JPanel();
        gamePanel = new JPanel();

        cardPanel.add(startPanel, "start");
        cardPanel.add(heroPanel, "hero");
        cardPanel.add(heroNamePanel, "heroName");
        cardPanel.add(gamePanel, "game");

        uiFactory = new UIFactory();

        frame.setContentPane(cardPanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        setupKeyBindings();
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalStateException("Missing required resource: " + path);
        }
        return new ImageIcon(url);
    }

    public ImageIcon loadHeroIcon(HeroType heroType) {
        String path = "/images/h_" + heroType.toString().toLowerCase() + ".png";
        return loadIcon(path);
    }

    private ImageIcon[] loadVillainIcons() {
        String[] names = { "dragon", "dracula", "skeleton" };
        ImageIcon[] icons = new ImageIcon[names.length];
        for (int i = 0; i < names.length; i++) {
            icons[i] = uiFactory.scaleIcon(loadIcon("/images/v_" + names[i] + ".png"));
        }
        return icons;
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
        uiFactory.applyTextStyle(title, Typography.Style.TITLE);

        JButton startButton = uiFactory.createButton("START", UIFactory.Style.PRIMARY);
        startButton.addActionListener(e -> cardLayout.show(cardPanel, "hero"));

        startPanel.add(title, centeredConstraints(0, 20));
        startPanel.add(startButton, centeredConstraints(1, 0));
        startPanel.revalidate();
        startPanel.repaint();
        cardLayout.show(cardPanel, "start");
    }

    private void buildHeroChoiceScreen() {
        heroPanel.removeAll();
        uiFactory.configureScreenPanel(heroPanel);

        JLabel heroTitle = new JLabel("Choose hero setup");
        uiFactory.applyTextStyle(heroTitle, Typography.Style.H1);

        JButton createHeroButton = uiFactory.createButton("Create New Hero", UIFactory.Style.PRIMARY);
        createHeroButton.addActionListener(e -> buildHeroCreationScreen());

        JButton loadHeroButton = uiFactory.createButton("Select Existing Hero", UIFactory.Style.SECONDARY);
        loadHeroButton.addActionListener(e -> buildHeroSelectionScreen());

        heroPanel.add(heroTitle, centeredConstraints(0, 20));
        heroPanel.add(createHeroButton, centeredConstraints(1, 12));
        heroPanel.add(loadHeroButton, centeredConstraints(2, 0));
        heroPanel.revalidate();
        heroPanel.repaint();
        cardLayout.show(cardPanel, "heroChoice");

    }

    private void buildHeroSelectionScreen() {
        heroNamePanel.removeAll();
        uiFactory.configureScreenPanel(heroNamePanel);

        JLabel title = new JLabel("Select a Hero");
        uiFactory.applyTextStyle(title, Typography.Style.H1);
        heroNamePanel.add(title, centeredConstraints(0, 20));

        for (int i = 0; i < loadedHeroes.size(); i++) {
            HeroStats h = loadedHeroes.get(i);
            final int index = i;
            JButton btn = uiFactory.createButton(h.name() + " (Level " + h.level() + ")", UIFactory.Style.SECONDARY);
            btn.addActionListener(e -> {
                controller.selectHero(index);
                heroIcon = loadHeroIcon(HeroType.valueOf(loadedHeroes.get(index).type()));
                showHeroDetails(controller.getHeroStats());
                drawMap(controller.getMapState());
            });
            heroNamePanel.add(btn, centeredConstraints(1 + i, 8));
        }

        heroNamePanel.revalidate();
        heroNamePanel.repaint();
    }

    private void buildHeroCreationScreen() {
        heroNamePanel.removeAll();
        uiFactory.configureScreenPanel(heroNamePanel);

        JLabel heroNameTitle = new JLabel("Name your hero");
        uiFactory.applyTextStyle(heroNameTitle, Typography.Style.H1);

        JTextField heroNameField = uiFactory.createTextField(18, UIFactory.Style.FIELD);
        JLabel heroTypeTitle = new JLabel("Choose hero class");
        uiFactory.applyTextStyle(heroTypeTitle, Typography.Style.H2);

        JComboBox<HeroType> heroTypeSelector = uiFactory.createSelector(HeroType.values(), UIFactory.Style.SELECTOR);

        JButton submitButton = uiFactory.createButton("Continue", UIFactory.Style.PRIMARY);
        submitButton.addActionListener(e -> {
            String name = heroNameField.getText().trim();
            HeroType selectedClass = (HeroType) heroTypeSelector.getSelectedItem();
            if (!name.isEmpty() && selectedClass != null) {
                controller.createHero(name, selectedClass);
                heroIcon = loadHeroIcon(selectedClass);
                showHeroDetails(controller.getHeroStats());
                drawMap(controller.getMapState());
            }
        });

        heroNamePanel.add(heroNameTitle, centeredConstraints(0, 20));
        heroNamePanel.add(heroNameField, centeredConstraints(1, 12));
        heroNamePanel.add(heroTypeTitle, centeredConstraints(2, 12));
        heroNamePanel.add(heroTypeSelector, centeredConstraints(3, 12));
        heroNamePanel.add(submitButton, centeredConstraints(4, 0));
        heroNamePanel.revalidate();
        heroNamePanel.repaint();
        cardLayout.show(cardPanel, "heroName");
    }

    @Override
    public void showHeroDetails(HeroStats stats) {
        heroNamePanel.removeAll();
        uiFactory.configureScreenPanel(heroNamePanel);
        JLabel heroDetails = new JLabel("<html>Hero Details:<br/>" +
                "Name: " + stats.name() + "<br/>" +
                "Class: " + stats.type() + "<br/>" +
                "Level: " + stats.level() + "<br/>" +
                "XP: " + stats.xp() + "<br/>" +
                "Attack: " + stats.attack() + "<br/>" +
                "Defense: " + stats.defense() + "<br/>" +
                "Hit Points: " + stats.hitPoints() + "</html>");
        uiFactory.applyTitleStyle(heroDetails, 18f);

        heroNamePanel.add(heroDetails, centeredConstraints(0, 20));
        cardLayout.show(cardPanel, "heroName");
    }

    @Override
    public void onBattleStart() {
        MapState current = controller.getMapState();
        System.out.println("[BATTLE] Battle started at (" + current.heroX() + ", " + current.heroY() + ")");
        updateGrid(current);
        fightAnimation(current.heroX(), current.heroY());
    }

    @Override
    public boolean askFight() {
        System.out.println("[BATTLE] Asking player: fight or flee?");
        int response = JOptionPane.showConfirmDialog(frame, "Do you want to fight?", "Fight",
                JOptionPane.YES_NO_OPTION);
        boolean fight = response == JOptionPane.YES_OPTION;
        System.out.println("[BATTLE] Player chose: " + (fight ? "FIGHT" : "FLEE"));
        return fight;
    }

    @Override
    public boolean askArtifactPickup(Artifact artifactDetails) {
        System.out.println(
                "[ARTIFACT] Dropped: " + artifactDetails.getType() + " (value: " + artifactDetails.getValue() + ")");
        MapState current = controller.getMapState();
        setCellIcon(current.heroX(), current.heroY(), loadArtifactIcon(artifactDetails.getType()));
        int response = JOptionPane.showConfirmDialog(frame, "Do you want to pick up this artifact?", "Artifact Pickup",
                JOptionPane.YES_NO_OPTION);
        boolean pickup = response == JOptionPane.YES_OPTION;
        System.out.println("[ARTIFACT] Player chose: " + (pickup ? "PICK UP" : "LEAVE"));
        return pickup;
    }

    private JLabel drawHeroDetailsBar(HeroStats hero) {
        JLabel heroDetails = new JLabel("<html>Hero Details:<br/>" +
                "Name: " + hero.name() + "<br/>" +
                "Class: " + hero.type() + "<br/>" +
                "Level: " + hero.level() + "<br/>" +
                "XP: " + hero.xp() + "<br/>" +
                "Attack: " + hero.attack() + "<br/>" +
                "Defense: " + hero.defense() + "<br/>" +
                "Hit Points: " + hero.hitPoints() + "</html>");
        uiFactory.applyTextStyle(heroDetails, Typography.Style.BODY);
        return heroDetails;
    }

    @Override
    public void run(Controller controller) {
        this.controller = controller;
        loadedHeroes = controller.loadHeroes();
        SwingUtilities.invokeLater(() -> {
            buildStartScreen();
            buildHeroChoiceScreen();
        });

    }

    @Override
    public void drawMap(MapState state) {
        SwingUtilities.invokeLater(() -> {
            if (!gameStarted) {
                uiFactory.configureScreenPanel(gamePanel);
                gamePanel.removeAll();
                gamePanel.setLayout(new BorderLayout(10, 10));
                JLabel heroDetailsBar = drawHeroDetailsBar(controller.getHeroStats());
                gamePanel.add(heroDetailsBar, BorderLayout.NORTH);
                gamePanel.add(initMap(state), BorderLayout.CENTER);
                gameStarted = true;
            } else {
                if (fightTimer != null) {
                    fightTimer.stop();
                    fightTimer = null;
                }
                System.out.println("Updating map for hero position: (" + state.heroX() + ", " + state.heroY() + ")");
                updateGrid(state);
            }
            gamePanel.revalidate();
            gamePanel.repaint();
            cardLayout.show(cardPanel, "game");
            frame.requestFocusInWindow();
        });
    }

    private void updateGrid(MapState map) {
        int newX = map.heroX();
        int newY = map.heroY();

        if (lastDrawnHeroX != -1 && lastDrawnHeroY != -1 && (lastDrawnHeroX != newX || lastDrawnHeroY != newY)) {
            gridCells[lastDrawnHeroY][lastDrawnHeroX].setIcon(baseIcons[lastDrawnHeroY][lastDrawnHeroX]);
        }
        gridCells[newY][newX].setIcon(heroIcon);
        lastDrawnHeroX = newX;
        lastDrawnHeroY = newY;
    }

    private JPanel initMap(MapState map) {
        int size = map.size();
        JPanel mapPanel = new JPanel(new GridLayout(size, size));

        gridCells = new JLabel[size][size];
        baseIcons = new ImageIcon[size][size];
        mapPanel.setBackground(ColorPalette.DARK_GRAY);
        mapPanel.setOpaque(true);
        heroIcon = uiFactory.scaleIcon(heroIcon);
        villainIcons = loadVillainIcons();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                JLabel cell = new JLabel();
                cell.setHorizontalAlignment(JLabel.CENTER);
                cell.setVerticalAlignment(JLabel.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

                if (x == map.heroX() && y == map.heroY()) {
                    cell.setIcon(heroIcon);
                    lastDrawnHeroX = x;
                    lastDrawnHeroY = y;
                } else {
                    for (int[] pos : map.villainPositions()) {
                        if (pos[0] == x && pos[1] == y) {
                            ImageIcon icon = villainIcons[(int) (Math.random() * villainIcons.length)];
                            cell.setIcon(icon);
                            baseIcons[y][x] = icon;
                            break;
                        }
                    }
                }

                gridCells[y][x] = cell;
                mapPanel.add(cell);
            }
        }

        return mapPanel;
    }

    private void setupKeyBindings() {
        frame.setFocusable(true);
        frame.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (!gameStarted)
                    return;
                DirectionType direction = null;
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_UP:
                        direction = DirectionType.NORTH;
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        direction = DirectionType.SOUTH;
                        break;
                    case java.awt.event.KeyEvent.VK_LEFT:
                        direction = DirectionType.WEST;
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        direction = DirectionType.EAST;
                        break;
                }
                if (direction != null) {
                    controller.onMove(direction);
                    if (controller.isAtBorder()) {
                        showVictory("You reached the border! You win!");
                        controller.endGame();
                    } else if (controller.isHeroDefeated()) {
                        showGameOver("Your hero was defeated. Game over.");
                        controller.endGame();
                    } else {
                        drawMap(controller.getMapState());
                    }
                }
            }
        });
    }

    @Override
    public void showMessage(String message) {
        System.out.println("[MSG] " + message);
    }

    @Override
    public void showBattleResult(BattleResult result) {
        if (fightTimer != null) {
            fightTimer.stop();
            fightTimer = null;
        }
        MapState current = controller.getMapState();
        int x = current.heroX();
        int y = current.heroY();
        boolean won = result.getResult() == BattleResult.Result.WIN;
        if (won) {
            baseIcons[y][x] = null;
            System.out.println("[BATTLE] WIN at (" + x + ", " + y + ")" +
                    (result.getArtifact() != null ? " | artifact: " + result.getArtifact().getType() : ""));
        } else {
            System.out.println("[BATTLE] LOSE at (" + x + ", " + y + ")");
        }
        setCellIcon(x, y, loadResultIcon(won, result.getArtifact()));
    }

    @Override
    public void showVictory(String message) {
        System.out.println("[VICTORY] " + message);
        JOptionPane.showMessageDialog(null, message);
    }

    @Override
    public void showGameOver(String message) {
        System.out.println("[GAME OVER] " + message);
        JOptionPane.showMessageDialog(null, message);
    }

    public boolean askPlayAgain() {
        return false;
    }

    private ImageIcon loadResultIcon(boolean won, Artifact artifact) {
        if (won) {
            if (artifact != null) {
                return uiFactory.scaleIcon(loadIcon(
                        "/images/a_" + artifact.getType().toString().toLowerCase() + ".png"));
            }
            return heroIcon;
        }
        return uiFactory.scaleIcon(loadIcon("/images/lose.png"));
    }

    private void setCellIcon(int x, int y, ImageIcon icon) {
        gridCells[y][x].setIcon(icon);
    }

    private void fightAnimation(int x, int y) {
        if (fightTimer != null) {
            fightTimer.stop();
        }
        ImageIcon icon1 = uiFactory.scaleIcon(loadIcon("/images/fight_1.png"));
        ImageIcon icon2 = uiFactory.scaleIcon(loadIcon("/images/fight_2.png"));
        boolean[] showFirst = { true };
        fightTimer = new Timer(300, e -> {
            setCellIcon(x, y, showFirst[0] ? icon1 : icon2);
            showFirst[0] = !showFirst[0];
            System.out.println("ANIMATION RUNNING at position: (" + x + ", " + y + ")");
        });
        System.out.println("Starting fight animation at position: (" + x + ", " + y + ")");
        fightTimer.start();
    }

    private ImageIcon loadArtifactIcon(com.swingy.model.artifact.ArtifactType type) {
        return uiFactory.scaleIcon(loadIcon("/images/a_" + type.toString().toLowerCase() + ".png"));
    }
}
