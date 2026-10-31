/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package whisperingfalls;

/**
 *
 * @author User
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ForestPathPanel extends JPanel {

    private BufferedImage pathImage;

    public ForestPathPanel() {
        setLayout(null);
        setOpaque(true);
        loadImage();
    }

    private void loadImage() {
        String path =
                "/resources/ForestPath/Path_Sloth1_to_Sloth2.png";

        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input != null) {
                pathImage = ImageIO.read(input);
            } else {
                System.err.println("Forest path image not found: " + path);
            }
        } catch (Exception e) {
            System.err.println(
                    "Could not load forest path image: " + e.getMessage());
        }
    }

    public void startScene() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        if (pathImage != null) {
            g2.drawImage(
                    pathImage,
                    0, 0,
                    panelWidth, panelHeight,
                    this);
        } else {
            g2.setColor(Color.WHITE);
            g2.drawString(
                    "Forest path image not found",
                    30, 40);
        }

        g2.dispose();
    }
}