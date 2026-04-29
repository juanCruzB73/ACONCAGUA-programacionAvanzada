package practico1.swingcompleto;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormularioUsuario extends JFrame {
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JComboBox<String> cmbDia, cmbMes, cmbAnio;
    private JButton btnGuardar;

    public FormularioUsuario() {
        super("Formulario de Usuario");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel Principal con GridLayout
        JPanel panelCentral = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCentral.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Datos Personales", TitledBorder.LEFT, TitledBorder.TOP));

        panelCentral.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelCentral.add(txtNombre);

        panelCentral.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelCentral.add(txtApellido);

        // Panel Fecha con FlowLayout
        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelFecha.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Fecha de Nacimiento", TitledBorder.LEFT, TitledBorder.TOP));
        
        cmbDia = new JComboBox<>(new String[]{"1", "2", "3", "15", "30"});
        cmbMes = new JComboBox<>(new String[]{"Enero", "Febrero", "Marzo"});
        cmbAnio = new JComboBox<>(new String[]{"1990", "2000", "2010"});

        panelFecha.add(new JLabel("Día:"));
        panelFecha.add(cmbDia);
        panelFecha.add(new JLabel("Mes:"));
        panelFecha.add(cmbMes);
        panelFecha.add(new JLabel("Año:"));
        panelFecha.add(cmbAnio);

        // Panel Inferior para botón
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(FormularioUsuario.this, 
                            "Por favor, complete todos los campos de texto.", 
                            "Error de Validación", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(FormularioUsuario.this, 
                            "Usuario guardado exitosamente.", 
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        panelInferior.add(btnGuardar);

        add(panelCentral, BorderLayout.NORTH);
        add(panelFecha, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormularioUsuario().setVisible(true));
    }
}
