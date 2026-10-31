/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package whisperingfalls;

import javax.swing.SwingUtilities;

/**
 *
 * @author User
 */

public class WhisperingFalls {
    // Start the game on Swing's UI thread.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameWindow().start();
        });
    }
}

