package practico1.tp1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class FormularioContacto extends JFrame {

    // Input fields
    private RoundTextField txtNombre;
    private RoundTextField txtApellido;
    private RoundTextField txtDni;
    private RoundTextField txtPasaporte;
    private RoundTextField txtTelefono;
    private RoundTextField txtCodigoPostal;
    private RoundTextField txtDomicilio;

    // Error labels
    private JLabel lblErrorNombre;
    private JLabel lblErrorApellido;
    private JLabel lblErrorDni;
    private JLabel lblErrorPasaporte;
    private JLabel lblErrorTelefono;
    private JLabel lblErrorCodigoPostal;
    private JLabel lblErrorDomicilio;

    // Guard flag to prevent recursion on document updates
    private boolean isUpdating = false;

    public FormularioContacto() {
        super("Carga de Contacto - Universidad del Aconcagua");
        initUI();
    }

    private void initUI() {
        setSize(520, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main background panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 18, 18)); // Premium dark background
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitle = new JLabel("Carga de Contacto", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(245, 245, 245));

        JLabel lblSubtitle = new JLabel("Licenciatura en Desarrollo de Software - Programación Avanzada", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(156, 163, 175)); // Muted text

        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Card Panel
        RoundPanel cardPanel = new RoundPanel(new GridBagLayout(), 15);
        cardPanel.setBackground(new Color(30, 30, 30)); // Lighter dark card background
        cardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // --- Nombre ---
        txtNombre = new RoundTextField(20, 10);
        lblErrorNombre = createErrorLabel();
        addFormRow(cardPanel, "Nombre:", txtNombre, lblErrorNombre, gbc, row);
        row += 2;

        // --- Apellido ---
        txtApellido = new RoundTextField(20, 10);
        lblErrorApellido = createErrorLabel();
        addFormRow(cardPanel, "Apellido:", txtApellido, lblErrorApellido, gbc, row);
        row += 2;

        // --- DNI ---
        txtDni = new RoundTextField(20, 10);
        lblErrorDni = createErrorLabel();
        addFormRow(cardPanel, "DNI:", txtDni, lblErrorDni, gbc, row);
        row += 2;

        // --- Pasaporte ---
        txtPasaporte = new RoundTextField(20, 10);
        lblErrorPasaporte = createErrorLabel();
        addFormRow(cardPanel, "Pasaporte:", txtPasaporte, lblErrorPasaporte, gbc, row);
        row += 2;

        // --- Teléfono ---
        txtTelefono = new RoundTextField(20, 10);
        lblErrorTelefono = createErrorLabel();
        addFormRow(cardPanel, "Teléfono:", txtTelefono, lblErrorTelefono, gbc, row);
        row += 2;

        // --- Código Postal ---
        txtCodigoPostal = new RoundTextField(20, 10);
        lblErrorCodigoPostal = createErrorLabel();
        addFormRow(cardPanel, "Código Postal:", txtCodigoPostal, lblErrorCodigoPostal, gbc, row);
        row += 2;

        // --- Domicilio ---
        txtDomicilio = new RoundTextField(20, 10);
        lblErrorDomicilio = createErrorLabel();
        addFormRow(cardPanel, "Domicilio:", txtDomicilio, lblErrorDomicilio, gbc, row);
        row += 2;

        // Vertical spacer to push elements up
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        cardPanel.add(Box.createGlue(), gbc);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Buttons Panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        RoundButton btnValidar = new RoundButton("Validar", new Color(37, 99, 235), new Color(29, 78, 216), 10);
        RoundButton btnLimpiar = new RoundButton("Limpiar", new Color(75, 85, 99), new Color(55, 65, 81), 10);
        RoundButton btnCerrar = new RoundButton("Cerrar", new Color(220, 38, 38), new Color(185, 28, 28), 10);

        buttonPanel.add(btnValidar);
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnCerrar);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // Apply DocumentFilters for character-level validation
        ((AbstractDocument) txtNombre.getDocument()).setDocumentFilter(ValidadorCaracteres.getFiltroAlfabetico(20));
        ((AbstractDocument) txtApellido.getDocument()).setDocumentFilter(ValidadorCaracteres.getFiltroAlfabetico(20));
        ((AbstractDocument) txtDni.getDocument()).setDocumentFilter(ValidadorCaracteres.getFiltroNumerico(8));
        ((AbstractDocument) txtPasaporte.getDocument()).setDocumentFilter(ValidadorCaracteres.getFiltroPasaporte());
        ((AbstractDocument) txtTelefono.getDocument()).setDocumentFilter(ValidadorCaracteres.getFiltroTelefono(25));
        ((AbstractDocument) txtCodigoPostal.getDocument()).setDocumentFilter(ValidadorCaracteres.getFiltroNumerico(4));
        ((AbstractDocument) txtDomicilio.getDocument()).setDocumentFilter(ValidadorCaracteres.getFiltroLongitudMaxima(50));

        // Setup dynamic mutual exclusion between DNI and Pasaporte
        setupMutualExclusion();

        // Setup FocusListeners for field validation (outside field validation)
        setupFocusValidators();

        // Button action handlers
        btnValidar.addActionListener(e -> validarFormulario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnCerrar.addActionListener(e -> {
            dispose();
            System.exit(0);
        });
    }

    private void addFormRow(JPanel panel, String labelText, RoundTextField field, JLabel errorLabel, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(229, 231, 235));

        // Label constraints
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(12, 5, 0, 10);
        panel.add(label, gbc);

        // Field constraints
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 0, 0, 5);
        panel.add(field, gbc);

        // Error Label constraints
        gbc.gridx = 1;
        gbc.gridy = row + 1;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 4, 2, 5);
        panel.add(errorLabel, gbc);
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        label.setForeground(new Color(239, 68, 68)); // Material Red
        return label;
    }

    private void setupMutualExclusion() {
        // Document listener for DNI to disable Pasaporte if filled
        txtDni.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                SwingUtilities.invokeLater(() -> {
                    if (isUpdating) return;
                    isUpdating = true;
                    try {
                        boolean hasText = txtDni.getText().trim().length() > 0;
                        if (hasText) {
                            if (txtPasaporte.isEnabled()) {
                                txtPasaporte.setEnabled(false);
                            }
                            if (!txtPasaporte.getText().isEmpty()) {
                                txtPasaporte.setText("");
                            }
                            lblErrorPasaporte.setText("");
                            txtPasaporte.setError(false);
                        } else {
                            if (!txtPasaporte.isEnabled()) {
                                txtPasaporte.setEnabled(true);
                            }
                        }
                    } finally {
                        isUpdating = false;
                    }
                });
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        // Document listener for Pasaporte to disable DNI if filled
        txtPasaporte.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                SwingUtilities.invokeLater(() -> {
                    if (isUpdating) return;
                    isUpdating = true;
                    try {
                        boolean hasText = txtPasaporte.getText().trim().length() > 0;
                        if (hasText) {
                            if (txtDni.isEnabled()) {
                                txtDni.setEnabled(false);
                            }
                            if (!txtDni.getText().isEmpty()) {
                                txtDni.setText("");
                            }
                            lblErrorDni.setText("");
                            txtDni.setError(false);
                        } else {
                            if (!txtDni.isEnabled()) {
                                txtDni.setEnabled(true);
                            }
                        }
                    } finally {
                        isUpdating = false;
                    }
                });
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
    }

    private void setupFocusValidators() {
        txtNombre.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarNombre(true);
            }
        });
        txtApellido.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarApellido(true);
            }
        });
        txtDni.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (txtDni.isEnabled() && !txtDni.getText().trim().isEmpty()) {
                    validarDocumentacion(true);
                }
            }
        });
        txtPasaporte.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (txtPasaporte.isEnabled() && !txtPasaporte.getText().trim().isEmpty()) {
                    validarDocumentacion(true);
                }
            }
        });
        txtTelefono.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarTelefono(true);
            }
        });
        txtCodigoPostal.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarCodigoPostal(true);
            }
        });
        txtDomicilio.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarDomicilio(true);
            }
        });
    }

    // --- Validation Logic ---

    private boolean validarNombre(boolean showFieldError) {
        String text = txtNombre.getText().trim();
        if (text.isEmpty()) {
            if (showFieldError) {
                txtNombre.setError(true);
                lblErrorNombre.setText("El nombre es requerido.");
            }
            return false;
        }
        if (!text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+")) {
            if (showFieldError) {
                txtNombre.setError(true);
                lblErrorNombre.setText("Solo debe contener letras.");
            }
            return false;
        }
        txtNombre.setError(false);
        lblErrorNombre.setText("");
        return true;
    }

    private boolean validarApellido(boolean showFieldError) {
        String text = txtApellido.getText().trim();
        if (text.isEmpty()) {
            if (showFieldError) {
                txtApellido.setError(true);
                lblErrorApellido.setText("El apellido es requerido.");
            }
            return false;
        }
        if (!text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+")) {
            if (showFieldError) {
                txtApellido.setError(true);
                lblErrorApellido.setText("Solo debe contener letras.");
            }
            return false;
        }
        txtApellido.setError(false);
        lblErrorApellido.setText("");
        return true;
    }

    private boolean validarDocumentacion(boolean showFieldError) {
        String dniText = txtDni.getText().trim();
        String pasaporteText = txtPasaporte.getText().trim();

        boolean hasDni = !dniText.isEmpty();
        boolean hasPasaporte = !pasaporteText.isEmpty();

        // 1. Both empty check
        if (!hasDni && !hasPasaporte) {
            if (showFieldError) {
                txtDni.setError(true);
                txtPasaporte.setError(true);
                lblErrorDni.setText("Debe completar DNI o Pasaporte.");
                lblErrorPasaporte.setText("Debe completar DNI o Pasaporte.");
            }
            return false;
        }

        // 2. Both filled check (failsafe, though UI blocks this)
        if (hasDni && hasPasaporte) {
            if (showFieldError) {
                txtDni.setError(true);
                txtPasaporte.setError(true);
                lblErrorDni.setText("Solo uno de ellos puede llevar un valor.");
                lblErrorPasaporte.setText("Solo uno de ellos puede llevar un valor.");
            }
            return false;
        }

        // 3. Validate DNI if present
        if (hasDni) {
            if (dniText.length() != 8) {
                if (showFieldError) {
                    txtDni.setError(true);
                    lblErrorDni.setText("El DNI debe tener exactamente 8 dígitos.");
                }
                return false;
            }
            try {
                long val = Long.parseLong(dniText);
                if (val < 10000000 || val > 60000000) {
                    if (showFieldError) {
                        txtDni.setError(true);
                        lblErrorDni.setText("Rango inválido (10.000.000 a 60.000.000).");
                    }
                    return false;
                }
            } catch (NumberFormatException e) {
                if (showFieldError) {
                    txtDni.setError(true);
                    lblErrorDni.setText("DNI inválido.");
                }
                return false;
            }
            txtDni.setError(false);
            lblErrorDni.setText("");
            txtPasaporte.setError(false);
            lblErrorPasaporte.setText("");
            return true;
        }

        // 4. Validate Pasaporte if present
        if (hasPasaporte) {
            if (pasaporteText.length() != 9) {
                if (showFieldError) {
                    txtPasaporte.setError(true);
                    lblErrorPasaporte.setText("Debe tener 1 letra y 8 dígitos.");
                }
                return false;
            }
            char first = pasaporteText.charAt(0);
            if (!Character.isLetter(first)) {
                if (showFieldError) {
                    txtPasaporte.setError(true);
                    lblErrorPasaporte.setText("Debe iniciar con una letra.");
                }
                return false;
            }
            String numbersText = pasaporteText.substring(1);
            if (!numbersText.matches("[0-9]{8}")) {
                if (showFieldError) {
                    txtPasaporte.setError(true);
                    lblErrorPasaporte.setText("Debe contener 8 dígitos tras la letra.");
                }
                return false;
            }
            try {
                long val = Long.parseLong(numbersText);
                if (val < 10000000 || val > 60000000) {
                    if (showFieldError) {
                        txtPasaporte.setError(true);
                        lblErrorPasaporte.setText("El número debe estar entre 10M y 60M.");
                    }
                    return false;
                }
            } catch (NumberFormatException e) {
                if (showFieldError) {
                    txtPasaporte.setError(true);
                    lblErrorPasaporte.setText("Pasaporte inválido.");
                }
                return false;
            }
            txtPasaporte.setError(false);
            lblErrorPasaporte.setText("");
            txtDni.setError(false);
            lblErrorDni.setText("");
            return true;
        }

        return true;
    }

    private boolean validarTelefono(boolean showFieldError) {
        String text = txtTelefono.getText().trim();
        if (text.isEmpty()) {
            if (showFieldError) {
                txtTelefono.setError(true);
                lblErrorTelefono.setText("El teléfono es requerido.");
            }
            return false;
        }

        // Count only numerical digits in telephone
        int digitCount = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) {
                digitCount++;
            }
        }

        if (digitCount <= 6) {
            if (showFieldError) {
                txtTelefono.setError(true);
                lblErrorTelefono.setText("Debe contener más de 6 dígitos numéricos.");
            }
            return false;
        }
        txtTelefono.setError(false);
        lblErrorTelefono.setText("");
        return true;
    }

    private boolean validarCodigoPostal(boolean showFieldError) {
        String text = txtCodigoPostal.getText().trim();
        if (text.isEmpty()) {
            if (showFieldError) {
                txtCodigoPostal.setError(true);
                lblErrorCodigoPostal.setText("El código postal es requerido.");
            }
            return false;
        }
        if (text.length() != 4 || !text.matches("[0-9]{4}")) {
            if (showFieldError) {
                txtCodigoPostal.setError(true);
                lblErrorCodigoPostal.setText("Debe tener exactamente 4 dígitos.");
            }
            return false;
        }
        txtCodigoPostal.setError(false);
        lblErrorCodigoPostal.setText("");
        return true;
    }

    private boolean validarDomicilio(boolean showFieldError) {
        String text = txtDomicilio.getText().trim();
        if (text.isEmpty()) {
            if (showFieldError) {
                txtDomicilio.setError(true);
                lblErrorDomicilio.setText("El domicilio es requerido.");
            }
            return false;
        }
        if (text.length() > 50) {
            if (showFieldError) {
                txtDomicilio.setError(true);
                lblErrorDomicilio.setText("Máximo 50 caracteres.");
            }
            return false;
        }
        txtDomicilio.setError(false);
        lblErrorDomicilio.setText("");
        return true;
    }

    private void validarFormulario() {
        // Run all validations to show errors in fields simultaneously
        boolean isNombreValid = validarNombre(true);
        boolean isApellidoValid = validarApellido(true);
        boolean isDocValid = validarDocumentacion(true);
        boolean isTelValid = validarTelefono(true);
        boolean isCpValid = validarCodigoPostal(true);
        boolean isDomicilioValid = validarDomicilio(true);

        if (isNombreValid && isApellidoValid && isDocValid && isTelValid && isCpValid && isDomicilioValid) {
            // Success! Create the domain object
            Contacto contacto = new Contacto(
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtDni.getText().trim(),
                    txtPasaporte.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCodigoPostal.getText().trim(),
                    txtDomicilio.getText().trim()
            );

            // Display a modern success dialog
            SuccessDialog dialog = new SuccessDialog(this, contacto);
            dialog.setVisible(true);
        } else {
            // Highlight issue with a custom warning JOptionPane
            JOptionPane.showMessageDialog(this,
                    "Por favor, corrija los errores marcados en el formulario antes de continuar.",
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtDni.setText("");
        txtPasaporte.setText("");
        txtTelefono.setText("");
        txtCodigoPostal.setText("");
        txtDomicilio.setText("");

        // Reset errors
        txtNombre.setError(false);
        txtApellido.setError(false);
        txtDni.setError(false);
        txtPasaporte.setError(false);
        txtTelefono.setError(false);
        txtCodigoPostal.setError(false);
        txtDomicilio.setError(false);

        lblErrorNombre.setText("");
        lblErrorApellido.setText("");
        lblErrorDni.setText("");
        lblErrorPasaporte.setText("");
        lblErrorTelefono.setText("");
        lblErrorCodigoPostal.setText("");
        lblErrorDomicilio.setText("");

        // Re-enable both fields
        txtDni.setEnabled(true);
        txtPasaporte.setEnabled(true);
    }

    public static void main(String[] args) {
        // Use standard system appearance styles internally, but with our overrides
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            FormularioContacto frame = new FormularioContacto();
            frame.setVisible(true);
        });
    }
}

