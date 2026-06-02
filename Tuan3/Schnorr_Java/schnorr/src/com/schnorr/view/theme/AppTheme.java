package com.schnorr.view.theme;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * THEME — Design tokens and shared widget factory.
 *
 * Palette: deep ocean-blue primary on a crisp white background.
 * All colours, fonts, and component factory methods live here so
 * every panel gets a consistent, professional look without duplication.
 */
public final class AppTheme {

    // ── Background tones ───────────────────────────────────────────────────────
    public static final Color BG_APP    = new Color(0xF0, 0xF4, 0xF9); // cool off-white
    public static final Color BG_CARD   = Color.WHITE;
    public static final Color BG_HEADER = new Color(0xE8, 0xF0, 0xFE); // blue-50
    public static final Color BG_INPUT  = new Color(0xF7, 0xF9, 0xFF); // faint blue-white

    // ── Primary — deep ocean blue ──────────────────────────────────────────────
    public static final Color PRIMARY       = new Color(0x1A, 0x56, 0xDB); // #1A56DB
    public static final Color PRIMARY_DARK  = new Color(0x10, 0x3A, 0xA3); // #103AA3
    public static final Color PRIMARY_LIGHT = new Color(0x45, 0x7F, 0xFF); // #457FFF
    public static final Color PRIMARY_PALE  = new Color(0xDB, 0xE8, 0xFF); // #DBE8FF tint

    // ── Semantic ───────────────────────────────────────────────────────────────
    public static final Color SUCCESS = new Color(0x05, 0x96, 0x69); // emerald-600
    public static final Color DANGER  = new Color(0xDC, 0x26, 0x26); // red-600
    public static final Color WARNING = new Color(0xD9, 0x77, 0x06); // amber-600
    public static final Color INFO    = new Color(0x06, 0x82, 0xC0); // sky-600

    // ── Neutrals ───────────────────────────────────────────────────────────────
    public static final Color TXT_DARK  = new Color(0x0F, 0x17, 0x2A); // near-black
    public static final Color TXT_MID   = new Color(0x1E, 0x40, 0x7C); // dark-blue text
    public static final Color TXT_DIM   = new Color(0x64, 0x74, 0x8B); // slate-500
    public static final Color BORDER    = new Color(0xBF, 0xD4, 0xF7); // soft blue border
    public static final Color BORDER_MD = new Color(0x93, 0xBB, 0xF4); // medium blue border
    public static final Color DIVIDER   = new Color(0xD1, 0xE3, 0xFA); // faint divider

    // ── Typography ─────────────────────────────────────────────────────────────
    public static final Font FONT_UI    = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD,   13);
    public static final Font FONT_MONO  = new Font("Consolas", Font.PLAIN,  13);
    public static final Font FONT_HEAD  = new Font("Segoe UI", Font.BOLD,   15);
    public static final Font FONT_STEP  = new Font("Segoe UI", Font.BOLD,   10);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN,  11);

    // ── Border factories ───────────────────────────────────────────────────────

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    public static Border inputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_MD, 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10));
    }

    public static Border inputFocusBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 2),
                BorderFactory.createEmptyBorder(6, 9, 6, 9));
    }

    // ── Widget factory ─────────────────────────────────────────────────────────

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    /** Editable text field with rounded corners and focus border. */
    public static JTextField textField() {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TXT_DARK);
        tf.setFont(FONT_MONO);
        tf.setBorder(inputBorder());
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { tf.setBorder(inputFocusBorder()); }
            public void focusLost(java.awt.event.FocusEvent e)   { tf.setBorder(inputBorder()); }
        });
        return tf;
    }

    /** Read-only output field with tinted background. */
    public static JTextField readonlyField(Color fg) {
        JTextField tf = textField();
        tf.setEditable(false);
        tf.setBackground(new Color(0xEE, 0xF3, 0xFF));
        tf.setForeground(fg);
        return tf;
    }

    /** Styled combo-box. */
    public static JComboBox<String> comboBox(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(BG_INPUT);
        cb.setForeground(TXT_DARK);
        cb.setFont(FONT_MONO);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return cb;
    }

    /** Filled primary-colour action button. */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isArmed()    ? PRIMARY_DARK
                           : getModel().isRollover() ? PRIMARY_LIGHT
                           : PRIMARY;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return b;
    }

    /** Outlined secondary button. */
    public static JButton outlineButton(String text, Color color) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 18));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setForeground(color);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return b;
    }

    /**
     * Small icon button with a folder/file symbol drawn via Graphics2D.
     * Never depends on emoji or platform fonts.
     */
    public static JButton fileButton() {
        JButton b = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                Color bg = getModel().isRollover()
                        ? new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 22)
                        : BG_INPUT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(BORDER_MD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                // Draw folder icon
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int fw = 14, fh = 10, fx = cx - fw / 2, fy = cy - fh / 2 + 1;
                g2.setColor(new Color(0xBF, 0xD4, 0xF7)); // folder body fill
                g2.fillRoundRect(fx, fy, fw, fh, 2, 2);
                g2.setColor(PRIMARY);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(fx, fy, fw, fh, 2, 2);
                // Tab on folder
                g2.fillRoundRect(fx, fy - 3, 6, 4, 2, 2);
                g2.setColor(PRIMARY);
                g2.drawRoundRect(fx, fy - 3, 6, 4, 2, 2);
                g2.dispose();
            }
        };
        styleIconButton(b);
        return b;
    }

    /** Legacy text-label icon button kept for backward compatibility. */
    public static JButton iconButton(String icon) {
        JButton b = new JButton(icon) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 18)
                        : BG_INPUT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(BORDER_MD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setForeground(PRIMARY);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        styleIconButton(b);
        return b;
    }

    private static void styleIconButton(JButton b) {
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setForeground(PRIMARY);
        b.setPreferredSize(new Dimension(42, 40));
        b.setMaximumSize(new Dimension(42, 40));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private AppTheme() { /* utility class */ }
}
