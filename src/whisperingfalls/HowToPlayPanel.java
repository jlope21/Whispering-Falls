/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package whisperingfalls;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author User
 */

public class HowToPlayPanel extends JPanel {

    private BufferedImage backgroundImage;
    private BufferedImage boardImage;
    private BufferedImage howToImage;
    private BufferedImage wasdImage;
    private BufferedImage mouseImage;
    private BufferedImage clockImage;
    private BufferedImage eImage;
    private BufferedImage spaceBarImage;
    private BufferedImage gotItImage;

    private JButton gotItButton;
    private final Runnable onGotIt;

    private Font pixelFont;

    private double scale = 1.0;
    private int sceneX;
    private int sceneY;

    public HowToPlayPanel(Runnable onGotIt) {
        this.onGotIt = onGotIt;

        setLayout(null);
        setOpaque(true);

        loadImages();
        loadPixelFont();
        createGotItButton();
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
        backgroundImage = loadImage("/resources/HowToPlay/MenuBackground.png");
        boardImage = loadImage("/resources/HowToPlay/Board.png");
        howToImage = loadImage("/resources/HowToPlay/HowTo.png");
        wasdImage = loadImage("/resources/HowToPlay/WASD.png");
        mouseImage = loadImage("/resources/HowToPlay/Mouse.png");
        clockImage = loadImage("/resources/HowToPlay/Clock.png");
        eImage = loadImage("/resources/HowToPlay/E.png");
        spaceBarImage = loadImage("/resources/HowToPlay/SpaceBar.png");
        gotItImage = loadImage("/resources/HowToPlay/Got it.png");
    }

