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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import javazoom.jl.player.Player;

/**
 *
 * @author User
 */


public class StoryPanel extends JPanel {
    // Story images are displayed in order, from index 0 to index 5.
    private Image[] storyImages = new Image[6];
    // Tracks how many story images have been fully shown.
    private int currentFrame = 0;
    private float imageAlpha = 0f;
    private float fadeOutAlpha = 1f;
    // Timers control image fading, the pause, and the final transition.
    private Timer timer;
    private Timer pauseTimer;
    private Timer transitionTimer;
    private JButton skipButton;
    private ImageIcon skipIcon;
    private final int gap = 12;
    private final int margin = 16;
    private final int buttonWidth = 200;
    private final int buttonHeight = 100;
    private final int pauseDuration = 1500;
    // Sound effects run on background threads so the screen stays responsive.
    private final ExecutorService soundExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService skipSoundExecutor = Executors.newSingleThreadExecutor();
    private final Object soundLock = new Object();
    private volatile Player soundPlayer;
    private volatile boolean storySkipped = false;
    private boolean transitionStarted = false;
    private boolean fadingOut = false;

    // Set up story images, the Skip button, animation, and first sound.
    public StoryPanel() {
        setLayout(null);
        setBackground(new Color(101, 67, 45));

        for (int i = 0; i < storyImages.length; i++) {
            URL imageURL = getClass().getResource(
                "/resources/story/Frame" + (i + 1) + ".png"
            );

            if (imageURL != null) {
                storyImages[i] = new ImageIcon(imageURL).getImage();
            } else {
                System.err.println(
                    "Story image not found: Frame" + (i + 1) + ".png"
                );
            }
        }

        URL skipURL = getClass().getResource(
            "/resources/story/SkipButton.png"
        );

        if (skipURL != null) {
            ImageIcon originalIcon = new ImageIcon(skipURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                buttonWidth,
                buttonHeight,
                Image.SCALE_SMOOTH
            );

            skipIcon = new ImageIcon(scaledImage);
            skipButton = new JButton(skipIcon);
            skipButton.setText("");
            skipButton.setBorderPainted(false);
            skipButton.setContentAreaFilled(false);
            skipButton.setFocusPainted(false);
            skipButton.setOpaque(false);
            skipButton.setFocusable(false);
            skipButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
            skipButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            skipButton.setRolloverEnabled(false);
            skipButton.setDisabledIcon(skipIcon);
            skipButton.setPressedIcon(skipIcon);
            skipButton.setRolloverIcon(skipIcon);
            skipButton.setBounds(
                getWidth() - buttonWidth - 20,
                getHeight() - buttonHeight - 20,
                buttonWidth,
                buttonHeight
            );

            skipButton.addActionListener(e -> {
                if (transitionStarted) {
                    return;
                }

                storySkipped = true;
                stopSound();
                playSkipSound();

                if (timer != null) {
                    timer.stop();
                }

                currentFrame = storyImages.length;
                imageAlpha = 1f;
                repaint();

                finishStory();
            });

            add(skipButton);

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    skipButton.setBounds(
                        getWidth() - buttonWidth - 20,
                        getHeight() - buttonHeight - 20,
                        buttonWidth,
                        buttonHeight
                    );
                }
            });
        } else {
            System.err.println(
                "SkipButton.png not found in /resources/story/"
            );
        }

        timer = new Timer(16, e -> {
            if (transitionStarted) {
                return;
            }

            imageAlpha += 0.01f;

            if (imageAlpha >= 1f) {
                imageAlpha = 1f;
                currentFrame++;

                if (currentFrame >= storyImages.length) {
                    finishStory();
                } else {
                    imageAlpha = 0f;
                    playPageTurnSound();
                }
            }

            repaint();
        });

        imageAlpha = 0f;
        playPageTurnSound();
        timer.start();
    }

    // Called when all images have appeared or the player presses Skip.
    private void finishStory() {
        if (transitionStarted) {
            return;
        }

        transitionStarted = true;

        if (timer != null) {
            timer.stop();
        }

        if (skipButton != null) {
            skipButton.setEnabled(true);
            skipButton.setRolloverEnabled(false);
            skipButton.setIcon(skipIcon);
            skipButton.setDisabledIcon(skipIcon);
            skipButton.setPressedIcon(skipIcon);
        }

        pauseTimer = new Timer(pauseDuration, e -> {
            pauseTimer.stop();
            startFadeOut();
        });

        pauseTimer.setRepeats(false);
        pauseTimer.start();
    }

    // Fade the story screen before opening the menu.
    private void startFadeOut() {
        if (fadingOut) {
            return;
        }

        fadingOut = true;
        fadeOutAlpha = 1f;

        transitionTimer = new Timer(30, e -> {
            fadeOutAlpha -= 0.04f;

            if (fadeOutAlpha <= 0f) {
                fadeOutAlpha = 0f;
                transitionTimer.stop();
                repaint();
                goToMenu();
                return;
            }

            repaint();
        });

        transitionTimer.start();
    }

    // Play the page-turn sound between story images.
    private void playPageTurnSound() {
        soundExecutor.execute(() -> {
            if (storySkipped || transitionStarted) {
                return;
            }

            try {
                URL soundURL = getClass().getResource(
                    "/resources/sounds/pageturn-102978.mp3"
                );

                if (soundURL == null) {
                    System.err.println("Page turn sound file not found.");
                    return;
                }

                try (InputStream input = soundURL.openStream()) {
                    Player player = new Player(input);

                    synchronized (soundLock) {
                        if (storySkipped || transitionStarted) {
                            player.close();
                            return;
                        }

                        soundPlayer = player;
                    }

                    player.play();

                    synchronized (soundLock) {
                        if (soundPlayer == player) {
                            soundPlayer = null;
                        }
                    }
                }
            } catch (Exception ex) {
                if (!storySkipped) {
                    System.err.println(
                        "Unable to play page turn sound: " + ex.getMessage()
                    );
                }
            }
        });
    }

    // Play the sound effect when Skip is pressed.
    private void playSkipSound() {
        skipSoundExecutor.execute(() -> {
            try {
                URL soundURL = getClass().getResource(
                    "/resources/sounds/button-4-214382.mp3"
                );

                if (soundURL == null) {
                    System.err.println("Skip button sound file not found.");
                    return;
                }

                try (InputStream input = soundURL.openStream()) {
                    Player player = new Player(input);

                    synchronized (soundLock) {
                        soundPlayer = player;
                    }

                    player.play();

                    synchronized (soundLock) {
                        if (soundPlayer == player) {
                            soundPlayer = null;
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println(
                    "Unable to play skip button sound: " + ex.getMessage()
                );
            }
        });
    }

    // Stop the currently tracked sound player.
    private void stopSound() {
        synchronized (soundLock) {
            Player currentPlayer = soundPlayer;

            if (currentPlayer != null) {
                soundPlayer = null;
                currentPlayer.close();
            }
        }
    }

    // Stop story timers and ask GameWindow to show the menu.
    private void goToMenu() {
        if (timer != null) {
            timer.stop();
        }

        if (pauseTimer != null) {
            pauseTimer.stop();
        }

        if (transitionTimer != null) {
            transitionTimer.stop();
        }

        System.out.println("Story finished. Switching to menu.");
        GameWindow.showMenu();
    }

    // Draw all completed images and fade in the current image.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        int cellWidth = (getWidth() - margin * 2 - gap * 2) / 3;
        int cellHeight = (getHeight() - margin * 2 - gap) / 2;

        for (int i = 0; i < currentFrame; i++) {
            drawFrame(g2, i, cellWidth, cellHeight, 1f);
        }

        if (currentFrame < storyImages.length) {
            drawFrame(g2, currentFrame, cellWidth, cellHeight, imageAlpha);
        }

        g2.dispose();
    }

    // Draw one image in its position in the 3-by-2 layout.
    private void drawFrame(
        Graphics2D g,
        int index,
        int cellWidth,
        int cellHeight,
        float alpha
    ) {
        if (storyImages[index] == null) {
            return;
        }

        int col = index % 3;
        int row = index / 3;

        int x = margin + col * (cellWidth + gap);
        int y = margin + row * (cellHeight + gap);

        Composite originalComposite = g.getComposite();

        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER,
            Math.max(0f, Math.min(1f, alpha))
        ));

        g.setColor(Color.WHITE);
        g.fillRoundRect(
            x - 3,
            y - 3,
            cellWidth + 6,
            cellHeight + 6,
            8,
            8
        );

        g.drawImage(
            storyImages[index],
            x,
            y,
            cellWidth,
            cellHeight,
            this
        );

        g.setComposite(originalComposite);
    }

    // Draw the fade overlay over both the images and the Skip button.
    @Override
    protected void paintChildren(Graphics g) {
        if (!fadingOut) {
            super.paintChildren(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        super.paintChildren(g2);

        g2.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER,
            Math.max(0f, Math.min(1f, 1f - fadeOutAlpha))
        ));

        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    // Stop timers and sound threads when leaving the story screen.
    @Override
    public void removeNotify() {
        if (timer != null) {
            timer.stop();
        }

        if (pauseTimer != null) {
            pauseTimer.stop();
        }

        if (transitionTimer != null) {
            transitionTimer.stop();
        }

        storySkipped = true;
        stopSound();
        soundExecutor.shutdownNow();
        skipSoundExecutor.shutdownNow();

        super.removeNotify();
    }
}



