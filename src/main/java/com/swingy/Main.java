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

            view = createView(viewType);
            Controller controller = new Controller(view, repository);
            while (true) {
                  view.run(controller);
                  String switchTo = controller.consumePendingViewSwitch();
                  if (switchTo == null)
                        break;
                  view = createView(switchTo);
                  controller.setView(view);
            }
      }

      private static GameView createView(String type) {
            return type.equals("console") ? new ConsoleView() : new GUIView();
      }
}
