package whisperingfalls;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** Owns the seven screens and their navigation. */
public class GameWindow {

    private static JFrame window;
    private static CardLayout cardLayout;
    private static JPanel pages;
    private static MenuPanel menuPanel;
    private static Sloth1Panel sloth1Panel;
    private static Sloth2Panel sloth2Panel;
    private static Sloth3Panel sloth3Panel;
    private static Sloth4Panel sloth4Panel;
    private static Sloth5Panel sloth5Panel;
    private static EndingPanel endingPanel;

    public void start() {
        window = new JFrame("Whispering Falls");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createScreens();
        window.setContentPane(pages);
        window.setSize(1280, 720);
        window.setMinimumSize(new Dimension(800, 450));
        window.setLocationRelativeTo(null);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setVisible(true);
        showMenu();
    }

    private void createScreens() {
        cardLayout = new CardLayout();
        pages = new JPanel(cardLayout);
        menuPanel = new MenuPanel(GameWindow::startNewGame);
        sloth1Panel = new Sloth1Panel();
        sloth2Panel = new Sloth2Panel();
        sloth3Panel = new Sloth3Panel();
        sloth4Panel = new Sloth4Panel();
        sloth5Panel = new Sloth5Panel();
        endingPanel = new EndingPanel(GameWindow::startNewGame, GameWindow::showMenu);

        pages.add(menuPanel, "MENU");
        pages.add(sloth1Panel, "SLOTH_1");
        pages.add(sloth2Panel, "SLOTH_2");
        pages.add(sloth3Panel, "SLOTH_3");
        pages.add(sloth4Panel, "SLOTH_4");
        pages.add(sloth5Panel, "SLOTH_5");
        pages.add(endingPanel, "ENDING");
    }

    public static void startNewGame() {
        if (pages == null) return;
        sloth1Panel.startScene();
        sloth2Panel.startScene();
        sloth3Panel.startScene();
        sloth4Panel.startScene();
        sloth5Panel.startScene();
        showSloth1();
    }

    public static void showMenu() { showCard("MENU"); }
    public static void showSloth1() { showScene("SLOTH_1", sloth1Panel); }
    public static void showSloth2() { showScene("SLOTH_2", sloth2Panel); }
    public static void showSloth3() { showScene("SLOTH_3", sloth3Panel); }
    public static void showSloth4() { showScene("SLOTH_4", sloth4Panel); }
    public static void showSloth5() { showScene("SLOTH_5", sloth5Panel); }
    public static void showEnding() { showCard("ENDING"); }

    private static void showScene(String name, Sloth1Panel scene) {
        if (pages == null || scene == null) return;
        scene.startScene();
        showCard(name);
    }

    private static void showScene(String name, Sloth2Panel scene) {
        if (pages == null || scene == null) return;
        scene.startScene();
        showCard(name);
    }

    private static void showScene(String name, Sloth3Panel scene) {
        if (pages == null || scene == null) return;
        scene.startScene();
        showCard(name);
    }

    private static void showScene(String name, Sloth4Panel scene) {
        if (pages == null || scene == null) return;
        scene.startScene();
        showCard(name);
    }

    private static void showScene(String name, Sloth5Panel scene) {
        if (pages == null || scene == null) return;
        scene.startScene();
        showCard(name);
    }

    private static void showCard(String name) {
        if (pages == null || cardLayout == null) return;
        cardLayout.show(pages, name);
        pages.revalidate();
        pages.repaint();
    }
}
