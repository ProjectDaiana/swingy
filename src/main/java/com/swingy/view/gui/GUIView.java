package com.swingy.view.gui;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.swingy.model.DirectionType;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroType;
import com.swingy.model.map.GameMap;
import com.swingy.view.GameView;
import com.swingy.view.HeroCreationData;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.BlockingQueue;
import java.awt.Color;

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
    private HeroType HeroTypeInput;

    /// private final BlockingQueue<Boolean> newHeroChoice;
    private final BlockingQueue<String> heroNameInput;
    private final BlockingQueue<DirectionType> heroDirectionInput;
    private ImageIcon heroIcon;
    private ImageIcon villainIcon;
    private JLabel[][] gridCells;
    private JPanel mapGridPanel;
    private boolean mapReady; // o el nombre que prefieras
    private CountDownLatch newHeroLatch;
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
        /// remember to make this same as for heroIcon, so that the villain icon is and
        /// add an exception handling in case the resource is not found
        mapReady = false;

        cardPanel.add(startPanel, "start");
        cardPanel.add(heroPanel, "hero");
        cardPanel.add(heroNamePanel, "heroName");
        cardPanel.add(gamePanel, "game");
        cardPanel.add(battlePanel, "battle");

        newHeroChoice = new AtomicReference<>(null);
        /// newHeroChoice = new BlockingQueue<LinkedBlockingQueue<Boolean>>();
        heroNameInput = new LinkedBlockingQueue<String>();
        heroDirectionInput = new LinkedBlockingQueue<DirectionType>();
        uiFactory = new UIFactory();
        screensReady = false;

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

    public ImageIcon loadVillainIcon() {
        String path = "/images/v_" + "dragon" + ".png"; // replace this to select randomly or level based
        return loadIcon(path);
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
        uiFactory.applyTextStyle(title, Typography.Style.TITLE);

        JButton startButton = uiFactory.createButton("START", UIFactory.Style.PRIMARY);
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
        uiFactory.applyTextStyle(heroTitle, Typography.Style.H1);

        JButton createHeroButton = uiFactory.createButton("Create New Hero",
                UIFactory.Style.PRIMARY);
        createHeroButton.addActionListener(e -> {
            newHeroChoice.set(Boolean.TRUE);
            if (newHeroLatch != null) {
                newHeroLatch.countDown();
            }
        });

        JButton loadHeroButton = uiFactory.createButton("Select Existing Hero",
                UIFactory.Style.SECONDARY);
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
        uiFactory.applyTextStyle(heroNameTitle, Typography.Style.H1);

        // Create Input Field and Selector
        JTextField heroNameField = uiFactory.createTextField(18, UIFactory.Style.FIELD);
        JLabel HeroTypeTitle = new JLabel("Choose hero class");
        uiFactory.applyTextStyle(HeroTypeTitle, Typography.Style.H2);

        // Create HeroType Selector
        JComboBox<HeroType> HeroTypeSelector = uiFactory.createSelector(HeroType.values(),
                UIFactory.Style.SELECTOR);

        // Create Submit Button
        JButton submitButton = uiFactory.createButton("Continue", UIFactory.Style.PRIMARY);
        submitButton.addActionListener(e -> {
            String name = heroNameField.getText().trim();
            HeroType selectedClass = (HeroType) HeroTypeSelector.getSelectedItem(); /// I do not like this castnig
            if (!name.isEmpty() && selectedClass != null) {
                try {
                    heroNameInput.put(name);
                    HeroTypeInput = selectedClass;
                    heroIcon = loadHeroIcon(selectedClass);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
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
    public HeroCreationData createNewHero() {
        if (!screensReady) {
            startGame();
        }

        buildHeroCreationScreen();
        cardLayout.show(cardPanel, "heroName");
        String heroName = null;
        try {
            heroName = heroNameInput.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        return new HeroCreationData(heroName, HeroTypeInput);
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
        DirectionType direction = null;
        try {
            direction = heroDirectionInput.take(); // bloquea hasta que el usuario clickea W/A/S/D
            System.out.println("Direction received: " + direction);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return direction;
    }

    @Override
    public boolean askFight() {
        return false;
    }

    @Override
    public boolean askArtifactPickup(Artifact artifactDetails) {
        return false;
    }

    private JLabel drawHeroDetailsBar(Hero hero) {
        JLabel heroDetails = new JLabel("<html>Hero Details:<br/>" +
                "Name: " + hero.getName() + "<br/>" +
                "Class: " + hero.getHeroType().toString() + "<br/>" +
                "Level: " + hero.getLevel() + "<br/>" +
                "XP: " + hero.getXp() + "<br/>" +
                "Attack: " + hero.getAttack() + "<br/>" +
                "Defense: " + hero.getDefense() + "<br/>" +
                "Hit Points: " + hero.getHitPoints() + "</html>");
        uiFactory.applyTextStyle(heroDetails, Typography.Style.BODY);
        gamePanel.add(heroDetails, BorderLayout.NORTH);
        return heroDetails;
    }

    @Override
    public void drawMap(GameMap map, Hero hero) {
        SwingUtilities.invokeLater(() -> {
            if (!mapReady) {
                uiFactory.configureScreenPanel(gamePanel);
                gamePanel.removeAll();
                gamePanel.setLayout(new BorderLayout(10, 10));
                JLabel heroDetailsBar = drawHeroDetailsBar(hero);
                mapGridPanel = initMap(map);
                gamePanel.add(heroDetailsBar, BorderLayout.NORTH);
                gamePanel.add(mapGridPanel, BorderLayout.CENTER);
                mapReady = true;
            } else {
                System.out.println("Updating map for hero position: (" + map.getHeroX() + ", " + map.getHeroY() + ")");
                updateGrid(map, hero);
            }
            gamePanel.revalidate();
            gamePanel.repaint();
            cardLayout.show(cardPanel, "game");
            frame.requestFocusInWindow();
        });
    }

    private void updateGrid(GameMap map, Hero hero) {
        int newX = map.getHeroX();
        int newY = map.getHeroY();
        int prevX = map.getPrevHeroX();
        int prevY = map.getPrevHeroY();

        // Limpiar la celda anterior del héroe (si existe una posición previa válida)
        if (prevX != -1 && prevY != -1 && (prevX != newX || prevY != newY)) {
            JLabel prevCell = gridCells[prevY][prevX];
            prevCell.setIcon(map.hasVillain(prevX, prevY) ? villainIcon : null);
        }
        // Pintar la celda nueva con el héroe
        gridCells[newY][newX].setIcon(heroIcon);
        System.out.println("Moving hero to new position: (" + newX + ", " + newY + ")");
    }

    private JPanel initMap(GameMap map) {
        int size = map.getSize();
        GridLayout grid = new GridLayout(size, size);
        JPanel mapPanel = new JPanel(grid);

        gridCells = new JLabel[size][size];
        mapPanel.setBackground(ColorPalette.DARK_GRAY);
        mapPanel.setOpaque(true); // Asegura que el fondo sea visible
        heroIcon = uiFactory.scaleIcon(heroIcon); // Escala el icono del héroe
        villainIcon = uiFactory.scaleIcon(loadVillainIcon()); // Escala el icono
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                JLabel cell = new JLabel();
                cell.setHorizontalAlignment(JLabel.CENTER);
                cell.setVerticalAlignment(JLabel.CENTER);

                // Make the cell visible
                cell.setBorder(BorderFactory.createLineBorder(Color.WHITE));

                if (x == map.getHeroX() && y == map.getHeroY()) {
                    cell.setIcon(heroIcon); // or heroIcon scaled
                } else if (map.hasVillain(x, y)) {
                    cell.setIcon(villainIcon); // or villainIcon scaled
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
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_UP:
                        submitDirection(DirectionType.NORTH);
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        submitDirection(DirectionType.SOUTH);
                        break;
                    case java.awt.event.KeyEvent.VK_LEFT:
                        submitDirection(DirectionType.WEST);
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        submitDirection(DirectionType.EAST);
                        break;
                }
            }
        });
    }

    private void submitDirection(DirectionType direction) {
        try {
            heroDirectionInput.put(direction);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
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
