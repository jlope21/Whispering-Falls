/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package whisperingfalls;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author User
 */

public class Sloth1Panel extends JPanel {

    private BufferedImage trappedImage;
    private BufferedImage wrongToolImage;
    private BufferedImage rescuedImage;

    private BufferedImage toolbarImage;
    private BufferedImage scissorsImage;
    private BufferedImage headphonesImage;
    private BufferedImage waterBottleImage;
    private BufferedImage flashlightImage;

    private BufferedImage[] toolImages;

    private final String[] toolNames = {
        "Scissors", "Headphones", "Water", "Flashlight"
    };

    private enum SceneState {
        TRAPPED, WRONG_TOOL, RESCUED
    }

    private SceneState currentState = SceneState.TRAPPED;
    private Timer sceneTimer;

    private double scaleX = 1.0;
    private double scaleY = 1.0;

    private int draggingTool = -1;
    private int mouseX;
    private int mouseY;

    private final int toolbarX = 230;
    private final int toolbarY = 500;
    private final int toolbarWidth = 820;
    private final int toolbarHeight = 200;

    private final int slotSize = 100;
    private final int slotY = 594;
    private final int firstSlotX = 305;
    private final int slotGap = 190;

    private final Rectangle slothTarget =
            new Rectangle(390, 100, 500, 470);

    public Sloth1Panel() {
        setLayout(null);
        setOpaque(true);

        loadImages();
        setupMouseControls();
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

    private void loadImages() {
        trappedImage = loadImage(
                "/resources/Sloth1/Sloth1_Trapped.png");

        wrongToolImage = loadImage(
                "/resources/Sloth1/Sloth1_WrongTool.png");

        rescuedImage = loadImage(
                "/resources/Sloth1/Sloth1_Rescued.png");

        toolbarImage = loadImage(
                "/resources/Tools/Toolbar.png");

        scissorsImage = loadImage(
                "/resources/Tools/Scissors.png");

        headphonesImage = loadImage(
                "/resources/Tools/Headphones.png");

        waterBottleImage = loadImage(
                "/resources/Tools/WaterBottle.png");

        flashlightImage = loadImage(
                "/resources/Tools/Flashlight.png");

        toolImages = new BufferedImage[] {
            scissorsImage,
            headphonesImage,
            waterBottleImage,
            flashlightImage
        };
    }

    private void setupMouseControls() {
        MouseAdapter mouseHandler = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int[] point = toSceneCoordinates(e.getX(), e.getY());

                mouseX = point[0];
                mouseY = point[1];

                if (currentState != SceneState.TRAPPED) {
                    return;
                }

                draggingTool = getToolAt(mouseX, mouseY);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggingTool < 0) {
                    return;
                }

                int[] point = toSceneCoordinates(e.getX(), e.getY());

                mouseX = point[0];
                mouseY = point[1];

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggingTool < 0) {
                    return;
                }

                int[] point = toSceneCoordinates(e.getX(), e.getY());

                int dropX = point[0];
                int dropY = point[1];

                int selectedTool = draggingTool;
                draggingTool = -1;

                if (currentState == SceneState.TRAPPED
                        && slothTarget.contains(dropX, dropY)) {
                    useTool(selectedTool);
                }

                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private int[] toSceneCoordinates(int screenX, int screenY) {
        scaleX = Math.max(1, getWidth()) / 1280.0;
        scaleY = Math.max(1, getHeight()) / 720.0;

        int x = (int) (screenX / scaleX);
        int y = (int) (screenY / scaleY);

        return new int[] {x, y};
    }

    private int getToolAt(int x, int y) {
        for (int i = 0; i < toolImages.length; i++) {
            int slotX = firstSlotX + i * slotGap;

            Rectangle slot = new Rectangle(
                    slotX, slotY, slotSize, slotSize);

            if (slot.contains(x, y)) {
                return i;
            }
        }

        return -1;
    }

    private void useTool(int toolIndex) {
        if (currentState != SceneState.TRAPPED) {
            return;
        }

        stopSceneTimer();

        if (toolIndex == 0) {
            currentState = SceneState.RESCUED;
            draggingTool = -1;
            repaint();

            sceneTimer = new Timer(1600, e -> {
                stopSceneTimer();
                GameWindow.showForestPath();
            });

            sceneTimer.setRepeats(false);
            sceneTimer.start();

        } else {
            currentState = SceneState.WRONG_TOOL;
            repaint();

            sceneTimer = new Timer(1000, e -> {
                stopSceneTimer();
                currentState = SceneState.TRAPPED;
                repaint();
            });

            sceneTimer.setRepeats(false);
            sceneTimer.start();
        }
    }

