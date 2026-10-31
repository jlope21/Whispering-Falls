/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package whisperingfalls;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javazoom.jl.player.Player;

/**
 *
 * @author User
 */

public class MenuPanel extends JPanel {

    private java.awt.image.BufferedImage backgroundImage;
    private java.awt.image.BufferedImage titleImage;
    private java.awt.image.BufferedImage startImage;

    private JButton startButton;

    private volatile boolean musicEnabled = true;
    private volatile boolean menuVisible = false;
    private volatile Player musicPlayer;

    private final AtomicLong musicGeneration = new AtomicLong(0);
    private final Runnable onStart;

    public MenuPanel(Runnable onStart) {
        this.onStart = onStart;

        setLayout(null);
        setFocusable(true);

        loadImages();
        createButtons();
    }

    private java.awt.image.BufferedImage loadImage(String path) {
        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input != null) {
                return ImageIO.read(input);
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + path);
        }

        return null;
    }

    private void loadImages() {
        backgroundImage = loadImage("/resources/menu/MenuBackground.png");
        titleImage = loadImage("/resources/menu/WhisperingFalls.png");
        startImage = loadImage("/resources/menu/START.png");
    }

    private ImageIcon createIcon(java.awt.image.BufferedImage image, int width, int height) {
        if (image == null) {
            return new ImageIcon();
        }

        return new ImageIcon(
            image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH)
        );
    }

    private JButton createImageButton(java.awt.image.BufferedImage image, int width, int height) {
        JButton button = new JButton(createIcon(image, width, height));

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void createButtons() {
        startButton = createImageButton(startImage, 300, 180);
        startButton.setBounds(630, 550, 300, 180);

        startButton.addActionListener((ActionEvent e) -> {
            playButtonSound();

            if (onStart != null) {
                onStart.run();
            }
        });

        add(startButton);
    }

    public synchronized void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;

        if (!musicEnabled) {
            stopMusicPlayback();
        } else if (menuVisible) {
            startMenuMusic();
        }
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public synchronized void setMenuVisible(boolean visible) {
        menuVisible = visible;

        if (menuVisible && musicEnabled) {
            startMenuMusic();
        } else {
            stopMusicPlayback();
        }
    }

    private synchronized void startMenuMusic() {
        if (!musicEnabled || !menuVisible || musicPlayer != null) {
            return;
        }

        long generation = musicGeneration.incrementAndGet();

        Thread musicThread = new Thread(() -> {
            while (musicEnabled && menuVisible
                    && musicGeneration.get() == generation) {
                Player player = null;

                try {
                    URL musicUrl = getClass().getResource(
                        "/resources/sounds/241013-piano-ambient-relaxing.mp3"
                    );

                    if (musicUrl == null) {
                        System.err.println("Could not load menu music.");
                        break;
                    }

                    try (InputStream input = musicUrl.openStream()) {
                        player = new Player(input);

                        synchronized (this) {
                            if (!musicEnabled || !menuVisible
                                    || musicGeneration.get() != generation) {
                                player.close();
                                break;
                            }

                            musicPlayer = player;
                        }

                        player.play();
                    }
                } catch (Exception e) {
                    if (musicEnabled && menuVisible
                            && musicGeneration.get() == generation) {
                        System.err.println(
                            "Could not play menu music: " + e.getMessage()
                        );
                    }

                    break;
                } finally {
                    synchronized (this) {
                        if (musicPlayer == player) {
                            musicPlayer = null;
                        }
                    }
                }
            }
        }, "MenuMusic");

        musicThread.setDaemon(true);
        musicThread.start();
    }

    private synchronized void stopMusicPlayback() {
        musicGeneration.incrementAndGet();

        Player player = musicPlayer;
        musicPlayer = null;

        if (player != null) {
            player.close();
        }
    }

    private void playButtonSound() {
        Thread soundThread = new Thread(() -> {
            try {
                URL soundUrl = getClass().getResource(
                    "/resources/sounds/button-4-214382.mp3"
                );

                if (soundUrl == null) {
                    System.err.println("Could not load button sound.");
                    return;
                }

                try (InputStream input = soundUrl.openStream()) {
                    Player player = new Player(input);
                    player.play();
                }
            } catch (Exception e) {
                System.err.println(
                    "Could not play button sound: " + e.getMessage()
                );
            }
        }, "ButtonSound");

        soundThread.setDaemon(true);
        soundThread.start();
    }

    public void stopMenu() {
        setMenuVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (titleImage != null) {
            int titleWidth = 1000;
            int titleHeight = 500;
            int titleX = (getWidth() - titleWidth) / 2;
            int titleY = 50;

            g2.drawImage(
                titleImage,
                titleX,
                titleY,
                titleWidth,
                titleHeight,
                this
            );
        }

        g2.dispose();
    }

    @Override
    public void removeNotify() {
        stopMenu();
        super.removeNotify();
    }
}