    private BufferedImage loadImage(String path) {
        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input != null) {
                return ImageIO.read(input);
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + path);
        }

        System.err.println("Image not found: " + path);
        return null;
    }

    private void loadPixelFont() {
        try (InputStream input = getClass().getResourceAsStream(
                "/resources/fonts/PressStart2P-Regular.ttf")) {

            if (input != null) {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, input);
            } else {
                pixelFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
                System.err.println("Pixel font not found.");
            }

        } catch (FontFormatException | IOException e) {
            pixelFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
            System.err.println("Could not load pixel font: " + e.getMessage());
        }
    }

    private void createGotItButton() {
        gotItButton = new JButton();

        gotItButton.setBorderPainted(false);
        gotItButton.setContentAreaFilled(false);
        gotItButton.setFocusPainted(false);
        gotItButton.setOpaque(false);
        gotItButton.setFocusable(false);
        gotItButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gotItButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gotItButton.addActionListener(e -> {
            if (onGotIt != null) {
                onGotIt.run();
            }
        });

        add(gotItButton);
    }

    private void updateLayoutPositions() {
        int panelWidth = getWidth() > 0 ? getWidth() : 1280;
        int panelHeight = getHeight() > 0 ? getHeight() : 720;

        scale = Math.min(panelWidth / 1280.0, panelHeight / 720.0);

        sceneX = (panelWidth - (int) (1280 * scale)) / 2;
        sceneY = (panelHeight - (int) (720 * scale)) / 2;

        int buttonWidth = Math.max(1, (int) (210 * scale));
        int buttonHeight = Math.max(1, (int) (110 * scale));
        int buttonX = sceneX + (int) (530 * scale);
        int buttonY = sceneY + (int) (620 * scale);

        gotItButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

        if (gotItImage != null) {
            Image resizedImage = gotItImage.getScaledInstance(
                    buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
            gotItButton.setIcon(new ImageIcon(resizedImage));
        } else {
            gotItButton.setText("Got it");
            gotItButton.setFont(getPixelFont(9));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Board and title: position and size are controlled independently.
        drawScaledImage(g2, boardImage, 100, 85, 1080, 570);
        drawScaledImage(g2, howToImage, 330, -30, 600, 300);

        // WASD: image position and size.
        drawControlImage(g2, wasdImage, 285, 190, 150, 150);

        // WASD: title and description positions.
        drawPixelText(g2, "WASD", 360, 340, 12);
        drawPixelText(g2, "Use WASD to move", 360, 362, 7);
        drawPixelText(g2, "through the forest.", 360, 384, 7);

        // Mouse: image position and size.
        drawControlImage(g2, mouseImage, 565, 190, 150, 150);

        // Mouse: title and description positions.
        drawPixelText(g2, "Mouse", 640, 340, 12);
        drawPixelText(g2, "Click objects and characters.", 640, 362, 7);
        drawPixelText(g2, "Drag items when required.", 640, 384, 7);

        // Clock: image position and size.
        drawControlImage(g2, clockImage, 835, 190, 150, 150);

        // Clock: title and description positions.
        drawPixelText(g2, "Clock", 910, 340, 12);
        drawPixelText(g2, "Watch the clock carefully.", 910, 362, 7);
        drawPixelText(g2, "Reach the campsite in time.", 910, 384, 7);

        // E: image position and size, adjusted independently.
        drawControlImage(g2, eImage, 440, 395, 90, 90);

        // E: text positions, adjusted independently from its image.
        drawPixelText(g2, "E - Interact", 485, 505, 12);
        drawPixelText(g2, "Press E to interact", 485, 527, 7);
        drawPixelText(g2, "with nearby objects.", 485, 549, 7);

        // Spacebar: image position and size, adjusted independently.
        drawControlImage(g2, spaceBarImage, 695, 370, 150, 150);

        // Spacebar: text positions, adjusted independently from its image.
        drawPixelText(g2, "Spacebar - Jump", 770, 505, 12);
        drawPixelText(g2, "Press Spacebar to jump", 770, 527, 7);
        drawPixelText(g2, "over obstacles when needed.", 770, 549, 7);

        g2.dispose();
    }

    // Draw a decorative image with independent position and size.
    private void drawScaledImage(
            Graphics2D g2, BufferedImage image,
            int x, int y, int width, int height) {

        if (image == null) {
            return;
        }

        int drawX = sceneX + (int) (x * scale);
        int drawY = sceneY + (int) (y * scale);
        int drawWidth = (int) (width * scale);
        int drawHeight = (int) (height * scale);

        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, this);
    }

    // Draw a control image without stretching it.
    private void drawControlImage(
            Graphics2D g2, BufferedImage image,
            int x, int y, int width, int height) {

        if (image == null) {
            return;
        }

        int drawX = sceneX + (int) (x * scale);
        int drawY = sceneY + (int) (y * scale);
        int drawWidth = Math.max(1, (int) (width * scale));
        int drawHeight = Math.max(1, (int) (height * scale));

        drawImagePreservingRatio(
                g2, image, drawX, drawY, drawWidth, drawHeight);
    }

    // Draw one line of pixel text at an independent position.
    private void drawPixelText(
            Graphics2D g2, String text,
            int centerX, int baselineY, float fontSize) {

        g2.setColor(new Color(255, 230, 150));
        g2.setFont(getPixelFont(fontSize));

        int drawCenterX = sceneX + (int) (centerX * scale);
        int drawBaselineY = sceneY + (int) (baselineY * scale);

        FontMetrics metrics = g2.getFontMetrics();
        int textX = drawCenterX - metrics.stringWidth(text) / 2;

        g2.drawString(text, textX, drawBaselineY);
    }

    private Font getPixelFont(float baseSize) {
        Font baseFont = pixelFont != null
                ? pixelFont
                : new Font(Font.MONOSPACED, Font.PLAIN, 12);

        return baseFont.deriveFont(
                Math.max(1f, baseSize * (float) scale));
    }

    private void drawImagePreservingRatio(
            Graphics2D g2, BufferedImage image,
            int x, int y, int boxWidth, int boxHeight) {

        double scaleX = (double) boxWidth / image.getWidth();
        double scaleY = (double) boxHeight / image.getHeight();
        double imageScale = Math.min(scaleX, scaleY);

        int imageWidth = Math.max(1, (int) (image.getWidth() * imageScale));
        int imageHeight = Math.max(1, (int) (image.getHeight() * imageScale));

        int imageX = x + (boxWidth - imageWidth) / 2;
        int imageY = y + (boxHeight - imageHeight) / 2;

        g2.drawImage(
                image, imageX, imageY,
                imageWidth, imageHeight, this);
    }
}