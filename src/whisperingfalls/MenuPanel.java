package whisperingfalls;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/** Main menu using the prepared background, logo, and start button. */
public class MenuPanel extends JPanel {

    private BufferedImage backgroundImage;
    private BufferedImage titleImage;
    private BufferedImage startImage;
    private JButton startButton;
    private final Runnable onStart;

    public MenuPanel(Runnable onStart) {
        this.onStart = onStart;
        setLayout(null);
        loadImages();
        createStartButton();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionStartButton();
            }
        });
    }

    private BufferedImage loadImage(String path) {
        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input != null) return ImageIO.read(input);
        } catch (Exception e) {
            System.err.println("Could not load image: " + path);
        }
        System.err.println("Image not found: " + path);
        return null;
    }

    private void loadImages() {
        backgroundImage = loadImage("/resources/menu/MenuBackground.png");
        titleImage = loadImage("/resources/menu/WhisperingFalls.png");
        startImage = loadImage("/resources/menu/START.png");
    }

    private void createStartButton() {
        startButton = new JButton(startImage == null ? new ImageIcon()
                : new ImageIcon(startImage.getScaledInstance(300, 180,
                        java.awt.Image.SCALE_SMOOTH)));
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.setOpaque(false);
        startButton.setFocusable(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> {
            if (onStart != null) onStart.run();
        });
        add(startButton);
        positionStartButton();
    }

    private void positionStartButton() {
        int width = 300;
        int height = 180;
        startButton.setBounds((getWidth() - width) / 2,
                getHeight() - height - 150, width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        if (titleImage != null) {
            int width = Math.min(1000, getWidth());
            int height = Math.min(500, getHeight());
            g2.drawImage(titleImage, (getWidth() - width) / 2, 50,
                    width, height, this);
        }
        g2.dispose();
    }
}
