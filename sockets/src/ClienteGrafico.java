package practico1.sockets.src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

public class ClienteGrafico extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Login Panel Components
    private RoundTextField txtHost;
    private RoundTextField txtPort;
    private RoundTextField txtUsername;
    private JLabel lblErrorUser;
    private RoundButton btnConnect;

    // Chat Panel Components
    private JTextArea txtChatArea;
    private RoundTextField txtMessageInput;
    private JLabel lblStatus;

    // Socket Connection State
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private volatile boolean connected = false;
    private Thread receiverThread;
    private String usernameConectado;

    public ClienteGrafico() {
        super("Cliente Chat Multi-Hilo - Universidad del Aconcagua");
        initUI();
    }

    private void initUI() {
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 500));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(18, 18, 18));

        // Create the two views
        JPanel loginView = createLoginView();
        JPanel chatView = createChatView();

        cardPanel.add(loginView, "LOGIN");
        cardPanel.add(chatView, "CHAT");

        setContentPane(cardPanel);
        cardLayout.show(cardPanel, "LOGIN");

        // Apply character-level validation filter to username
        ((AbstractDocument) txtUsername.getDocument()).setDocumentFilter(new ValidadorUsuario(15));
    }

    private JPanel createLoginView() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(18, 18, 18));

        // Login Card Panel
        RoundPanel card = new RoundPanel(new GridBagLayout(), 15);
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setPreferredSize(new Dimension(380, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        // Header Title
        JLabel lblTitle = new JLabel("Conectarse al Chat", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(245, 245, 245));
        gbc.gridy = 0;
        card.add(lblTitle, gbc);

        // Subtitle
        JLabel lblSub = new JLabel("Ingrese las credenciales del servidor", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(156, 163, 175));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(lblSub, gbc);

        // Reset insets
        gbc.insets = new Insets(5, 0, 2, 0);

        // Host Input
        JLabel lblHost = new JLabel("Dirección del Servidor (Host):");
        lblHost.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHost.setForeground(new Color(229, 231, 235));
        gbc.gridy = 2;
        card.add(lblHost, gbc);

        txtHost = new RoundTextField(15, 8);
        txtHost.setText("localhost");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(txtHost, gbc);

        // Port Input
        gbc.insets = new Insets(5, 0, 2, 0);
        JLabel lblPort = new JLabel("Puerto:");
        lblPort.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPort.setForeground(new Color(229, 231, 235));
        gbc.gridy = 4;
        card.add(lblPort, gbc);

        txtPort = new RoundTextField(15, 8);
        txtPort.setText("5000");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(txtPort, gbc);

        // Username Input
        gbc.insets = new Insets(5, 0, 2, 0);
        JLabel lblUser = new JLabel("Nombre de Usuario (Sin espacios, >2 car.):");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(229, 231, 235));
        gbc.gridy = 6;
        card.add(lblUser, gbc);

        txtUsername = new RoundTextField(15, 8);
        gbc.gridy = 7;
        card.add(txtUsername, gbc);

        // Error message label
        lblErrorUser = new JLabel("");
        lblErrorUser.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblErrorUser.setForeground(new Color(239, 68, 68));
        gbc.gridy = 8;
        gbc.insets = new Insets(2, 4, 15, 0);
        card.add(lblErrorUser, gbc);

        // Connect Button
        btnConnect = new RoundButton("Conectar", new Color(37, 99, 235), new Color(29, 78, 216), 8);
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 0, 5, 0);
        card.add(btnConnect, gbc);

        // Setup actions for Login Card
        btnConnect.addActionListener(e -> attemptConnect());
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarNombreUsuario(true);
            }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptConnect();
                }
            }
        });

        // Add Card to View Panel
        panel.add(card);
        return panel;
    }

    private JPanel createChatView() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(18, 18, 18));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // North: Connection info topbar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

        lblStatus = new JLabel("Desconectado");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(new Color(16, 185, 129)); // Success Green

        topPanel.add(lblStatus, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Center: Chat messages area
        txtChatArea = new JTextArea();
        txtChatArea.setEditable(false);
        txtChatArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtChatArea.setBackground(new Color(30, 30, 30));
        txtChatArea.setForeground(new Color(240, 240, 240));
        txtChatArea.setLineWrap(true);
        txtChatArea.setWrapStyleWord(true);
        txtChatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(txtChatArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(75, 85, 99), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // East: Sidebar Panel for Commands
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(140, 0));

        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.insets = new Insets(0, 0, 8, 0);
        sgbc.gridx = 0;
        int sRow = 0;

        JLabel lblCmd = new JLabel("Acciones Rápidas", JLabel.CENTER);
        lblCmd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCmd.setForeground(new Color(156, 163, 175));
        sgbc.gridy = sRow++;
        sgbc.insets = new Insets(0, 0, 12, 0);
        sidebar.add(lblCmd, sgbc);

        sgbc.insets = new Insets(0, 0, 8, 0);

        RoundButton btnList = new RoundButton("Listar Usuarios", new Color(75, 85, 99), new Color(55, 65, 81), 6);
        btnList.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sgbc.gridy = sRow++;
        sidebar.add(btnList, sgbc);

        RoundButton btnFecha = new RoundButton("Ver Fecha", new Color(75, 85, 99), new Color(55, 65, 81), 6);
        btnFecha.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sgbc.gridy = sRow++;
        sidebar.add(btnFecha, sgbc);

        RoundButton btnHora = new RoundButton("Ver Hora", new Color(75, 85, 99), new Color(55, 65, 81), 6);
        btnHora.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sgbc.gridy = sRow++;
        sidebar.add(btnHora, sgbc);

        RoundButton btnHelp = new RoundButton("Ayuda", new Color(75, 85, 99), new Color(55, 65, 81), 6);
        btnHelp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sgbc.gridy = sRow++;
        sidebar.add(btnHelp, sgbc);

        // Spacer to push the disconnect button down
        sgbc.gridy = sRow++;
        sgbc.weighty = 1.0;
        sidebar.add(Box.createGlue(), sgbc);

        RoundButton btnDisconnect = new RoundButton("Desconectar", new Color(220, 38, 38), new Color(185, 28, 28), 6);
        btnDisconnect.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sgbc.gridy = sRow++;
        sgbc.weighty = 0.0;
        sgbc.insets = new Insets(0, 0, 0, 0);
        sidebar.add(btnDisconnect, sgbc);

        panel.add(sidebar, BorderLayout.EAST);

        // South: Message input panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        txtMessageInput = new RoundTextField(20, 8);
        txtMessageInput.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        RoundButton btnSend = new RoundButton("Enviar", new Color(37, 99, 235), new Color(29, 78, 216), 8);

        inputPanel.add(txtMessageInput, BorderLayout.CENTER);
        inputPanel.add(btnSend, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.SOUTH);

        // Sidebar actions
        btnList.addActionListener(e -> sendCommand("LIST"));
        btnFecha.addActionListener(e -> sendCommand("FECHA"));
        btnHora.addActionListener(e -> sendCommand("HORA"));
        btnHelp.addActionListener(e -> sendCommand("HELP"));
        btnDisconnect.addActionListener(e -> handleDisconnect("Conexión cerrada por el usuario."));

        // Sending actions
        btnSend.addActionListener(e -> sendMessage());
        txtMessageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        return panel;
    }

    // --- Actions & Socket Handlers ---

    private boolean validarNombreUsuario(boolean showFieldError) {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            if (showFieldError) {
                txtUsername.setError(true);
                lblErrorUser.setText("El nombre de usuario es requerido.");
            }
            return false;
        }
        if (username.length() < 3) {
            if (showFieldError) {
                txtUsername.setError(true);
                lblErrorUser.setText("Mínimo 3 caracteres.");
            }
            return false;
        }
        if (username.contains(" ")) {
            if (showFieldError) {
                txtUsername.setError(true);
                lblErrorUser.setText("No debe contener espacios.");
            }
            return false;
        }
        txtUsername.setError(false);
        lblErrorUser.setText("");
        return true;
    }

    private void attemptConnect() {
        String host = txtHost.getText().trim();
        String portStr = txtPort.getText().trim();
        String username = txtUsername.getText().trim();

        if (!validarNombreUsuario(true)) {
            return;
        }

        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el host del servidor.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
            if (port < 1024 || port > 65535) {
                JOptionPane.showMessageDialog(this, "Puerto inválido (1024-65535).", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El puerto debe ser un número entero.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnConnect.setEnabled(false);
        btnConnect.setText("Conectando...");

        // Async socket connection to avoid GUI thread locking
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                // Handshake protocol
                String serverMsg = entrada.readLine();
                if (serverMsg != null && serverMsg.startsWith("CONEXION_EXITOSA")) {
                    // Send username
                    salida.println(username);

                    SwingUtilities.invokeLater(() -> {
                        this.usernameConectado = username;
                        lblStatus.setText("Conectado como: " + username + "  |  Servidor: " + host + ":" + port);
                        txtChatArea.setText(""); // Reset history
                        cardLayout.show(cardPanel, "CHAT");
                        connected = true;
                        btnConnect.setEnabled(true);
                        btnConnect.setText("Conectar");

                        // Spawn receiver background thread
                        receiverThread = new Thread(new ReceptorRunnable());
                        receiverThread.start();
                    });
                } else {
                    throw new IOException("Fallo en el protocolo de inicio del servidor.");
                }

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    btnConnect.setEnabled(true);
                    btnConnect.setText("Conectar");
                    JOptionPane.showMessageDialog(this,
                            "No se pudo establecer la conexión:\n" + ex.getMessage(),
                            "Error de Conexión",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void sendMessage() {
        if (!connected) return;
        String text = txtMessageInput.getText().trim();
        if (text.isEmpty()) return;

        // Write directly to socket printwriter
        salida.println(text);

        // Echo command/message on local chat text area
        txtChatArea.append("> " + text + "\n");
        txtChatArea.setCaretPosition(txtChatArea.getDocument().getLength());

        txtMessageInput.setText("");

        // If local user requested exit, close connection cleanly
        if (text.equalsIgnoreCase("EXIT")) {
            handleDisconnect("Desconectado de la sala.");
        }
    }

    private void sendCommand(String command) {
        if (!connected) return;
        salida.println(command);
        txtChatArea.append("> " + command + "\n");
        txtChatArea.setCaretPosition(txtChatArea.getDocument().getLength());
    }

    private void handleDisconnect(String reason) {
        if (!connected) return;
        connected = false;

        try {
            if (salida != null) {
                salida.println("EXIT");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}

        SwingUtilities.invokeLater(() -> {
            cardLayout.show(cardPanel, "LOGIN");
            if (reason != null && !reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, reason, "Información de Desconexión", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    // --- Message Receiver Thread ---
    private class ReceptorRunnable implements Runnable {
        @Override
        public void run() {
            try {
                String line;
                // Wait and read messages from server
                while (connected && (line = entrada.readLine()) != null) {
                    final String message = line;
                    SwingUtilities.invokeLater(() -> {
                        txtChatArea.append(message + "\n");
                        // Scroll to bottom
                        txtChatArea.setCaretPosition(txtChatArea.getDocument().getLength());
                    });
                }
            } catch (IOException e) {
                // Occurs when socket is closed cleanly
            } finally {
                // Return to login screen if connection drops unexpectedly
                if (connected) {
                    handleDisconnect("La conexión con el servidor se ha interrumpido.");
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            ClienteGrafico frame = new ClienteGrafico();
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
 * Custom text field with rounded corners and focus indicator
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
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

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

