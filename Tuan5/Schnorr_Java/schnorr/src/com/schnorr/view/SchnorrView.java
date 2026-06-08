package com.schnorr.view;

import com.schnorr.view.components.FieldPanel;
import com.schnorr.view.components.StepCard;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

import static com.schnorr.view.components.StepCard.*;
import static com.schnorr.view.theme.AppTheme.*;

/**
 * VIEW — Main application window (JFrame).
 * Optimized Layout: Eliminates vertical stretching, fixes empty gaps.
 * Strict Read-Only UX: Auto-calculated fields completely reject focus and clicks.
 * Math Formulas: Clean Unicode characters with HTML formatting for flawless rendering.
 */
public class SchnorrView extends JFrame {

    // ── Step 1 — System Parameters (User Input) ────────────────────────────────
    private JTextField        tfP, tfQ, tfG;
    private JComboBox<String> cbHash;
    private JLabel            lblParamStatus;

    // ── Step 2 — Key Generation (Input & Auto-calculated) ──────────────────────
    private JTextField tfX;          // User Input or Random Generated
    private JTextField tfY;          // STRICT READ-ONLY: Public Key
    private JTextField tfYFormula;   // STRICT READ-ONLY: Formula Display

    // ── Step 3 — Signing (Input & Auto-calculated) ─────────────────────────────
    private JTextField tfMsg;        // User Input or File Load
    private JTextField tfK;          // User Input or Random Generated
    private JTextField tfR;          // STRICT READ-ONLY: Commitment
    private JTextField tfE;          // STRICT READ-ONLY: Hash e
    private JTextField tfSigE;       // STRICT READ-ONLY: Signature component e
    private JTextField tfSigS;       // STRICT READ-ONLY: Signature component s

    // ── Step 4 — Verification (Input & Auto-calculated) ────────────────────────
    private JTextField tfVMsg;       // User Input or Prefilled
    private JTextField tfVE;         // User Input or Prefilled
    private JTextField tfVS;         // User Input or Prefilled
    private JTextField tfVR;         // STRICT READ-ONLY: Calculated r'
    private JTextField tfVECalc;     // STRICT READ-ONLY: Calculated e'
    private JLabel     lblVerifyResult;
    private JPanel     pnlVerifyResult;

    // ── Buttons ────────────────────────────────────────────────────────────────
    private JButton btnCheckParams, btnGenPrivKey, btnCalcPubKey;
    private JButton btnGenNonce, btnSign, btnVerify;
    private JButton btnLoadMsgSign, btnLoadMsgVerify, btnReset;
    private JButton btnSaveSign, btnLoadSign;