    private void stopSceneTimer() {
        if (sceneTimer != null) {
            sceneTimer.stop();
            sceneTimer = null;
        }
    }

    public void startScene() {
        stopSceneTimer();

        currentState = SceneState.TRAPPED;
        draggingTool = -1;
        mouseX = 0;
        mouseY = 0;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        scaleX = panelWidth / 1280.0;
        scaleY = panelHeight / 720.0;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        BufferedImage currentImage = getCurrentSceneImage();

        if (currentImage != null) {
            // Fill the whole screen with the scene image.
            g2.drawImage(
                    currentImage,
                    0, 0,
                    panelWidth, panelHeight,
                    this);
        } else {
            drawMissingSceneMessage(g2);
        }

        // The toolbar and equipment are only shown in this scene.
        drawToolbar(g2);
        drawDraggedTool(g2);

        g2.dispose();
    }

    private BufferedImage getCurrentSceneImage() {
        switch (currentState) {
            case WRONG_TOOL:
                return wrongToolImage;

            case RESCUED:
                return rescuedImage;

            case TRAPPED:
            default:
                return trappedImage;
        }
    }

    private void drawMissingSceneMessage(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));

        String message = "Scene image not found";
        FontMetrics metrics = g2.getFontMetrics();

        g2.drawString(
                message,
                (getWidth() - metrics.stringWidth(message)) / 2,
                getHeight() / 2);
    }

    private void drawToolbar(Graphics2D g2) {
        int x = (int) (toolbarX * scaleX);
        int y = (int) (toolbarY * scaleY);
        int width = (int) (toolbarWidth * scaleX);
        int height = (int) (toolbarHeight * scaleY);

        if (toolbarImage != null) {
            g2.drawImage(
                    toolbarImage,
                    x, y,
                    width, height,
                    this);
        }

        for (int i = 0; i < toolImages.length; i++) {
            drawToolSlot(g2, i);
        }
    }

    private void drawToolSlot(Graphics2D g2, int index) {
        int slotX = (int) (
                (firstSlotX + index * slotGap) * scaleX);

        int slotYPosition = (int) (slotY * scaleY);
        int slotWidth = (int) (slotSize * scaleX);
        int slotHeight = (int) (slotSize * scaleY);

        BufferedImage toolImage = toolImages[index];

        if (toolImage != null) {
            int iconWidth = (int) (64 * scaleX);
            int iconHeight = (int) (64 * scaleY);

            int iconX = slotX + (slotWidth - iconWidth) / 2;
            int iconY = slotYPosition + (int) (4 * scaleY);

            drawImagePreservingRatio(
                    g2, toolImage,
                    iconX, iconY,
                    iconWidth, iconHeight);
        }

        double fontScale = Math.min(scaleX, scaleY);

        g2.setColor(new Color(255, 230, 150));
        g2.setFont(new Font(
                Font.MONOSPACED,
                Font.BOLD,
                Math.max(1, (int) (8 * fontScale))));

        FontMetrics metrics = g2.getFontMetrics();
        int labelWidth = metrics.stringWidth(toolNames[index]);

        g2.drawString(
                toolNames[index],
                slotX + (slotWidth - labelWidth) / 2,
                slotYPosition + (int) (88 * scaleY));
    }

    private void drawDraggedTool(Graphics2D g2) {
        if (draggingTool < 0
                || draggingTool >= toolImages.length
                || toolImages[draggingTool] == null) {
            return;
        }

        BufferedImage image = toolImages[draggingTool];

        int drawX = (int) (mouseX * scaleX);
        int drawY = (int) (mouseY * scaleY);

        int width = (int) (80 * scaleX);
        int height = (int) (80 * scaleY);

        drawImagePreservingRatio(
                g2, image,
                drawX - width / 2,
                drawY - height / 2,
                width, height);
    }

    private void drawImagePreservingRatio(
            Graphics2D g2,
            BufferedImage image,
            int x,
            int y,
            int boxWidth,
            int boxHeight) {

        double imageScaleX = (double) boxWidth / image.getWidth();
        double imageScaleY = (double) boxHeight / image.getHeight();
        double imageScale = Math.min(imageScaleX, imageScaleY);

        int imageWidth = Math.max(
                1, (int) (image.getWidth() * imageScale));

        int imageHeight = Math.max(
                1, (int) (image.getHeight() * imageScale));

        int imageX = x + (boxWidth - imageWidth) / 2;
        int imageY = y + (boxHeight - imageHeight) / 2;

        g2.drawImage(
                image,
                imageX, imageY,
                imageWidth, imageHeight,
                this);
    }

    @Override
    public void removeNotify() {
        stopSceneTimer();
        super.removeNotify();
    }
}