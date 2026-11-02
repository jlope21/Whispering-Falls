package whisperingfalls;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/** Final screen with prepared artwork and image buttons. */
public class EndingPanel extends JPanel {

    private BufferedImage backgroundImage;
    private BufferedImage sloth1Image;
    private BufferedImage sloth2Image;
    private JButton resumeButton;
    private JButton backButton;

    public EndingPanel(Runnable onResume, Runnable onBack) {
        setLayout(null);
        backgroundImage = loadImage("/resources/menu/MenuBackground.png");
        sloth1Image = loadImage("/resources/Ending/Ending_Sloth1.png");
        sloth2Image = loadImage("/resources/Ending/Ending_Sloth2.png");
        resumeButton = createButton("/resources/Ending/ResumeButton.png", onResume);
        backButton = createButton("/resources/Ending/BackButton.png", onBack);
        add(resumeButton);
        add(backButton);
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

    private JButton createButton(String path, Runnable action) {
        BufferedImage image = loadImage(path);
        JButton button = new JButton(image == null ? new ImageIcon()
                : new ImageIcon(image.getScaledInstance(300, 150,
                        java.awt.Image.SCALE_SMOOTH)));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            if (action != null) action.run();
        });
        return button;
    }

    @Override
    public void doLayout() {
        int buttonWidth = Math.min(300, Math.max(180, getWidth() / 4));
        int buttonHeight = Math.min(110, Math.max(66, getHeight() / 8));
        int centerX = (getWidth() - buttonWidth) / 2;
        int resumeY = Math.min(getHeight() - buttonHeight * 2 - 20,
                Math.max(getHeight() / 2 + 110, 430));
        resumeButton.setBounds(centerX, resumeY, buttonWidth, buttonHeight);
        backButton.setBounds(centerX, resumeY + buttonHeight + 8,
                buttonWidth, buttonHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage,0, 0, getWidth(), getHeight(), this);
        }
        if (sloth1Image != null) {
            g2.drawImage(sloth1Image, 0, 0, getWidth() / 3, getHeight() / 2, this);
        }
        if (sloth2Image != null) {
            int width = getWidth() / 3;
            int height = getHeight() / 2;
            g2.drawImage(sloth2Image, (getWidth() - width) / 2,
                    getHeight() / 5, width, height, this);
        }
        g2.dispose();
    }
}
