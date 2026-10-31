/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package whisperingfalls;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author User
 */

public class StoryScenePanel extends JPanel {

    private BufferedImage frame1Image;
    private BufferedImage frame2Image;
    private BufferedImage skipImage;

    private JButton skipButton;
    private Timer storyTimer;
    private Timer pauseTimer;
    private Timer fadeTimer;

    private int currentFrame = 0;
    private float imageAlpha = 0f;
    private float fadeAlpha = 0f;

    private boolean fadingOut = false;
    private boolean transitionStarted = false;

    private final int gap = 12;
    private final int margin = 16;
    private final Runnable onSkip;

    public StoryScenePanel(Runnable onSkip) {
        this.onSkip = onSkip;

        setLayout(null);
        setOpaque(true);
        setBackground(new Color(101, 67, 45));

        skipImage = loadImage("/resources/story2/SkipButton.png");
        createSkipButton();
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

    private void loadStoryImages() {
        frame1Image = loadImage("/resources/story2/Frame1.png");

        String character = GameWindow.getSelectedCharacter();

        if ("Girl".equalsIgnoreCase(character)) {
            frame2Image = loadImage("/resources/story2/Frame2 - girl.png");
        } else {
            frame2Image = loadImage("/resources/story2/Frame2 - boy.png");
        }

        System.out.println("Selected character: " + character);
        System.out.println("Frame 1 loaded: " + (frame1Image != null));
        System.out.println("Frame 2 loaded: " + (frame2Image != null));
    }

    private void createSkipButton() {
        skipButton = new JButton();

        if (skipImage != null) {
            Image resizedImage = skipImage.getScaledInstance(200, 100, Image.SCALE_SMOOTH);
            skipButton.setIcon(new ImageIcon(resizedImage));
        } else {
            skipButton.setText("SKIP");
        }

        skipButton.setBorderPainted(false);
        skipButton.setContentAreaFilled(false);
        skipButton.setFocusPainted(false);
        skipButton.setOpaque(false);
        skipButton.setFocusable(false);
        skipButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        skipButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        updateSkipButtonPosition();

        skipButton.addActionListener(e -> showAllFramesThenFade());

        add(skipButton);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSkipButtonPosition();
            }
        });
    }

    private void updateSkipButtonPosition() {
        skipButton.setBounds(getWidth() - 220, getHeight() - 115, 200, 100);
    }

    public void startStory() {
        stopStory();
        loadStoryImages();

        currentFrame = 0;
        imageAlpha = 0f;
        fadeAlpha = 0f;
        fadingOut = false;
        transitionStarted = false;

        repaint();

        storyTimer = new Timer(16, e -> {
            imageAlpha += 0.01f;

            if (imageAlpha >= 1f) {
                imageAlpha = 1f;
                currentFrame++;

                if (currentFrame >= 2) {
                    storyTimer.stop();

                    pauseTimer = new Timer(1200, event -> beginFadeOut());
                    pauseTimer.setRepeats(false);
                    pauseTimer.start();
                } else {
                    imageAlpha = 0f;
                }
            }

            repaint();
        });

        storyTimer.start();
    }

    private void showAllFramesThenFade() {
        if (transitionStarted) {
            return;
        }

        if (storyTimer != null) {
            storyTimer.stop();
            storyTimer = null;
        }

        if (pauseTimer != null) {
            pauseTimer.stop();
            pauseTimer = null;
        }

        // Show both story frames completely.
        currentFrame = 2;
        imageAlpha = 1f;
        repaint();

        // Pause briefly before fading out.
        pauseTimer = new Timer(1200, e -> {
            pauseTimer.stop();
            pauseTimer = null;
            beginFadeOut();
        });

        pauseTimer.setRepeats(false);
        pauseTimer.start();
    }
    
    private void beginFadeOut() {
        if (transitionStarted) {
            return;
        }

        transitionStarted = true;
        fadingOut = true;
        fadeAlpha = 0f;

        if (storyTimer != null) {
            storyTimer.stop();
            storyTimer = null;
        }

        if (pauseTimer != null) {
            pauseTimer.stop();
            pauseTimer = null;
        }

        fadeTimer = new Timer(30, e -> {
            fadeAlpha += 0.04f;

            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                fadeTimer.stop();
                fadeTimer = null;
                repaint();

                if (onSkip != null) {
                    onSkip.run();
                }

                return;
            }

            repaint();
        });

        fadeTimer.start();
    }

    public void stopStory() {
        if (storyTimer != null) {
            storyTimer.stop();
            storyTimer = null;
        }

        if (pauseTimer != null) {
            pauseTimer.stop();
            pauseTimer = null;
        }

        if (fadeTimer != null) {
            fadeTimer.stop();
            fadeTimer = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cellWidth = (getWidth() - margin * 2 - gap) / 2;
        int cellHeight = getHeight() - margin * 2;

        if (currentFrame >= 1) {
            drawFrame(g2, frame1Image, 0, cellWidth, cellHeight, 1f);
        }

        if (currentFrame == 0) {
            drawFrame(g2, frame1Image, 0, cellWidth, cellHeight, imageAlpha);
        } else if (currentFrame == 1) {
            drawFrame(g2, frame2Image, 1, cellWidth, cellHeight, imageAlpha);
        } else {
            drawFrame(g2, frame2Image, 1, cellWidth, cellHeight, 1f);
        }

        g2.dispose();
    }

    private void drawFrame(Graphics2D g2, BufferedImage image, int index, int cellWidth, int cellHeight, float alpha) {
        if (image == null) {
            return;
        }

        int x = margin + index * (cellWidth + gap);
        int y = margin;

        double scaleX = (double) cellWidth / image.getWidth();
        double scaleY = (double) cellHeight / image.getHeight();
        double scale = Math.min(scaleX, scaleY);

        int imageWidth = (int) (image.getWidth() * scale);
        int imageHeight = (int) (image.getHeight() * scale);

        int imageX = x + (cellWidth - imageWidth) / 2;
        int imageY = y + (cellHeight - imageHeight) / 2;

        Composite originalComposite = g2.getComposite();

        g2.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER,
            Math.max(0f, Math.min(1f, alpha))
        ));

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(imageX - 3, imageY - 3, imageWidth + 6, imageHeight + 6, 8, 8);
        g2.drawImage(image, imageX, imageY, imageWidth, imageHeight, this);

        g2.setComposite(originalComposite);
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        if (!fadingOut || fadeAlpha <= 0f) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER,
            Math.max(0f, Math.min(1f, fadeAlpha))
        ));

        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
    }
}
