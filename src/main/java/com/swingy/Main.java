package com.swingy;

import com.swingy.repository.HeroRepository;
import com.swingy.view.GameView;
import com.swingy.view.console.ConsoleView;
import com.swingy.controller.Controller;
import com.swingy.repository.FileHeroRepository;
import com.swingy.view.gui.GUIView;

public class Main {

      public static void main(String[] args) {
            GameView view;
            HeroRepository repository = new FileHeroRepository();

            if (args.length < 1) {
                  System.out.println("Please specify the view type: console or gui");
                  return;
            }
            if (args.length > 1) {
                  System.out.println("Too many arguments. Please specify only the view type: console or gui");
                  return;
            }
            String viewType = args[0].toLowerCase();
            if (!viewType.equals("console") && !viewType.equals("gui")) {
                  System.out.println("Invalid input. Please specify 'console' or 'gui'");
                  return;
            }

            if (viewType.equals("console")) {
                  System.out.println("Starting in console mode...");
                  view = new ConsoleView(); // or new GuiView() for GUI

            } else {
                  System.out.println("Starting in GUI mode...");
                  view = new GUIView(); // Implement this class for GUI
            }
            Controller controller = new Controller(view, repository); // test hero and map
            new Thread(() -> view.run(controller)).start(); // Start the game loop
      }
}