    // ── Constructor ────────────────────────────────────────────────────────────
    public SchnorrView() {
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setTitle("Schnorr Signature Simulator — Chữ Ký Số Schnorr");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 840);
        setMinimumSize(new Dimension(1080, 760));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_APP);
        setLayout(new BorderLayout());

        add(buildAppHeader(),    BorderLayout.NORTH);
        add(buildScrollContent(), BorderLayout.CENTER);
    }

    private JPanel buildAppHeader() {
        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_DARK, getWidth(), 0, PRIMARY));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        hdr.setBorder(new EmptyBorder(14, 24, 12, 24));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);

        JLabel title = new JLabel("Bảng điều khiển Chữ ký Schnorr");
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Mô phỏng quy trình mật mã học chính xác cho thuật toán chữ ký số Schnorr.");
        sub.setFont(FONT_SMALL);
        sub.setForeground(new Color(0xC7, 0xD9, 0xFF));

        left.add(title);
        left.add(sub);
        hdr.add(left, BorderLayout.CENTER);

        btnReset = new JButton("XÓA TẤT CẢ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()
                        ? new Color(255, 255, 255, 55)
                        : getModel().isRollover()
                          ? new Color(255, 255, 255, 38)
                          : new Color(255, 255, 255, 15);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnReset.setOpaque(false);
        btnReset.setContentAreaFilled(false);
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(FONT_BOLD);
        btnReset.setBorder(new EmptyBorder(6, 16, 6, 16));
        btnReset.setFocusPainted(false);
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hdr.add(btnReset, BorderLayout.EAST);

        return hdr;
    }

    private JButton createInlineTextButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                Color bg = Color.WHITE;
                if (getModel().isPressed()) {
                    bg = new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 30);
                } else if (getModel().isRollover()) {
                    bg = new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 12);
                }
                g2.setColor(bg);
                g2.fillRoundRect(2, 4, w - 4, h - 8, 6, 6);
                Color borderColor = getModel().isRollover() || getModel().isPressed() ? PRIMARY : BORDER_MD;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(2, 4, w - 5, h - 9, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(0, 12, 0, 12));
        btn.setFocusPainted(false);
        btn.setForeground(PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JScrollPane buildScrollContent() {
        JPanel leftCol  = column(buildCard01(), buildCard03());
        JPanel rightCol = column(buildCard02(), buildCard04());

        JPanel grid = new JPanel(new GridLayout(1, 2, 16, 0));
        grid.setOpaque(false);
        grid.add(leftCol);
        grid.add(rightCol);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_APP);
        wrap.setBorder(new EmptyBorder(16, 20, 20, 20));
        wrap.add(grid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrap);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_APP);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel column(JPanel top, JPanel bottom) {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        col.add(top, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        col.add(bottom, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        col.add(Box.createGlue(), gbc);

        return col;
    }

    // 🕵️ HÀM BỔ TRỢ: Khóa click, khóa focus tuyệt đối cho ô kết quả
    private void lockField(JTextField tf) {
        tf.setEditable(false);
        tf.setFocusable(false);
        tf.setRequestFocusEnabled(false);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARD 01 — BƯỚC 01: Tham số Hệ thống
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildCard01() {
        StepCard card = new StepCard("BƯỚC 01", "Tham số Hệ thống", null);
        JPanel body = card.getBody();

        tfP = textField();
        tfQ = textField();
        body.add(rowOf(2,
                new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>SỐ NGUYÊN TỐ P</span></html>", tfP),
                new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>THỪA SỐ Q (P - 1)</span></html>", tfQ)));
        body.add(gap(8));

        tfG = textField();
        body.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>PHẦN TỬ SINH G</span></html>", tfG));
        body.add(gap(8));

        cbHash = comboBox("SHA-256", "SHA-512");
        body.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>HÀM BĂM H</span></html>", cbHash));
        body.add(gap(12));

        JPanel bottomRow = new JPanel(new BorderLayout(10, 0));
        bottomRow.setOpaque(false);
        lblParamStatus = label("<html><span style='color:#656d76; font-size:11px;'>Điều kiện: g<sup>q</sup> ≡ 1 (mod p)</span></html>", FONT_SMALL, TXT_DIM);
        lblParamStatus.setHorizontalAlignment(SwingConstants.LEFT);

        btnCheckParams = outlineButton("KIỂM TRA THAM SỐ", PRIMARY);
        bottomRow.add(lblParamStatus, BorderLayout.CENTER);
        bottomRow.add(btnCheckParams, BorderLayout.EAST);
        body.add(bottomRow);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARD 02 — BƯỚC 02: Phát sinh Khóa
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildCard02() {
        StepCard card = new StepCard("BƯỚC 02", "Phát sinh Khóa", null);
        JPanel body = card.getBody();

        tfX           = textField();
        btnGenPrivKey = createInlineTextButton("NGẪU NHIÊN");
        body.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>KHÓA BÍ MẬT (X)</span>&nbsp;&nbsp;<span style='font-size:9px; color:#656d76;'>[1 ≤ x ≤ q - 1]</span></html>",
                FieldPanel.withIcon(tfX, btnGenPrivKey)));
        body.add(gap(10));

        btnCalcPubKey = primaryButton("TÍNH TOÁN KHÓA CÔNG KHAI");
        body.add(fullWidth(btnCalcPubKey));
        body.add(gap(10));

        // Khởi tạo và Khóa tuyệt đối các ô kết quả tự tính toán
        tfY        = readonlyField(SUCCESS);
        tfYFormula = readonlyField(TXT_DIM);
        lockField(tfY);
        lockField(tfYFormula);

        body.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>KHÓA CÔNG KHAI (Y)</span></html>", tfY));
        body.add(gap(6));
        body.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>CÔNG THỨC: y = g<sup>x</sup> mod p</span></html>", tfYFormula));

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARD 03 — BƯỚC 03: Ký thông điệp
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildCard03() {
        StepCard card = new StepCard("BƯỚC 03", "Ký thông điệp", null);
        JPanel body = card.getBody();

        tfMsg          = textField();
        btnLoadMsgSign = fileButton();
        body.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>THÔNG ĐIỆP (M)</span></html>",
                FieldPanel.withIcon(tfMsg, btnLoadMsgSign)));
        body.add(gap(8));

        tfK         = textField();
        btnGenNonce = createInlineTextButton("NGẪU NHIÊN");
        body.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>SỐ NGẪU NHIÊN (k)</span>&nbsp;&nbsp;<span style='font-size:9px; color:#656d76;'>[1 ≤ k ≤ q - 1]</span></html>",
                FieldPanel.withIcon(tfK, btnGenNonce)));
        body.add(gap(12));

        // Khởi tạo và Khóa các trường tự động tính toán cam kết r, e
        tfR = readonlyField(TXT_DARK);
        tfE = readonlyField(TXT_DARK);
        lockField(tfR);
        lockField(tfE);

        body.add(rowOf(2,
                new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>CAM KẾT r = g<sup>k</sup> mod p</span></html>", tfR),
                new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>BĂM e = H(r || M)</span></html>", tfE)));
        body.add(gap(12));

        btnSign = primaryButton("TẠO CHỮ KÝ SỐ");
        body.add(fullWidth(btnSign));
        body.add(gap(12));

        // Khởi tạo và Khóa các trường kết quả đầu ra của chữ ký
        tfSigE = readonlyField(PRIMARY);
        tfSigS = readonlyField(PRIMARY);
        lockField(tfSigE);
        lockField(tfSigS);

        JPanel sigBox = new JPanel(new GridLayout(2, 1, 0, 6));
        sigBox.setOpaque(false);
        sigBox.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>Thành phần chữ ký e</span></html>", tfSigE));
        sigBox.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>Thành phần chữ ký s</span> &nbsp;<span style='font-size:9px; font-weight:normal; color:#656d76;'>(s = k - x · e mod q)</span></html>", tfSigS));

        JLabel sigLabel = label("CHỮ KÝ ĐẦU RA (e, s) — s = (k - x · e) mod q", new Font("Segoe UI", Font.BOLD, 10), TXT_DIM);
        sigLabel.setHorizontalAlignment(SwingConstants.LEFT);
        sigLabel.setBorder(new EmptyBorder(4, 0, 4, 0));
        body.add(sigLabel);
        body.add(sigBox);
        body.add(gap(12));

        btnSaveSign = outlineButton("LƯU CHỮ KÝ (.SIG)", SUCCESS);
        body.add(fullWidth(btnSaveSign));

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARD 04 — BƯỚC 04: Xác minh Chữ ký
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildCard04() {
        StepCard card = new StepCard("BƯỚC 04", "Xác minh Chữ ký", null);
        JPanel body = card.getBody();

        tfVMsg           = textField();
        btnLoadMsgVerify = fileButton();
        body.add(new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>XÁC MINH THÔNG ĐIỆP</span></html>",
                FieldPanel.withIcon(tfVMsg, btnLoadMsgVerify)));
        body.add(gap(8));

        tfVE = textField();
        tfVS = textField();
        
        btnLoadSign = outlineButton("TẢI FILE CHỮ KÝ (.SIG)", PRIMARY);
        
        body.add(rowOf(2,
                new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>NHẬP e</span></html>", tfVE),
                new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>NHẬP s</span></html>", tfVS)));
        body.add(gap(8));
        body.add(fullWidth(btnLoadSign));
        body.add(gap(12));

        // Khởi tạo và Khóa các trường tính toán phục vụ đối sánh xác thực r', e'
        tfVR     = readonlyField(TXT_DARK);
        tfVECalc = readonlyField(TXT_DARK);
        lockField(tfVR);
        lockField(tfVECalc);

        body.add(rowOf(2,
                new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>Tính toán r' = g<sup>s</sup> · y<sup>e</sup> mod p</span></html>", tfVR),
                new FieldPanel("<html><span style='font-size:10px; font-weight:bold; color:#8c95a0;'>Tính toán e' = H(r' || M)</span></html>", tfVECalc)));
        body.add(gap(12));

        btnVerify = primaryButton("KIỂM TRA TÍNH HỢP LỆ");
        body.add(fullWidth(btnVerify));
        body.add(gap(12));

        pnlVerifyResult = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        pnlVerifyResult.setOpaque(false);
        pnlVerifyResult.setBackground(new Color(0xF0, 0xF4, 0xF9));
        pnlVerifyResult.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 2),
                new EmptyBorder(12, 0, 12, 0)));
        pnlVerifyResult.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        lblVerifyResult = label("<html>&mdash;&nbsp;&nbsp;CHƯA THỰC HIỆN KIỂM TRA KHỚP&nbsp;&nbsp;&mdash;</html>",
                new Font("Segoe UI", Font.BOLD, 12), TXT_DIM);
        lblVerifyResult.setHorizontalAlignment(SwingConstants.CENTER);
        pnlVerifyResult.add(lblVerifyResult);
        body.add(pnlVerifyResult);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONTROLLER INTERFACE — event registration
    // ══════════════════════════════════════════════════════════════════════════
    public void onCheckParams(ActionListener l)  { btnCheckParams.addActionListener(l); }
    public void onGenPrivKey(ActionListener l)    { btnGenPrivKey.addActionListener(l); }
    public void onCalcPubKey(ActionListener l)    { btnCalcPubKey.addActionListener(l); }
    public void onGenNonce(ActionListener l)      { btnGenNonce.addActionListener(l); }
    public void onSign(ActionListener l)          { btnSign.addActionListener(l); }
    public void onVerify(ActionListener l)        { btnVerify.addActionListener(l); }
    public void onLoadMsgSign(ActionListener l)   { btnLoadMsgSign.addActionListener(l); }
    public void onLoadMsgVerify(ActionListener l) { btnLoadMsgVerify.addActionListener(l); }
    public void onReset(ActionListener l)         { btnReset.addActionListener(l); }
    public void onSaveSign(ActionListener l)      { btnSaveSign.addActionListener(l); }
    public void onLoadSign(ActionListener l)      { btnLoadSign.addActionListener(l); }

    // ══════════════════════════════════════════════════════════════════════════
    // GETTERS / SETTERS
    // ══════════════════════════════════════════════════════════════════════════
    public String getP()             { return tfP.getText(); }
    public String getQ()             { return tfQ.getText(); }
    public String getG()             { return tfG.getText(); }
    public String getHashAlgorithm() { return (String) cbHash.getSelectedItem(); }
    public String getPrivateKey()    { return tfX.getText(); }
    public String getNonce()         { return tfK.getText(); }
    public String getSignMessage()   { return tfMsg.getText(); }
    public String getVerifyMessage() { return tfVMsg.getText(); }
    public String getVerifyE()       { return tfVE.getText(); }
    public String getVerifyS()       { return tfVS.getText(); }

    public void setPrivateKey(String x)  { tfX.setText(x); }
    public void setNonce(String k)       { tfK.setText(k); }
    public void setSignMessage(String m) { tfMsg.setText(m); }
    public void setVerifyMessage(String m) { tfVMsg.setText(m); }

    public void setPublicKey(String y, String formula) {
        tfY.setText(y);
        tfYFormula.setText(formula);
    }

    public void setSignResult(String r, String e, String sigE, String sigS) {
        tfR.setText(r);
        tfE.setText(e);
        tfSigE.setText(sigE);
        tfSigS.setText(sigS);
    }

    public void prefillVerify(String msg, String e, String s) {
        tfVMsg.setText(msg);
        tfVE.setText(e);
        tfVS.setText(s);
    }

    public void setVerifyResult(String rv, String ev, boolean ok) {
        tfVR.setText(rv);
        tfVECalc.setText(ev);
        if (ok) {
            pnlVerifyResult.setBackground(new Color(0xD1, 0xFA, 0xE5));
            pnlVerifyResult.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SUCCESS, 2),
                    new EmptyBorder(12, 0, 12, 0)));
            lblVerifyResult.setText("<html>✓&nbsp;&nbsp;&nbsp;<b>CHỮ KÝ HỢP LỆ</b> &nbsp;&mdash;&nbsp; Biểu thức xác thực khớp đúng (e' = e)</html>");
            lblVerifyResult.setForeground(SUCCESS);
        } else {
            pnlVerifyResult.setBackground(new Color(0xFF, 0xE8, 0xE8));
            pnlVerifyResult.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER, 2),
                    new EmptyBorder(12, 0, 12, 0)));
            lblVerifyResult.setText("<html>✗&nbsp;&nbsp;&nbsp;<b>CHỮ KÝ KHÔNG HỢP LỆ</b> &nbsp;&mdash;&nbsp; Giá trị băm sai lệch (e' ≠ e)</html>");
            lblVerifyResult.setForeground(DANGER);
        }
        pnlVerifyResult.repaint();
    }

    public void showParamStatus(boolean ok) {
        if (ok) {
            lblParamStatus.setText("<html>✓&nbsp;&nbsp;<font color='#2da44e'><b>Tham số HỢP LỆ</b></font></html>");
            lblParamStatus.setForeground(SUCCESS);
        } else {
            lblParamStatus.setText("<html>✗&nbsp;&nbsp;<font color='#cf222e'><b>Tham số KHÔNG HỢP LỆ</b></font></html>");
            lblParamStatus.setForeground(DANGER);
        }
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public File pickDocumentFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Documents (*.txt, *.pdf, *.docx)", "txt", "pdf", "docx"));
        return fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile() : null;
    }

    public File pickSignatureFileToSave() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu file chữ ký");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Signature Files (*.sig)", "sig"));
        return fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile() : null;
    }
    
    public File pickSignatureFileToLoad() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file chữ ký");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Signature Files (*.sig)", "sig"));
        return fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile() : null;
    }

    public void resetAll() {
        for (JTextField tf : new JTextField[]{
                tfP, tfQ, tfG, tfX, tfY, tfYFormula,
                tfMsg, tfK, tfR, tfE, tfSigE, tfSigS,
                tfVMsg, tfVE, tfVS, tfVR, tfVECalc
        }) tf.setText("");

        cbHash.setSelectedIndex(0);

        lblParamStatus.setText("<html><span style='color:#656d76; font-size:11px;'>Điều kiện: g<sup>q</sup> ≡ 1 (mod p)</span></html>");
        lblParamStatus.setForeground(TXT_DIM);

        lblVerifyResult.setText("<html>&mdash;&nbsp;&nbsp;CHƯA THỰC HIỆN KIỂM TRA KHỚP&nbsp;&nbsp;&mdash;</html>");
        lblVerifyResult.setForeground(TXT_DIM);

        pnlVerifyResult.setBackground(new Color(0xF0, 0xF4, 0xF9));
        pnlVerifyResult.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 2),
                new EmptyBorder(12, 0, 12, 0)));
        pnlVerifyResult.repaint();
    }
}