// ==========================================
// Custom UI Components for Rich Aesthetics
// ==========================================

/**
 * Panel with rounded corners
 */
class RoundPanel extends JPanel {
    private final int round;

    public RoundPanel(LayoutManager layout, int round) {
        super(layout);
        this.round = round;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), round, round);
        g2.dispose();
        super.paintComponent(g);
    }
}

/**
 * Custom text field with rounded corners, placeholder capabilities, and interactive colors
 */
class RoundTextField extends JTextField {
    private final int round;
    private final Color borderColor = new Color(75, 85, 99); // Dark border
    private final Color focusBorderColor = new Color(59, 130, 246); // Blue accent
    private final Color errorBorderColor = new Color(239, 68, 68); // Red error border
    private boolean isError = false;

    public RoundTextField(int columns, int round) {
        super(columns);
        this.round = round;
        setOpaque(false);
        setBackground(new Color(45, 45, 45)); // Dark input background
        setForeground(new Color(245, 245, 245)); // Light text color
        setCaretColor(new Color(245, 245, 245));
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // Internal padding
    }

    public void setError(boolean error) {
        this.isError = error;
        repaint();
    }

    @Override
    public Color getBackground() {
        if (!isEnabled()) {
            return new Color(30, 30, 30); // Dim/match panel background
        }
        return super.getBackground();
    }

