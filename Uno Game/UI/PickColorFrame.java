package UI;

import java.awt.Font;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import Logic.UnoCard;

public class PickColorFrame extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PickColorFrame.class.getName());

    private UnoCard.Color wildColor = null;
    Boolean allow = false;
    PopUp popUp;

    public PickColorFrame() {
        super((java.awt.Frame) null, "Pick a Color", true);
        initComponents();
    }

    public PickColorFrame(PopUp pop) {
        super((java.awt.Frame) null, "Pick a Color", true);
        initComponents();
        popUp = pop;
    }

    public UnoCard.Color chooseColor(UnoCard card) {
        if (card.getColor() == UnoCard.Color.Wild) {
            this.setResizable(false);
            this.setBounds(100,150,600,700);
            setLocationRelativeTo(null);
            this.setVisible(true);
        }
        else {
            wildColor = card.getColor();
        }
        return wildColor;
    }

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        blueButton = new javax.swing.JButton();
        greenButton = new javax.swing.JButton();
        yellowButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semibold", Font.PLAIN, 36)); // NOI18N
        jLabel1.setText("Pick the Color of your Wild Card");

        jButton1.setFont(new java.awt.Font("Yu Gothic UI Semibold", Font.PLAIN, 18)); // NOI18N
        jButton1.setText("Red");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        blueButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", Font.PLAIN, 18)); // NOI18N
        blueButton.setText("Blue");
        blueButton.addActionListener(this::blueButtonActionPerformed);

        greenButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", Font.PLAIN, 18)); // NOI18N
        greenButton.setText("Green");
        greenButton.addActionListener(this::greenButtonActionPerformed);

        yellowButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", Font.PLAIN, 18)); // NOI18N
        yellowButton.setText("Yellow");
        yellowButton.addActionListener(this::yellowButtonActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(19, 19, 19)
                                                .addComponent(jLabel1))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(246, 246, 246)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(yellowButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(greenButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(blueButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jLabel1)
                                .addGap(38, 38, 38)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44)
                                .addComponent(blueButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(greenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42)
                                .addComponent(yellowButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(51, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        wildColor = UnoCard.Color.Red;
        JLabel message = new JLabel("The wild card color is red");
        message.setFont(new Font("Arial", Font.BOLD, 48));
        JOptionPane.showMessageDialog(null, message);
        allow = true;
        this.dispose();
        popUp.declaredColor = UnoCard.Color.Red;
        popUp.topCardButton.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/Cards/Small/" + popUp.game.getTopCardImage()))));
    }

    private void blueButtonActionPerformed(java.awt.event.ActionEvent evt) {
        wildColor = UnoCard.Color.Blue;
        JLabel message = new JLabel("The wild card color is blue");
        message.setFont(new Font("Arial", Font.BOLD, 48));
        JOptionPane.showMessageDialog(null, message);
        allow = true;
        this.dispose();
        popUp.declaredColor = UnoCard.Color.Blue;
        popUp.topCardButton.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/Cards/Small/" + popUp.game.getTopCardImage()))));
    }

    private void greenButtonActionPerformed(java.awt.event.ActionEvent evt) {
        wildColor = UnoCard.Color.Green;
        JLabel message = new JLabel("The wild card color is green");
        message.setFont(new Font("Arial", Font.BOLD, 48));
        JOptionPane.showMessageDialog(null, message);
        allow = true;
        this.dispose();
        popUp.declaredColor = UnoCard.Color.Green;
        popUp.topCardButton.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/Cards/Small/" + popUp.game.getTopCardImage()))));
    }

    private void yellowButtonActionPerformed(java.awt.event.ActionEvent evt) {
        wildColor = UnoCard.Color.Yellow;
        JLabel message = new JLabel("The wild card color is yellow");
        message.setFont(new Font("Arial", Font.BOLD, 48));
        JOptionPane.showMessageDialog(null, message);
        allow = true;
        this.dispose();
        popUp.declaredColor = UnoCard.Color.Yellow;
        popUp.topCardButton.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/Cards/Small/" + popUp.game.getTopCardImage()))));
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new PickColorFrame().setVisible(true));
    }

    // Variables declaration
    private javax.swing.JButton blueButton;
    private javax.swing.JButton greenButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton yellowButton;
}

