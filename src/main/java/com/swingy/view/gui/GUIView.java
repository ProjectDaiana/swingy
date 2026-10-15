package com.swingy.view.gui;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import com.swingy.controller.Controller;
import com.swingy.model.DirectionType;
import com.swingy.model.GameResult;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.hero.HeroType;
import com.swingy.view.Banner;
import com.swingy.view.GameView;
import com.swingy.view.HeroStats;
import com.swingy.view.MapState;
import com.swingy.view.ArtifactStats;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

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
    private final IconLoader iconLoader;

    private List<HeroStats> loadedHeroes;
    private ImageIcon heroIcon;
    private ImageIcon[] villainIcons;
    private JLabel[][] gridCells;
    private ImageIcon[][] baseIcons;
    private int lastDrawnHeroX = -1;
    private int lastDrawnHeroY = -1;
    private Timer fightTimer;

    private JPanel heroDetailsBar;
    private JLabel statLevel, statXp, statAttack, statDefense, statHp, statEquipment;
    private boolean gameStarted = false;

    public GUIView() {
        applyDialogStyle();
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
        iconLoader = new IconLoader();

        frame.setContentPane(cardPanel);
        frame.setSize(960, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        setupKeyBindings();
    }

    private void buildStartScreen() {
        uiFactory.configureScreenPanel(startPanel);

        JButton startButton = uiFactory.createButton("START", UIFactory.Style.PRIMARY);
        startButton.addActionListener(e -> buildHeroChoiceScreen());

        startPanel.add(buildTitleLabel(), ViewUtils.centeredConstraints(0, 20));
        startPanel.add(startButton, ViewUtils.centeredConstraints(1, 0));
        startPanel.revalidate();
        startPanel.repaint();
        cardLayout.show(cardPanel, "start");
    }

    private void buildHeroChoiceScreen() {
        loadedHeroes = controller.loadHeroes();
        heroPanel.removeAll();
        uiFactory.configureScreenPanel(heroPanel);

        JLabel heroTitle = new JLabel("You need a Hero");
        uiFactory.applyTextStyle(heroTitle, Typography.Style.H1);

        JButton createHeroButton = uiFactory.createButton("Create New Hero", UIFactory.Style.SECONDARY);
        createHeroButton.addActionListener(e -> buildHeroCreationScreen());

        JButton loadHeroButton = uiFactory.createButton("Select Existing Hero", UIFactory.Style.SECONDARY);
        loadHeroButton.setEnabled(!loadedHeroes.isEmpty());
        loadHeroButton.addActionListener(e -> buildHeroSelectionScreen());

        heroPanel.add(heroTitle, ViewUtils.centeredConstraints(0, 20));
        heroPanel.add(createHeroButton, ViewUtils.centeredConstraints(1, 12));
        heroPanel.add(loadHeroButton, ViewUtils.centeredConstraints(2, 0));
        heroPanel.revalidate();
        heroPanel.repaint();
        cardLayout.show(cardPanel, "hero");
    }

    private DefaultListCellRenderer heroIconRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean hasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                String text = null, type = null;
                if (value instanceof HeroStats h) {
                    text = h.name() + ", Level " + h.level();
                    type = h.type();
                } else if (value instanceof HeroType t) {
                    text = ViewUtils.toTitleCase(t.name());
                    type = t.name();
                }
                if (text != null) {
                    lbl.setText(text);
                    lbl.setIcon(buildHeroIcon(type));
                    lbl.setIconTextGap(10);
                }
                if (index == -1) {
                    lbl.setBorder(BorderFactory.createEmptyBorder());
                    lbl.setOpaque(true);
                    lbl.setBackground(ColorPalette.DARK_GRAY);
                } else {
                    lbl.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                    lbl.setOpaque(isSelected);
                    if (isSelected)
                        lbl.setBackground(ColorPalette.ACCENT);
                }
                lbl.setForeground(isSelected ? ColorPalette.BLACK : ColorPalette.WHITE);
                return lbl;
            }
        };
    }

    private ImageIcon buildHeroIcon(String type) {
        int iconSize = 24, pad = 2, total = iconSize + pad * 2;
        BufferedImage composite = new BufferedImage(total, total, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = composite.createGraphics();
        g.setColor(ColorPalette.DARK_GRAY);
        g.fillRect(0, 0, total, total);
        g.drawImage(iconLoader.hero(type).getImage(), pad, pad, iconSize, iconSize, null);
        g.dispose();
        return new ImageIcon(composite);
    }

    private JComboBox<HeroStats> buildHeroDropdown() {
        HeroStats[] options = loadedHeroes.toArray(new HeroStats[0]);
        JComboBox<HeroStats> selector = uiFactory.createSelector(options);
        selector.setRenderer(heroIconRenderer());
        return selector;
    }

    private void buildHeroSelectionScreen() {
        heroNamePanel.removeAll();
        uiFactory.configureScreenPanel(heroNamePanel);

        JLabel title = new JLabel("Select a Hero");
        uiFactory.applyTextStyle(title, Typography.Style.H2);
        heroNamePanel.add(title, ViewUtils.centeredConstraints(0, 20));

        JComboBox<HeroStats> heroSelector = buildHeroDropdown();
        JButton selectButton = uiFactory.createButton("Select", UIFactory.Style.PRIMARY);
        selectButton.addActionListener(e -> {
            int index = heroSelector.getSelectedIndex();
            controller.selectHero(index);
            heroIcon = iconLoader.hero(controller.getHeroStats().type());
            showHeroDetails(controller.getHeroStats());
            drawMap(controller.getMapState());
        });

        heroNamePanel.add(heroSelector, ViewUtils.centeredConstraints(1, 12));
        heroNamePanel.add(selectButton, ViewUtils.centeredConstraints(2, 0));
        heroNamePanel.revalidate();
        heroNamePanel.repaint();
        cardLayout.show(cardPanel, "heroName");
    }

    private void buildHeroCreationScreen() {
        heroNamePanel.removeAll();
        uiFactory.configureScreenPanel(heroNamePanel);

        JLabel heroNameTitle = new JLabel("Name your hero");
        uiFactory.applyTextStyle(heroNameTitle, Typography.Style.H2);

        JTextField heroNameField = uiFactory.createTextField();
        JLabel heroTypeTitle = new JLabel("Choose hero class");
        uiFactory.applyTextStyle(heroTypeTitle, Typography.Style.H2);

        JComboBox<HeroType> heroTypeSelector = uiFactory.createSelector(HeroType.values());
        heroTypeSelector.setRenderer(heroIconRenderer());

        JButton submitButton = uiFactory.createButton("Continue", UIFactory.Style.PRIMARY);
        submitButton.addActionListener(e -> {
            String name = heroNameField.getText().trim();
            HeroType selectedClass = (HeroType) heroTypeSelector.getSelectedItem();
            if (!name.isEmpty() && selectedClass != null) {
                controller.createHero(name, selectedClass);
                heroIcon = iconLoader.hero(selectedClass);
                showHeroDetails(controller.getHeroStats());
                drawMap(controller.getMapState());
            }
        });

        heroNamePanel.add(heroNameTitle, ViewUtils.centeredConstraints(0, 20));
        heroNamePanel.add(heroNameField, ViewUtils.centeredConstraints(1, 12));
        heroNamePanel.add(heroTypeTitle, ViewUtils.centeredConstraints(2, 12));
        heroNamePanel.add(heroTypeSelector, ViewUtils.centeredConstraints(3, 12));
        heroNamePanel.add(submitButton, ViewUtils.centeredConstraints(4, 0));
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

        heroNamePanel.add(heroDetails, ViewUtils.centeredConstraints(0, 20));
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
        MapState current = controller.getMapState();
        ImageIcon villainIcon = baseIcons[current.heroY()][current.heroX()];
        int response = JOptionPane.showConfirmDialog(frame, "A villain blocks your path!\nDo you want to fight?",
                "Fight", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, villainIcon);
        boolean fight = response == JOptionPane.YES_OPTION;
        System.out.println("[BATTLE] Player chose: " + (fight ? "FIGHT" : "FLEE"));
        return fight;
    }

    @Override
    public boolean askArtifactPickup(ArtifactStats artifactStats) {
        ImageIcon artifactIcon = iconLoader.artifact(artifactStats.type());
        int response = JOptionPane.showConfirmDialog(frame,
                "<html>Found a " + artifactStats.type().toLowerCase() + "<br/>"
                        + artifactStats.description() + " will increase by " + artifactStats.value() + "<br/>"
                        + "Pick it up?</html>",
                "Artifact Found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, artifactIcon);
        return response == JOptionPane.YES_OPTION;
    }

    @Override
    public void run(Controller controller) {
        this.controller = controller;
        iconLoader.preloadAll();
        buildStartScreen();
    }

    @Override
    public void drawMap(MapState state) {
        SwingUtilities.invokeLater(() -> {
            if (!gameStarted) {
                gamePanel.removeAll();
                gamePanel.setLayout(new BorderLayout(0, 12));
                gamePanel.setBackground(ColorPalette.BLACK);
                gamePanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
                initHeroDetailsBar(controller.getHeroStats());
                JPanel northPanel = new JPanel(new BorderLayout(0, 8));
                northPanel.setBackground(ColorPalette.BLACK);
                northPanel.add(buildTitleLabel(), BorderLayout.NORTH);
                northPanel.add(heroDetailsBar, BorderLayout.SOUTH);
                gamePanel.add(northPanel, BorderLayout.NORTH);
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

    private JLabel buildTitleLabel() {
        JLabel lbl = new JLabel(ViewUtils.ansiToHtml(Banner.ART_ANSI), JLabel.CENTER);
        lbl.setBackground(ColorPalette.BLACK);
        return lbl;
    }

    private void initHeroDetailsBar(HeroStats stats) {
        statLevel = uiFactory.createStatCell(ViewUtils.statHtml("Lv", stats.level()), Typography.Style.STAT_ACCENT_BOLD);
        JLabel statName = uiFactory.createStatCell(ViewUtils.statHtml("", stats.name()), Typography.Style.STAT_ACCENT_BOLD);
        JLabel statType = uiFactory.createStatCell(ViewUtils.statHtml("", ViewUtils.toTitleCase(stats.type())), Typography.Style.STAT_ACCENT_BOLD);
        statXp = uiFactory.createStatCell(ViewUtils.statHtml("XP", stats.xp()), Typography.Style.STAT);
        statAttack = uiFactory.createStatCell(ViewUtils.statHtml("ATK", stats.attack()), Typography.Style.STAT);
        statDefense = uiFactory.createStatCell(ViewUtils.statHtml("DEF", stats.defense()), Typography.Style.STAT);
        statHp = uiFactory.createStatCell(ViewUtils.statHtml("HP", stats.hitPoints()), Typography.Style.STAT);
        statEquipment = uiFactory.createStatCell(ViewUtils.buildEquipmentText(stats));

        JPanel statsRow = new JPanel(new GridLayout(1, 7, 1, 0));
        statsRow.setBackground(ColorPalette.DARK_GRAY);
        statsRow.setOpaque(true);
        for (JLabel cell : new JLabel[] { statLevel, statName, statType, statXp, statAttack, statDefense, statHp })
            statsRow.add(cell);

        heroDetailsBar = new JPanel(new BorderLayout());
        heroDetailsBar.setBorder(BorderFactory.createLineBorder(ColorPalette.WHITE));
        heroDetailsBar.setBackground(ColorPalette.BLACK);
        heroDetailsBar.setOpaque(true);
        heroDetailsBar.add(statsRow, BorderLayout.NORTH);
        heroDetailsBar.add(statEquipment, BorderLayout.SOUTH);
    }

    private void updateHeroDetailsBar() {
        HeroStats stats = controller.getHeroStats();
        statLevel.setText(ViewUtils.statHtml("Lv", stats.level()));
        statXp.setText(ViewUtils.statHtml("XP", stats.xp()));
        statAttack.setText(ViewUtils.statHtml("ATK", stats.attack()));
        statDefense.setText(ViewUtils.statHtml("DEF", stats.defense()));
        statHp.setText(ViewUtils.statHtml("HP", stats.hitPoints()));
        statEquipment.setText(ViewUtils.buildEquipmentText(stats));
    }

    private JPanel initMap(MapState map) {
        int size = map.size();
        JPanel mapPanel = new JPanel(new GridLayout(size, size));

        gridCells = new JLabel[size][size];
        baseIcons = new ImageIcon[size][size];
        mapPanel.setBorder(BorderFactory.createLineBorder(ColorPalette.WHITE));
        mapPanel.setBackground(ColorPalette.DARK_GRAY);
        mapPanel.setOpaque(true);
        villainIcons = iconLoader.allVillains();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                JLabel cell = new JLabel();
                cell.setHorizontalAlignment(JLabel.CENTER);
                cell.setVerticalAlignment(JLabel.CENTER);
                int top = (y == 0) ? 0 : 1;
                int left = (x == 0) ? 0 : 1;
                cell.setBorder(BorderFactory.createMatteBorder(top, left, 0, 0, Color.DARK_GRAY));
                boolean isEdge = (x == 0 || x == size - 1 || y == 0 || y == size - 1);
                cell.setOpaque(true);
                cell.setBackground(isEdge ? ColorPalette.DARK_GRAY : ColorPalette.BLACK);

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

    private void applyDialogStyle() {
        UIManager.put("OptionPane.background", ColorPalette.BLACK);
        UIManager.put("Panel.background", ColorPalette.BLACK);
        UIManager.put("OptionPane.messageForeground", ColorPalette.LIGHT_GRAY);
        UIManager.put("OptionPane.messageFont", Typography.BASE_FONT.deriveFont(16f));
        UIManager.put("Button.background", ColorPalette.ACCENT);
        UIManager.put("Button.foreground", ColorPalette.BLACK);
        UIManager.put("Button.font", Typography.BASE_FONT.deriveFont(14f));
    }

    private void setupKeyBindings() {
        frame.setFocusable(true);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameStarted)
                    return;
                DirectionType direction = null;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        direction = DirectionType.NORTH;
                        break;
                    case KeyEvent.VK_DOWN:
                        direction = DirectionType.SOUTH;
                        break;
                    case KeyEvent.VK_LEFT:
                        direction = DirectionType.WEST;
                        break;
                    case KeyEvent.VK_RIGHT:
                        direction = DirectionType.EAST;
                        break;
                }
                if (direction != null) {
                    GameResult result = controller.onMove(direction);
                    if (result == GameResult.ONGOING) {
                        updateHeroDetailsBar();
                        drawMap(controller.getMapState());
                    } else {
                        gameStarted = false;
                        boolean keepPlaying = controller.endGame(result);
                        if (keepPlaying) {
                            updateHeroDetailsBar();
                            lastDrawnHeroX = -1;
                            lastDrawnHeroY = -1;
                            drawMap(controller.getMapState());
                        } else {
                            SwingUtilities.invokeLater(() -> buildStartScreen());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Message",
                JOptionPane.PLAIN_MESSAGE, iconLoader.fight(1));

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
            JOptionPane.showMessageDialog(frame, "You won this battle!", "Victory",
                    JOptionPane.PLAIN_MESSAGE, heroIcon);
        } else {
            baseIcons[y][x] = iconLoader.lose();
        }
        setCellIcon(x, y, resultIcon(won, result.getArtifact()));
    }

    @Override
    public void showVictory(String message) {
        JOptionPane.showMessageDialog(frame, message, "Victory", JOptionPane.PLAIN_MESSAGE, iconLoader.win());
    }

    @Override
    public void showLevelUp(int newLevel) {
        JOptionPane.showMessageDialog(frame, "Level up! You are now level " + newLevel + "!",
                "Level Up", JOptionPane.PLAIN_MESSAGE, heroIcon);
    }

    @Override
    public void showGameOver(String message) {
        JOptionPane.showMessageDialog(frame, message, "Game Over", JOptionPane.PLAIN_MESSAGE, iconLoader.lose());
    }

    public boolean askPlayAgain() {
        return false;
    }

    private ImageIcon resultIcon(boolean won, Artifact artifact) {
        if (won) {
            return artifact != null ? iconLoader.artifact(artifact.getType()) : heroIcon;
        }
        return iconLoader.lose();
    }

    private void setCellIcon(int x, int y, ImageIcon icon) {
        gridCells[y][x].setIcon(icon);
    }

    private void fightAnimation(int x, int y) {
        if (fightTimer != null) {
            fightTimer.stop();
        }
        ImageIcon icon1 = iconLoader.fight(1);
        ImageIcon icon2 = iconLoader.fight(2);
        boolean[] showFirst = { true };
        fightTimer = new Timer(300, e -> {
            setCellIcon(x, y, showFirst[0] ? icon1 : icon2);
            showFirst[0] = !showFirst[0];
        });
        System.out.println("Starting fight animation at position: (" + x + ", " + y + ")");
        fightTimer.start();
    }

    @Override
    public boolean askNewGame(String message) {
        int response = JOptionPane.showConfirmDialog(frame, message,
                "New Game", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, heroIcon);
        return response == JOptionPane.YES_OPTION;
    }
}
