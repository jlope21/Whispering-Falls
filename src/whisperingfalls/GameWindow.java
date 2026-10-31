/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package whisperingfalls;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author User
 */

public class GameWindow {

    private static JFrame window;
    private static CardLayout cardLayout;
    private static JPanel pages;

    private static MenuPanel menuPanel;
    private static StoryScenePanel storyScenePanel;
    private static HowToPlayPanel howToPlayPanel;
    private static Sloth1Panel sloth1Panel;
    private static ForestPathPanel forestPathPanel;

    private static JButton soundButton;

    private static BufferedImage onImage;
    private static BufferedImage offImage;

    private static boolean musicEnabled = true;
    private static String selectedCharacter;

    public void start() {
        window = new JFrame("Whispering Falls");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadSoundButtonImages();
        createScreens();
        createLayeredPane();

        window.setSize(1280, 720);
        window.setLocationRelativeTo(null);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setVisible(true);
    }

    private void loadSoundButtonImages() {
        onImage = loadImage("/resources/menu/ON.png");
        offImage = loadImage("/resources/menu/OFF.png");
    }

    private void createScreens() {
        cardLayout = new CardLayout();
        pages = new JPanel(cardLayout);

        StoryPanel storyPanel = new StoryPanel();

        menuPanel = new MenuPanel(() -> {
            menuPanel.setMenuVisible(false);
            showCharacterSelection();
        });

        CharacterSelectionPanel characterSelectionPanel =
                new CharacterSelectionPanel(character -> {
                    selectedCharacter = character;
                    showStory2();
                });

        storyScenePanel = new StoryScenePanel(
                () -> showHowToPlay());

        howToPlayPanel = new HowToPlayPanel(
                () -> showSloth1());

        sloth1Panel = new Sloth1Panel();
        forestPathPanel = new ForestPathPanel();

        pages.add(storyPanel, "STORY");
        pages.add(menuPanel, "MENU");
        pages.add(characterSelectionPanel, "CHARACTER_SELECTION");
        pages.add(storyScenePanel, "STORY_2");
        pages.add(howToPlayPanel, "HOW_TO_PLAY");
        pages.add(sloth1Panel, "SLOTH_1");
        pages.add(forestPathPanel, "FOREST_PATH");
    }

    private void createLayeredPane() {
        JLayeredPane layeredPane = new JLayeredPane();

        layeredPane.setPreferredSize(
                new Dimension(1280, 720));

        pages.setBounds(0, 0, 1280, 720);
        layeredPane.add(pages, Integer.valueOf(0));

        createSoundButton();
        layeredPane.add(soundButton, Integer.valueOf(1));

        layeredPane.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                int width = layeredPane.getWidth();
                int height = layeredPane.getHeight();

                pages.setBounds(0, 0, width, height);

                soundButton.setBounds(
                        width - 90, 20, 80, 80);
            }
        });

        window.setContentPane(layeredPane);

        cardLayout.show(pages, "STORY");
        soundButton.setVisible(false);
    }

    private void createSoundButton() {
        soundButton = new JButton();

        soundButton.setBorderPainted(false);
        soundButton.setContentAreaFilled(false);
        soundButton.setFocusPainted(false);
        soundButton.setOpaque(false);
        soundButton.setFocusable(false);
        soundButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        soundButton.setBounds(1190, 20, 80, 80);

        updateSoundButton();

        soundButton.addActionListener(e -> {
            musicEnabled = !musicEnabled;
            updateSoundButton();

            if (menuPanel != null) {
                menuPanel.setMusicEnabled(musicEnabled);
            }
        });
    }

    private static BufferedImage loadImage(String path) {
        try (InputStream input =
                GameWindow.class.getResourceAsStream(path)) {

            if (input != null) {
                return ImageIO.read(input);
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + path);
        }

        System.err.println("Image not found: " + path);
        return null;
    }

    private static ImageIcon createIcon(BufferedImage image) {
        if (image == null) {
            return new ImageIcon();
        }

        Image resizedImage = image.getScaledInstance(
                80, 80, Image.SCALE_SMOOTH);

        return new ImageIcon(resizedImage);
    }

    private static void updateSoundButton() {
        if (soundButton == null) {
            return;
        }

        if (musicEnabled) {
            soundButton.setIcon(createIcon(onImage));
        } else {
            soundButton.setIcon(createIcon(offImage));
        }
    }

    public static void showMenu() {
        if (cardLayout == null
                || pages == null
                || menuPanel == null) {
            return;
        }

        cardLayout.show(pages, "MENU");
        soundButton.setVisible(true);

        menuPanel.setMenuVisible(true);
        menuPanel.setMusicEnabled(musicEnabled);

        pages.revalidate();
        pages.repaint();
    }

    public static void showCharacterSelection() {
        if (cardLayout == null || pages == null) {
            return;
        }

        cardLayout.show(pages, "CHARACTER_SELECTION");
        soundButton.setVisible(true);

        pages.revalidate();
        pages.repaint();
    }

    public static void showStory() {
        if (cardLayout == null || pages == null) {
            return;
        }

        if (menuPanel != null) {
            menuPanel.setMenuVisible(false);
        }

        cardLayout.show(pages, "STORY");
        soundButton.setVisible(false);

        pages.revalidate();
        pages.repaint();
    }

    public static void showStory2() {
        if (cardLayout == null
                || pages == null
                || storyScenePanel == null) {
            return;
        }

        cardLayout.show(pages, "STORY_2");
        soundButton.setVisible(true);

        storyScenePanel.startStory();

        pages.revalidate();
        pages.repaint();
    }

    public static void showHowToPlay() {
        if (cardLayout == null
                || pages == null
                || howToPlayPanel == null) {
            return;
        }

        cardLayout.show(pages, "HOW_TO_PLAY");
        soundButton.setVisible(true);

        pages.revalidate();
        pages.repaint();
    }

    public static void showSloth1() {
        if (cardLayout == null
                || pages == null
                || sloth1Panel == null) {
            return;
        }

        sloth1Panel.startScene();

        cardLayout.show(pages, "SLOTH_1");
        soundButton.setVisible(true);

        pages.revalidate();
        pages.repaint();
    }

    public static void showForestPath() {
        if (cardLayout == null
                || pages == null
                || forestPathPanel == null) {
            return;
        }

        forestPathPanel.startScene();

        cardLayout.show(pages, "FOREST_PATH");
        soundButton.setVisible(true);

        pages.revalidate();
        pages.repaint();
    }

    public static String getSelectedCharacter() {
        return selectedCharacter;
    }

    public static boolean isMusicEnabled() {
        return musicEnabled;
    }
}
