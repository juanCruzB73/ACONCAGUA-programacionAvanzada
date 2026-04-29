package practico1.swingcompleto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TecladoNumerico extends JFrame {
    private JTextField pantalla;

    public TecladoNumerico() {
        super("Teclado Numérico");
        setSize(300, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // Pantalla superior
        pantalla = new JTextField();
        pantalla.setEditable(false);
        pantalla.setFont(new Font("Arial", Font.BOLD, 24));
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        add(pantalla, BorderLayout.NORTH);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(4, 3, 5, 5));
        String[] botones = {
            "7", "8", "9",
            "4", "5", "6",
            "1", "2", "3",
            "C", "0", "="
        };

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String comando = e.getActionCommand();
                if (comando.equals("C")) {
                    pantalla.setText("");
                } else if (comando.equals("=")) {
                    if (!pantalla.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(TecladoNumerico.this, 
                            "Valor ingresado: " + pantalla.getText(),
                            "Resultado", JOptionPane.INFORMATION_MESSAGE);
                        pantalla.setText("");
                    }
                } else {
                    pantalla.setText(pantalla.getText() + comando);
                }
            }
        };

        for (String texto : botones) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Arial", Font.BOLD, 20));
            boton.addActionListener(listener);
            panelBotones.add(boton);
        }

        add(panelBotones, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TecladoNumerico().setVisible(true));
    }
}