    @Override
    public Color getForeground() {
        if (!isEnabled()) {
            return new Color(107, 114, 128); // Greyed out text
        }
        return super.getForeground();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), round, round);
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        if (!isEnabled()) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isError) {
            g2.setColor(errorBorderColor);
            g2.setStroke(new BasicStroke(1.5f));
        } else if (isFocusOwner()) {
            g2.setColor(focusBorderColor);
            g2.setStroke(new BasicStroke(1.5f));
        } else {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, round, round);
        g2.dispose();
    }
}

/**
 * Premium custom button with hover interactions and rounded corners
 */
class RoundButton extends JButton {
    private final int round;
    private final Color baseBg;
    private final Color hoverBg;
    private boolean isHovered = false;

    public RoundButton(String text, Color baseBg, Color hoverBg, int round) {
        super(text);
        this.baseBg = baseBg;
        this.hoverBg = hoverBg;
        this.round = round;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isHovered ? hoverBg : baseBg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), round, round);
        
        // Add subtle lighting reflect on hover
        if (isHovered) {
            g2.setColor(new Color(255, 255, 255, 25));
            g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, round, round);
        }
        
        super.paintComponent(g2);
        g2.dispose();
    }
}

/**
 * Modern popup modal for successful registrations
 */
class SuccessDialog extends JDialog {
    public SuccessDialog(Frame owner, Contacto contacto) {
        super(owner, "Registro Exitoso", true);
        setSize(420, 360);
        setLocationRelativeTo(owner);
        setResizable(false);

        // Root Panel
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblTitle = new JLabel("✓ ¡Contacto Registrado!", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(16, 185, 129)); // Success green
        panel.add(lblTitle, BorderLayout.NORTH);

        // Body details
        JTextArea txtDetails = new JTextArea();
        txtDetails.setEditable(false);
        txtDetails.setOpaque(false);
        txtDetails.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDetails.setForeground(new Color(245, 245, 245));
        txtDetails.setText(contacto.toString());
        txtDetails.setMargin(new Insets(10, 10, 10, 10));

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(18, 18, 18));
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(75, 85, 99), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        detailsPanel.add(txtDetails, BorderLayout.CENTER);
        panel.add(detailsPanel, BorderLayout.CENTER);

        // Close Button
        RoundButton btnOk = new RoundButton("Aceptar", new Color(16, 185, 129), new Color(5, 150, 105), 10);
        btnOk.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnOk);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
    }
}
