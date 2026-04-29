package practico1.swingcompleto;

import javax.swing.*;
import java.awt.event.*;

public class VentanaMejorada extends JFrame {
    public VentanaMejorada() {
        super("Dashboard Pro - Swing [cite: 52]");
        setSize(500, 400);
        setLocationRelativeTo(null); // Centra la ventana
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Mejora: Confirmación de salida [cite: 63]
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int resp = JOptionPane.showConfirmDialog(null, "¿Desea salir?", "Confirmar Salida", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaMejorada().setVisible(true));
    }
}
