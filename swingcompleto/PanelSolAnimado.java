package practico1.swingcompleto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelSolAnimado extends JPanel implements ActionListener {
    private boolean ojosAbiertos = true;
    private Timer timer;

    public PanelSolAnimado() {
        timer = new Timer(500, this); // Parpadeo cada 500ms
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Anti-aliasing para bordes suaves
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Fondo con gradiente
        GradientPaint gradiente = new GradientPaint(0, 0, new Color(135, 206, 235), 0, height, Color.WHITE);
        g2d.setPaint(gradiente);
        g2d.fillRect(0, 0, width, height);

        // Centro y radio del sol
        int cx = width / 2;
        int cy = height / 2;
        int radio = 50;

        // Dibujar rayos (líneas)
        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < 12; i++) {
            double angulo = i * Math.PI / 6;
            int x1 = (int) (cx + (radio + 10) * Math.cos(angulo));
            int y1 = (int) (cy + (radio + 10) * Math.sin(angulo));
            int x2 = (int) (cx + (radio + 40) * Math.cos(angulo));
            int y2 = (int) (cy + (radio + 40) * Math.sin(angulo));
            g2d.drawLine(x1, y1, x2, y2);
        }

        // Dibujar el sol
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(cx - radio, cy - radio, radio * 2, radio * 2);
        g2d.setColor(Color.ORANGE);
        g2d.drawOval(cx - radio, cy - radio, radio * 2, radio * 2);

        // Ojos
        g2d.setColor(Color.BLACK);
        if (ojosAbiertos) {
            g2d.fillOval(cx - 20, cy - 15, 10, 10);
            g2d.fillOval(cx + 10, cy - 15, 10, 10);
        } else {
            g2d.drawLine(cx - 20, cy - 10, cx - 10, cy - 10);
            g2d.drawLine(cx + 10, cy - 10, cx + 20, cy - 10);
        }

        // Sonrisa (drawArc)
        g2d.setColor(Color.RED);
        g2d.drawArc(cx - 20, cy - 10, 40, 30, 180, 180);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ojosAbiertos = !ojosAbiertos;
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sol Animado");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setLocationRelativeTo(null);
            frame.add(new PanelSolAnimado());
            frame.setVisible(true);
        });
    }
}
