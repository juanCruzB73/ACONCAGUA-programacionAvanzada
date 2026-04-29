package practico1.swingcompleto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventoSaludoPro extends JFrame {
    private JTextField txtNombre;
    private JButton btnSaludar;

    public EventoSaludoPro() {
        super("Evento Saludo Pro");
        setSize(350, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        add(new JLabel("Ingrese su nombre:"));
        txtNombre = new JTextField(15);
        add(txtNombre);

        btnSaludar = new JButton("Saludar");
        // Registro del Evento utilizando Clase Interna (Posibilidad 2)
        btnSaludar.addActionListener(new ManejadorBoton());
        add(btnSaludar);
    }

    // Clase interna recomendada para acceder a miembros privados
    private class ManejadorBoton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(EventoSaludoPro.this, 
                        "Por favor, ingrese un nombre válido.", 
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(EventoSaludoPro.this, 
                        "¡Hola " + nombre + ", bienvenido a Swing!", 
                        "Saludo Personalizado", JOptionPane.INFORMATION_MESSAGE);
                txtNombre.setText("");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EventoSaludoPro().setVisible(true));
    }
}
