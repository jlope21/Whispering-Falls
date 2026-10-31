/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package whisperingfalls;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javazoom.jl.player.Player;

/**
 *
 * @author User
 */

public class CharacterSelectionPanel extends JPanel {

    private Image backgroundImage;
    private Image boardImage;
    private Image selectImage;
    private Image boyImage;
    private Image girlImage;

    private JButton boyButton;
    private JButton girlButton;

    private final java.util.function.Consumer<String> onCharacterSelected;

    private double scale = 1.0;
    private int sceneX;
    private int sceneY;

    public CharacterSelectionPanel(
            java.util.function.Consumer<String> onCharacterSelected) {

        this.onCharacterSelected = onCharacterSelected;

        setLayout(null);
        setOpaque(true);

        loadImages();
        createCharacterButtons();
        updateLayoutPositions();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayoutPositions();
                repaint();
            }
        });
    }

    private void loadImages() {
        backgroundImage = loadImage("/resources/SelectCharacter/MenuBackground.png");
        boardImage = loadImage("/resources/SelectCharacter/Board.png");
        selectImage = loadImage("/resources/SelectCharacter/Select.png");
        boyImage = loadImage("/resources/SelectCharacter/Boy.png");
        girlImage = loadImage("/resources/SelectCharacter/Girl.png");
    }

    private Image loadImage(String imagePath) {
        try (InputStream input = getClass().getResourceAsStream(imagePath)) {
            if (input != null) {
                return ImageIO.read(input);
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + imagePath);
        }

        System.err.println("Could not load image: " + imagePath);
        return null;
    }

    private ImageIcon createIcon(Image image, int width, int height) {
        if (image == null) {
            return new ImageIcon();
        }

        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private JButton createCharacterButton(Image image, int width, int height) {
        JButton button = new JButton(createIcon(image, width, height));

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void createCharacterButtons() {
        boyButton = createCharacterButton(boyImage, 400, 400);
        boyButton.addActionListener(e -> selectCharacter("Boy"));
        add(boyButton);

        girlButton = createCharacterButton(girlImage, 400, 400);
        girlButton.addActionListener(e -> selectCharacter("Girl"));
        add(girlButton);
    }

    private void updateLayoutPositions() {
        int panelWidth = getWidth() > 0 ? getWidth() : 1280;
        int panelHeight = getHeight() > 0 ? getHeight() : 720;

        scale = Math.min(panelWidth / 1280.0, panelHeight / 720.0);

        int sceneWidth = (int) (1280 * scale);
        int sceneHeight = (int) (700 * scale);

        sceneX = (panelWidth - sceneWidth) / 2;
        sceneY = (panelHeight - sceneHeight) / 2 - (int) (15 * scale);

        int boardX = sceneX + (int) (100 * scale);
        int boardY = sceneY + (int) (90 * scale);

        int buttonWidth = (int) (350 * scale);
        int buttonHeight = (int) (400 * scale);
        int buttonImageSize = (int) (400 * scale);

        boyButton.setIcon(createIcon(boyImage, buttonImageSize, buttonImageSize));
        boyButton.setBounds(
                boardX + (int) (205 * scale),
                sceneY + (int) (198 * scale),
                buttonWidth,
                buttonHeight);

        girlButton.setIcon(createIcon(girlImage, buttonImageSize, buttonImageSize));
        girlButton.setBounds(
                boardX + (int) (550 * scale),
                sceneY + (int) (198 * scale),
                buttonWidth,
                buttonHeight);
    }

    private void selectCharacter(String character) {
        playButtonSound();

        if (onCharacterSelected != null) {
            onCharacterSelected.accept(character);
        }
    }

    private void playButtonSound() {
        Thread soundThread = new Thread(() -> {
            try {
                URL soundURL = getClass().getResource(
                        "/resources/sounds/button-4-214382.mp3");

                if (soundURL == null) {
                    System.err.println("Could not load button sound.");
                    return;
                }

                try (InputStream input = soundURL.openStream()) {
                    Player player = new Player(input);
                    player.play();
                }
            } catch (Exception e) {
                System.err.println("Could not play button sound: " + e.getMessage());
            }
        }, "CharacterButtonSound");

        soundThread.setDaemon(true);
        soundThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        int boardWidth = (int) (1100 * scale);
        int boardHeight = (int) (600 * scale);
        int boardX = sceneX + (int) (100 * scale);
        int boardY = sceneY + (int) (90 * scale);

        if (boardImage != null) {
            g2.drawImage(
                    boardImage,
                    boardX, boardY,
                    boardWidth, boardHeight,
                    this);
        }

        int titleWidth = (int) (700 * scale);
        int titleHeight = (int) (270 * scale);
        int titleX = (getWidth() - titleWidth) / 2;
        int titleY = sceneY;

        if (selectImage != null) {
            g2.drawImage(
                    selectImage,
                    titleX, titleY,
                    titleWidth, titleHeight,
                    this);
        }

        g2.dispose();
    }
}
