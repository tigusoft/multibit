package org.multibit.viewsystem.swing.view;

import org.multibit.controller.MultiBitController;
import org.multibit.model.Data;
import org.multibit.model.DataProvider;
import org.multibit.viewsystem.View;
import org.multibit.viewsystem.swing.MultiBitFrame;
import org.multibit.viewsystem.swing.WalletTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ShowTransactionsPanel extends JPanel implements DataProvider, View {

    private static final Logger log = LoggerFactory.getLogger(ShowTransactionsPanel.class);

    private static final long serialVersionUID = 1235108897887842662L;

    private MultiBitController controller;

    private JTable table;
    private WalletTableModel walletTableModel;

    private Data data;

    private static final String SPACER = "   "; // 3 spaces

    private static final String PROGRESS_0_ICON_FILE = "/images/progress0.png";
    private static final String PROGRESS_1_ICON_FILE = "/images/progress1.png";
    private static final String PROGRESS_2_ICON_FILE = "/images/progress2.png";
    private static final String PROGRESS_3_ICON_FILE = "/images/progress3.png";
    private static final String PROGRESS_4_ICON_FILE = "/images/progress4.png";
    private static final String PROGRESS_5_ICON_FILE = "/images/progress5.png";
    private static final String TICK_ICON_FILE = "/images/tick.png";

    public ShowTransactionsPanel(JFrame mainFrame, MultiBitController controller) {
        this.controller = controller;

        data = new Data();

        initUI();
    }

    private void initUI() {
        createWalletPanel();
    }

    private void createWalletPanel() {
        setBackground(MultiBitFrame.VERY_LIGHT_BACKGROUND_COLOR);
        setLayout(new GridBagLayout());
        setOpaque(true);
        GridBagConstraints constraints = new GridBagConstraints();

        walletTableModel = new WalletTableModel(controller);
        table = new JTable(walletTableModel);
        table.setOpaque(true);
        table.setBorder(BorderFactory.createEmptyBorder());

        // use status icons
        table.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());

        // date right justified
        table.getColumnModel().getColumn(1).setCellRenderer(new RightJustifiedDateRenderer());

        // center column headers
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        JLabel label = (JLabel) renderer;
        label.setHorizontalAlignment(JLabel.CENTER);

        // description left justified
        table.getColumnModel().getColumn(2).setCellRenderer(new LeftJustifiedRenderer());

        // credit and debit right justified
        table.getColumnModel().getColumn(3).setCellRenderer(new RightJustifiedRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new RightJustifiedRenderer());

        TableColumn tableColumn = table.getColumnModel().getColumn(0); // status
        tableColumn.setPreferredWidth(35);

        tableColumn = table.getColumnModel().getColumn(1); // date
        tableColumn.setPreferredWidth(85);

        tableColumn = table.getColumnModel().getColumn(2); // description
        tableColumn.setPreferredWidth(320);

        tableColumn = table.getColumnModel().getColumn(3); // debit
        tableColumn.setPreferredWidth(40);

        tableColumn = table.getColumnModel().getColumn(4); // credit
        tableColumn.setPreferredWidth(40);

        // sorter
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(sorter);

        // sort by date descending
        List<TableRowSorter.SortKey> sortKeys = new ArrayList<TableRowSorter.SortKey>();
        sortKeys.add(new TableRowSorter.SortKey(1, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        Comparator<Date> comparator = new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                long n1 = o1.getTime();
                long n2 = o2.getTime();
                if (n1 == 0) {
                    // object 1 has missing date
                    return 1;
                }
                if (n2 == 0) {
                    // object 2 has missing date
                    return -1;
                }
                if (n1 < n2) {
                    return -1;
                } else if (n1 > n2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        sorter.setComparator(1, comparator);

        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getViewport().setBackground(MultiBitFrame.BACKGROUND_COLOR);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;

        add(scrollPane, constraints);
    }

    public Data getData() {
        return data;
    }

    class ImageRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 154545L;

        JLabel label = new JLabel();

        ImageIcon tickIcon = MultiBitFrame.createImageIcon(TICK_ICON_FILE);
        ImageIcon progress0Icon = MultiBitFrame.createImageIcon(PROGRESS_0_ICON_FILE);
        ImageIcon progress1Icon = MultiBitFrame.createImageIcon(PROGRESS_1_ICON_FILE);
        ImageIcon progress2Icon = MultiBitFrame.createImageIcon(PROGRESS_2_ICON_FILE);
        ImageIcon progress3Icon = MultiBitFrame.createImageIcon(PROGRESS_3_ICON_FILE);
        ImageIcon progress4Icon = MultiBitFrame.createImageIcon(PROGRESS_4_ICON_FILE);
        ImageIcon progress5Icon = MultiBitFrame.createImageIcon(PROGRESS_5_ICON_FILE);

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            int numberOfBlocksEmbedded = (Integer) value;
            if (numberOfBlocksEmbedded < 0) {
                numberOfBlocksEmbedded = 0;
            }
            if (numberOfBlocksEmbedded > 6) {
                numberOfBlocksEmbedded = 6;
            }

            switch (numberOfBlocksEmbedded) {
            case 0: {
                label.setIcon(progress0Icon);
                label.setToolTipText(controller.getLocaliser().getString("multiBitFrame.status.notConfirmed"));
                break;
            }
            case 1: {
                label.setIcon(progress1Icon);
                label.setToolTipText(controller.getLocaliser().getString("multiBitFrame.status.beingConfirmed"));
                break;
            }
            case 2: {
                label.setIcon(progress2Icon);
                label.setToolTipText(controller.getLocaliser().getString("multiBitFrame.status.beingConfirmed"));
                break;
            }
            case 3: {
                label.setIcon(progress3Icon);
                label.setToolTipText(controller.getLocaliser().getString("multiBitFrame.status.beingConfirmed"));
                break;
            }
            case 4: {
                label.setIcon(progress4Icon);
                label.setToolTipText(controller.getLocaliser().getString("multiBitFrame.status.beingConfirmed"));
                break;
            }
            case 5: {
                label.setIcon(progress5Icon);
                label.setToolTipText(controller.getLocaliser().getString("multiBitFrame.status.beingConfirmed"));
                break;
            }
            case 6: {
                label.setIcon(tickIcon);
                label.setToolTipText(controller.getLocaliser().getString("multiBitFrame.status.isConfirmed"));
                break;
            }
            default:
                label.setIcon(progress0Icon);
                label.setToolTipText(controller.getLocaliser().getString("multiBitFrame.status.notConfirmed"));
            }
            
            if (!label.getBackground().equals(table.getSelectionBackground())) {
                Color backgroundColor = (row % 2 == 0 ? Color.WHITE : MultiBitFrame.BACKGROUND_COLOR);
                label.setBackground(backgroundColor);
            }
            return label;
        }
    }

    class RightJustifiedRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1549545L;

        JLabel label = new JLabel();

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setOpaque(true);

            label.setText(value + SPACER);
            if (!label.getBackground().equals(table.getSelectionBackground())) {
                Color backgroundColor = (row % 2 == 0 ? Color.WHITE : MultiBitFrame.BACKGROUND_COLOR);
                label.setBackground(backgroundColor);
            }
            return label;
        }
    }

    class RightJustifiedDateRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1549545L;

        JLabel label = new JLabel();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", controller.getLocaliser().getLocale());

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setOpaque(true);

            String formattedDate = "";
            if (value != null) {
                if (value instanceof Date) {
                    if (((Date) value).getTime() == 0) {
                        // date is actually missing - just keep a blank string
                    } else {
                        try {
                            formattedDate = dateFormatter.format(value);
                        } catch (IllegalArgumentException iae) {
                            // ok
                        }
                    }
                } else {
                    formattedDate = value.toString();
                }
            }

            label.setText(formattedDate + SPACER);

            if (!label.getBackground().equals(table.getSelectionBackground())) {
                Color backgroundColor = (row % 2 == 0 ? Color.WHITE : MultiBitFrame.BACKGROUND_COLOR);
                label.setBackground(backgroundColor);
            }
            return label;
        }
    }

    class LeftJustifiedRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1549545L;

        JLabel label = new JLabel();

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBackground(MultiBitFrame.BACKGROUND_COLOR);
            label.setOpaque(true);   

            label.setText((String) value);

            if (!label.getBackground().equals(table.getSelectionBackground())) {
                Color backgroundColor = (row % 2 == 0 ? Color.WHITE : MultiBitFrame.BACKGROUND_COLOR);
                label.setBackground(backgroundColor);
            }
            return label;
        }
    }

    @Override
    public void displayView() {
        walletTableModel.recreateWalletData();
        table.invalidate();
        table.validate();
        table.repaint();
    }
    
    @Override
    public void updateView() {
        walletTableModel.recreateWalletData();

        table.invalidate();
        table.validate();
        table.repaint();
    }

    public void navigateAwayFromView(int nextViewId, int relationshipOfNewViewToPrevious) {
    }

    public void displayMessage(String messageKey, Object[] messageData, String titleKey) {
    }

    public WalletTableModel getWalletTableModel() {
        return walletTableModel;
    }

    public JPanel getFormPanel() {
        return null;
    }

    public JTextField getLabelTextField() {
        return null;
    }
}