# Swingy

A Java RPG built for 42 school. Play through a text-based console or a Swing GUI — same game logic, two different views.

## Build & Run

```bash
mvn clean package
java -jar target/swingy.jar console
java -jar target/swingy.jar gui
```

Requires Java 17+.

---

## Architecture

The project follows the **MVC pattern**. The controller drives the game loop and delegates all input/output through a `GameView` interface, which both `ConsoleView` and `GUIView` implement.

```mermaid
graph TD
    Main --> Controller
    Controller --> GameView
    GameView -->|implements| ConsoleView
    GameView -->|implements| GUIView
    Controller --> HeroRepository
    HeroRepository -->|implements| FileHeroRepository
    Controller --> GameMap
    Controller --> Battle
    GameMap --> Hero
    GameMap --> Villain
    Battle --> Hero
    Battle --> Villain
```

### Package layout

```
com.swingy/
├── Main.java
├── controller/
│   └── Controller.java          # game loop, battle logic, hero setup
├── model/
│   ├── hero/
│   │   ├── Hero.java            # stats, level-up, artifact slot
│   │   ├── HeroBuilder.java
│   │   └── HeroType.java        # WARRIOR, WIZARD, ROGUE
│   ├── villain/
│   │   └── Villain.java
│   ├── artifact/
│   │   ├── Artifact.java        # base class with value field
│   │   ├── Weapon.java
│   │   ├── Armor.java
│   │   └── Helm.java
│   ├── battle/
│   │   ├── Battle.java
│   │   └── BattleResult.java
│   ├── map/
│   │   └── GameMap.java         # grid, hero/villain positions
│   ├── DirectionType.java
│   └── GameResult.java
├── view/
│   ├── GameView.java            # interface shared by both views
│   ├── HeroCreationData.java    # record DTO
│   ├── console/
│   │   └── ConsoleView.java
│   └── gui/
│       ├── GUIView.java
│       ├── UIFactory.java       # component factory
│       ├── ColorPalette.java    # color tokens
│       ├── Typography.java      # text style tokens
│       └── Helpers.java
└── repository/
    ├── HeroRepository.java      # interface
    └── FileHeroRepository.java  # text-file persistence (mandatory)
```

---

## Design System

The GUI uses a small design system centralised in three files. All visual decisions go through these — no raw colours or font sizes scattered across view code.

### Color palette

| Token | Hex | Used for |
|---|---|---|
| `BLACK` | `#111111` | Screen background |
| `DARK_GRAY` | `#151515` | Map grid, text fields, selectors |
| `LIGHT_GRAY` | `#EBEBEB` | All text labels |
| `WHITE` | `#FFFFFF` | Cell borders |

### Typography scale

| Style | Size | Weight | Color |
|---|---|---|---|
| `TITLE` | 28px | Bold | LIGHT_GRAY |
| `H1` | 24px | Bold | LIGHT_GRAY |
| `H2` | 18px | Bold | LIGHT_GRAY |
| `BODY` | 16px | Plain | LIGHT_GRAY |

### UIFactory

`UIFactory` is the single entry point to create styled components. Nothing in `GUIView` calls `new JButton()` directly — it always goes through the factory.

```mermaid
graph TD
    GUIView -->|createButton PRIMARY| UIFactory
    GUIView -->|createButton SECONDARY| UIFactory
    GUIView -->|createTextField FIELD| UIFactory
    GUIView -->|createSelector SELECTOR| UIFactory
    GUIView -->|applyTextStyle| UIFactory
    GUIView -->|configureScreenPanel| UIFactory
    UIFactory --> ColorPalette
    UIFactory --> Typography
```

---
---

## Gameplay Rules

- **Map size:** `(level - 1) * 5 + 10 - (level % 2)`
- **Hero starts** at the center; **wins** by reaching any border cell
- **XP to next level:** `level * 1000 + (level - 1)^2 * 450`
- **Encounter:** fight or run (50 % escape chance)
- **Artifacts:** Weapon (+attack), Armor (+defense), Helm (+hit points) — value scales with villain strength; hero decides to keep or leave after winning

---

## Sprites

All icons are 24 × 24 px pixel art.

### Heroes

| Warrior | Rogue | Wizard |
|:---:|:---:|:---:|
| <img src="src/main/resources/images/h_warrior.png" width="24" height="24"> | <img src="src/main/resources/images/h_rogue.png" width="24" height="24"> | <img src="src/main/resources/images/h_wizard.png" width="24" height="24"> |

### Villains

| Dragon | Dracula | Skeleton |
|:---:|:---:|:---:|
| <img src="src/main/resources/images/v_dragon.png" width="24" height="24"> | <img src="src/main/resources/images/v_dracula.png" width="24" height="24"> | <img src="src/main/resources/images/v_skeleton.png" width="24" height="24"> |

### Artifacts

| Weapon | Armor | Helm |
|:---:|:---:|:---:|
| <img src="src/main/resources/images/a_weapon.png" width="24" height="24"> | <img src="src/main/resources/images/a_armor.png" width="24" height="24"> | <img src="src/main/resources/images/a_helm.png" width="24" height="24"> |

### Battle

| Fight | Fight 2 | Lose |
|:---:|:---:|:---:|
| <img src="src/main/resources/images/fight_1.png" width="24" height="24"> | <img src="src/main/resources/images/fight_2.png" width="24" height="24"> | <img src="src/main/resources/images/lose.png" width="24" height="24"> |
