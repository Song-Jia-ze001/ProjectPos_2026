package pos.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import pos.domain.ProductDescription;
import pos.domain.Register;
import pos.domain.ReturnLineItem;
import pos.domain.ReturnTransaction;
import pos.domain.Sale;
import pos.domain.SalesLineItem;

/** 收银界面：只负责展示和收集输入，业务操作委托给 Register。 */
public class POSFrame extends JFrame {
    private static final Color GREEN = new Color(27, 94, 32);
    private static final Color DARK = new Color(33, 33, 33);
    private static final Color GOLD = new Color(255, 213, 79);

    private final Register register;
    private final DefaultTableModel saleModel;
    private final DefaultTableModel returnModel;
    private final JLabel saleTotalLabel = amountLabel();
    private final JLabel returnTotalLabel = amountLabel();
    private final JTextField itemIdField = new JTextField();
    private final JTextField qtyField = new JTextField("1");
    private final JTextField cashField = new JTextField();
    private final JTextField returnItemField = new JTextField();
    private final JTextField returnQtyField = new JTextField("1");
    private final JTextArea receiptArea = new JTextArea();
    private final JTabbedPane tabs = new JTabbedPane();
    private final JLabel modeLabel = new JLabel("PROCESS SALE");

    public POSFrame(Register register) {
        super("MiniMart POS  |  ProjectPos_2026");
        this.register = register;
        this.saleModel = tableModel();
        this.returnModel = tableModel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        tabs.addTab("Sale", buildSalePanel());
        tabs.addTab("Return", buildReturnPanel());
        tabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                if (tabs.getSelectedIndex() == 0) {
                    showSale();
                } else {
                    showReturn();
                }
            }
        });
        add(tabs, BorderLayout.CENTER);
        add(buildReceiptPanel(), BorderLayout.EAST);
        register.makeNewSale();
        refreshCatalogButtons();
        setSize(1120, 740);
        setLocation(16, 8);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(GREEN);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("MiniMart Point of Sale");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        modeLabel.setForeground(GOLD);
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(modeLabel);
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton saleBtn = headerButton("Sale");
        JButton returnBtn = headerButton("Return");
        saleBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSale();
            }
        });
        returnBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showReturn();
            }
        });
        buttons.add(saleBtn);
        buttons.add(returnBtn);
        header.add(left, BorderLayout.WEST);
        header.add(buttons, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSalePanel() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(245, 245, 245));

        JPanel entry = new JPanel(new GridLayout(1, 0, 8, 0));
        entry.setOpaque(false);
        entry.add(labeled("Item ID", itemIdField));
        entry.add(labeled("Qty", qtyField));
        JButton add = actionButton("Enter Item", GREEN);
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addSaleItem();
            }
        });
        entry.add(wrap(add));
        JButton newSale = actionButton("New Sale", new Color(69, 90, 100));
        newSale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                register.makeNewSale();
                saleModel.setRowCount(0);
                saleTotalLabel.setText("0.00");
                cashField.setText("");
                receiptArea.setText("");
            }
        });
        entry.add(wrap(newSale));

        JTable table = new JTable(saleModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton payBtn = actionButton("Make Payment", new Color(230, 81, 0));
        payBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paySale();
            }
        });
        JPanel pay = footerBar("TOTAL", saleTotalLabel, cashField, payBtn);

        JPanel catalog = buildCatalogPanel(true);
        root = stacked(root, entry, new JScrollPane(table), catalog, pay);
        return root;
    }

    private JPanel buildReturnPanel() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(255, 243, 224));

        JPanel entry = new JPanel(new GridLayout(1, 0, 8, 0));
        entry.setOpaque(false);
        entry.add(labeled("Item ID", returnItemField));
        entry.add(labeled("Qty", returnQtyField));
        JButton add = actionButton("Enter Return Item", new Color(191, 54, 12));
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addReturnItem();
            }
        });
        entry.add(wrap(add));
        JButton newR = actionButton("New Return", new Color(69, 90, 100));
        newR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                register.makeNewReturn();
                returnModel.setRowCount(0);
                returnTotalLabel.setText("0.00");
                receiptArea.setText("");
            }
        });
        entry.add(wrap(newR));

        JTable table = new JTable(returnModel);
        table.setRowHeight(26);

        JButton done = actionButton("Complete Return", new Color(191, 54, 12));
        done.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                completeReturn();
            }
        });
        JPanel refund = footerBar("REFUND", returnTotalLabel, null, done);

        JPanel catalog = buildCatalogPanel(false);
        root = stacked(root, entry, new JScrollPane(table), catalog, refund);
        return root;
    }

    private JPanel buildCatalogPanel(final boolean forSale) {
        JPanel panel = new JPanel(new GridLayout(0, 4, 6, 6));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Product Catalog  (click to add)"));
        panel.setBackground(new Color(250, 250, 250));
        panel.setOpaque(true);
        for (final ProductDescription p : register.getCatalog().getAll()) {
            JButton b = new JButton("<html><center><b>" + p.getName() + "</b><br/>"
                    + "<span style='color:#666'>" + p.getItemId() + " &nbsp; $" + p.getPrice()
                    + "</span></center></html>");
            b.setBackground(Color.WHITE);
            b.setFocusPainted(false);
            b.setPreferredSize(new Dimension(140, 42));
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (forSale) {
                        itemIdField.setText(p.getItemId());
                        qtyField.setText("1");
                        addSaleItem();
                    } else {
                        returnItemField.setText(p.getItemId());
                        returnQtyField.setText("1");
                        addReturnItem();
                    }
                }
            });
            panel.add(b);
        }
        return panel;
    }

    private JPanel buildReceiptPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(280, 600));
        panel.setBorder(BorderFactory.createTitledBorder("Receipt"));
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        return panel;
    }

    private void showSale() {
        modeLabel.setText("PROCESS SALE");
        if (tabs.getSelectedIndex() != 0) {
            tabs.setSelectedIndex(0);
        }
        if (register.getCurrentSale() == null) {
            register.makeNewSale();
        }
    }

    private void showReturn() {
        modeLabel.setText("HANDLE RETURNS");
        if (tabs.getSelectedIndex() != 1) {
            tabs.setSelectedIndex(1);
        }
        if (register.getCurrentReturn() == null) {
            register.makeNewReturn();
        }
    }

    private void addSaleItem() {
        try {
            String id = itemIdField.getText().trim();
            int qty = Integer.parseInt(qtyField.getText().trim());
            SalesLineItem item = register.enterItem(id, qty);
            saleModel.addRow(new Object[] {
                    item.getProduct().getItemId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    item.getSubtotal()
            });
            saleTotalLabel.setText(register.getCurrentSale().getTotal().toPlainString());
            itemIdField.setText("");
            qtyField.setText("1");
            itemIdField.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Enter Item", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void paySale() {
        try {
            register.endSale();
            BigDecimal cash = new BigDecimal(cashField.getText().trim());
            register.makePayment(cash);
            receiptArea.setText(register.lastReceipt());
            saleTotalLabel.setText(register.getCurrentSale().getTotal().toPlainString());
            JOptionPane.showMessageDialog(this,
                    "Change due: " + register.getCurrentSale().getBalance().toPlainString(),
                    "Payment complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Payment", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addReturnItem() {
        try {
            String id = returnItemField.getText().trim();
            int qty = Integer.parseInt(returnQtyField.getText().trim());
            ReturnLineItem item = register.enterReturnItem(id, qty);
            returnModel.addRow(new Object[] {
                    item.getProduct().getItemId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    item.getSubtotal()
            });
            returnTotalLabel.setText(register.getCurrentReturn().getTotal().toPlainString());
            returnItemField.setText("");
            returnQtyField.setText("1");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Return Item", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void completeReturn() {
        try {
            register.completeReturn();
            receiptArea.setText(register.lastReceipt());
            JOptionPane.showMessageDialog(this,
                    "Refund: " + register.getCurrentReturn().getRefundAmount().toPlainString(),
                    "Return complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Return", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void captureDemoScreenshots() {
        try {
            File dir = new File("docs/screenshots");
            dir.mkdirs();
            register.makeNewSale();
            saleModel.setRowCount(0);
            capture(new File(dir, "01_new_sale.png"));

            register.enterItem("1001", 2);
            register.enterItem("1003", 1);
            register.enterItem("1007", 3);
            fillSaleTable();
            capture(new File(dir, "02_sale_items.png"));

            cashField.setText("40.00");
            register.endSale();
            register.makePayment(new BigDecimal("40.00"));
            receiptArea.setText(register.lastReceipt());
            capture(new File(dir, "03_sale_paid.png"));

            showReturn();
            register.makeNewReturn();
            returnModel.setRowCount(0);
            register.enterReturnItem("1001", 1);
            fillReturnTable();
            capture(new File(dir, "04_return_item.png"));

            register.completeReturn();
            receiptArea.setText(register.lastReceipt());
            capture(new File(dir, "05_return_done.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillSaleTable() {
        saleModel.setRowCount(0);
        Sale sale = register.getCurrentSale();
        for (SalesLineItem item : sale.getLineItems()) {
            saleModel.addRow(new Object[] {
                    item.getProduct().getItemId(), item.getProduct().getName(),
                    item.getQuantity(), item.getProduct().getPrice(), item.getSubtotal()
            });
        }
        saleTotalLabel.setText(sale.getTotal().toPlainString());
    }

    private void fillReturnTable() {
        returnModel.setRowCount(0);
        ReturnTransaction rt = register.getCurrentReturn();
        for (ReturnLineItem item : rt.getLineItems()) {
            returnModel.addRow(new Object[] {
                    item.getProduct().getItemId(), item.getProduct().getName(),
                    item.getQuantity(), item.getProduct().getPrice(), item.getSubtotal()
            });
        }
        returnTotalLabel.setText(rt.getTotal().toPlainString());
    }

    private void capture(File file) throws Exception {
        validate();
        doLayout();
        tabs.validate();
        BufferedImage img = new BufferedImage(Math.max(getWidth(), 1100),
                Math.max(getHeight(), 700), BufferedImage.TYPE_INT_RGB);
        paint(img.getGraphics());
        ImageIO.write(img, "png", file);
        System.out.println("Wrote " + file.getAbsolutePath());
    }

    private void refreshCatalogButtons() {
        // 目录按钮在构造界面时已按当前商品目录生成
    }

    private static DefaultTableModel tableModel() {
        Vector<String> cols = new Vector<String>();
        cols.add("Item ID");
        cols.add("Name");
        cols.add("Qty");
        cols.add("Price");
        cols.add("Subtotal");
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private static JLabel amountLabel() {
        JLabel l = new JLabel("0.00");
        l.setFont(new Font("Consolas", Font.BOLD, 28));
        l.setForeground(new Color(27, 94, 32));
        l.setOpaque(true);
        l.setBackground(DARK);
        l.setForeground(GOLD);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        l.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
        return l;
    }

    private static JButton headerButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return b;
    }

    private static JButton actionButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBackground(bg);
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return b;
    }

    private static JPanel labeled(String title, JTextField field) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private static JPanel footerBar(String caption, JLabel amount, JTextField cash, JButton action) {
        JPanel pay = new JPanel();
        pay.setLayout(new BoxLayout(pay, BoxLayout.X_AXIS));
        pay.setOpaque(true);
        pay.setBackground(DARK);
        pay.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel totalCaption = new JLabel("  " + caption + "  ");
        totalCaption.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalCaption.setForeground(Color.WHITE);
        pay.add(totalCaption);
        pay.add(amount);
        if (cash != null) {
            pay.add(Box.createHorizontalStrut(16));
            JLabel cashLbl = new JLabel("Cash  ");
            cashLbl.setForeground(Color.WHITE);
            cashLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cash.setFont(new Font("Segoe UI", Font.BOLD, 20));
            cash.setMaximumSize(new Dimension(160, 36));
            cash.setPreferredSize(new Dimension(140, 36));
            pay.add(cashLbl);
            pay.add(cash);
        }
        pay.add(Box.createHorizontalStrut(12));
        pay.add(Box.createHorizontalGlue());
        pay.add(action);
        return pay;
    }

    private static JPanel stacked(JPanel root, Component entry, Component table,
            Component catalog, Component footer) {
        root.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.weighty = 0;
        root.add(entry, c);
        c.gridy = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        root.add(table, c);
        c.gridy = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 4, 2, 4);
        catalog.setPreferredSize(new Dimension(700, 148));
        root.add(catalog, c);
        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        footer.setPreferredSize(new Dimension(700, 58));
        root.add(footer, c);
        return root;
    }

    private static JPanel wrap(Component c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(new JLabel(" "), BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }
}
