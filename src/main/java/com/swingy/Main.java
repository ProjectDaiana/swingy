package com.swingy;

import com.swingy.repository.DbHeroRepository;
import com.swingy.repository.HeroRepository;
import com.swingy.view.GameView;
import com.swingy.view.console.ConsoleView;
import com.swingy.controller.Controller;
import com.swingy.repository.FileHeroRepository;
import com.swingy.view.gui.GUIView;

public class Main {

      public static void main(String[] args) {
            if (args.length < 1 ) {
                  System.out.println("Usage: java -jar swingy.jar <console|gui> [--db]");
                  return;
            }
            String viewType = args[0].toLowerCase();
            if (!viewType.equals("console") && !viewType.equals("gui")) {
                  System.out.println("Invalid input. Please specify 'console' or 'gui'");
                  return;
            }
            boolean useDb = args.length > 1 && args[1].equals("--db");
            HeroRepository repository = useDb ? new DbHeroRepository() : new FileHeroRepository();
            GameView view;
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
