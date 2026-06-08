package com.schnorr.view.components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static com.schnorr.view.theme.AppTheme.*;

/**
 * VIEW COMPONENT — A titled step card with a left accent stripe.
 *
 * <pre>
 *  ┌──────────────────────────────────────────┐
 *  │ ▌  BƯỚC 01  THAM SỐ HỆ THỐNG            │  ← header (BG_HEADER)
 *  │    Subtitle text …                        │
 *  ├──────────────────────────────────────────┤
 *  │   body content added via getBody()       │  ← body panel
 *  └──────────────────────────────────────────┘
 * </pre>
 */
public class StepCard extends JPanel {

    private final JPanel body;

    public StepCard(String stepNum, String title, String subtitle) {
        setLayout(new BorderLayout());
        setBackground(BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        // ── Left accent stripe ─────────────────────────────────────────────────
        JPanel stripe = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, PRIMARY, 0, getHeight(), PRIMARY_LIGHT));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        stripe.setPreferredSize(new Dimension(5, 0));

        // ── Card header ────────────────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_HEADER);
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        JLabel badge = new JLabel("BƯỚC " + stepNum);
        badge.setFont(FONT_STEP);
        badge.setForeground(Color.WHITE);
        badge.setBackground(PRIMARY);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 7, 3, 7));

        titleRow.add(badge);
        titleRow.add(label(title.toUpperCase(), FONT_HEAD, TXT_DARK));
        header.add(titleRow);
        header.add(Box.createVerticalStrut(4));

        JLabel sub = label(subtitle, FONT_SMALL, TXT_DIM);
        sub.setBorder(new EmptyBorder(0, 0, 0, 0));
        header.add(sub);

        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setBackground(BG_HEADER);
        headerWrap.add(header, BorderLayout.CENTER);
        headerWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));

        // ── Body ───────────────────────────────────────────────────────────────
        body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG_CARD);
        body.setBorder(new EmptyBorder(18, 18, 20, 18));

        // ── Assemble ───────────────────────────────────────────────────────────
        add(stripe,     BorderLayout.WEST);
        add(headerWrap, BorderLayout.NORTH);
        add(body,       BorderLayout.CENTER);
    }

    /** Returns the body panel where child components are added. */
    public JPanel getBody() { return body; }

    // ── Static layout helpers ──────────────────────────────────────────────────

    /** Creates a row of {@code cols} equally-sized components with gaps. */
    public static JPanel rowOf(int cols, JComponent... comps) {
        JPanel p = new JPanel(new GridLayout(1, cols, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        for (JComponent c : comps) p.add(c);
        return p;
    }

    /** Wraps {@code c} in a full-width panel. */
    public static JPanel fullWidth(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    /** Vertical gap spacer. */
    public static Component gap(int px) {
        return Box.createVerticalStrut(px);
    }
}
