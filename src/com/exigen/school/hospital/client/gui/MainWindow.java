package com.exigen.school.hospital.client.gui;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 05.11.13
 */

import com.alee.laf.WebLookAndFeel;
import com.exigen.school.hospital.client.gui.dialogs.AddDialog;
import com.exigen.school.hospital.client.gui.dialogs.AddRegDialog;
import com.exigen.school.hospital.client.gui.dialogs.SearchDialog;
import com.exigen.school.hospital.client.gui.exceptions.ServerResponseException;
import com.exigen.school.hospital.server.storage.jdbc.JdbcConfig;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class MainWindow extends JFrame implements JdbcConfig, ActionListener, GuiConfig {

    protected JPanel mainPanel = new JPanel(new BorderLayout());
    protected JTabbedPane pane = new JTabbedPane();
    protected final JLabel statusLabel = new JLabel("  Offline");

    public MainWindow(String title) throws Exception {
        super(title);
        WebLookAndFeel.install();

        URL iconUrl = getClass().getResource("images/hospital.png");
        ImageIcon imageIcon = new ImageIcon(iconUrl);
        setIconImage(imageIcon.getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initMainPanel();
        add(mainPanel);
        setSize(MAIN_WINDOW_DIMENSION);
        setLocationRelativeTo(null);
        setVisible(true);
    }



    private void initMainPanel() {
        initPane();
        mainPanel.add(initToolBar(), BorderLayout.PAGE_START);
        mainPanel.add(pane, BorderLayout.CENTER);
        mainPanel.add(initStatusBar(), BorderLayout.SOUTH);

    }

    public JTabbedPane getPane() {
        return this.pane;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    private JToolBar initToolBar() {
        JToolBar toolBar = new JToolBar();

        addButtons(toolBar);
        return toolBar;
    }

    private void addButtons(JToolBar toolBar) {
        JButton button;


        button = makeNavigationButton("database_connect.png", "network",
                "Network configuration",
                "NetConfig");
        toolBar.add(button);
        toolBar.addSeparator();

        button = makeNavigationButton("Search.png", "search",
                "Search",
                "Search");
        toolBar.add(button);

        button = makeNavigationButton("adduser.png", "adduser",
                "Add doctor or patient",
                "Add role");
        toolBar.add(button);

        button = makeNavigationButton("register.png", "register",
                "Create regestration card",
                "Register");
        toolBar.add(button);

        button = makeNavigationButton("deleteuser.png", "delete",
                "Delete",
                "Delete");
        toolBar.add(button);

        button = makeNavigationButton("refresh.png", "refresh",
                "Refresh data",
                "Resfresh");
        toolBar.add(button);

        toolBar.addSeparator();


        button = makeNavigationButton("exit.png", "exit",
                "Close program",
                "Quit");
        toolBar.add(button);
    }

    protected JButton makeNavigationButton(String imageName,
                                           String actionCommand,
                                           String toolTipText,
                                           String altText) {
        String imgLocation = "images/" + imageName;
        URL imageURL = MainWindow.class.getResource(imgLocation);

        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        if (imageURL != null) {
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {
            button.setText(altText);
            System.err.println("Resource not found: "
                    + imgLocation);
        }

        return button;
    }

    private void initPane() {
        pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        pane.add(DOCTORS_TAB_NAME, new TablePanel(new TableAdapter(), pane));
        pane.add(PATIENTS_TAB_NAME, new TablePanel( new TableAdapter(), pane));
        pane.add(REGISTRY_TAB_NAME, new TablePanel( new TableAdapter(), pane));
    }

    private JPanel initStatusBar() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(getWidth(), 20));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        return statusPanel;
    }


    public void actionPerformed(ActionEvent e) {
        /*if (e.getActionCommand().equals("network")) {

        }*/

        if (e.getActionCommand().equals("search")) {
            new SearchDialog(this);
        }

        if (e.getActionCommand().equals("exit")) {
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure to quit?", "Exit program", JOptionPane.YES_NO_OPTION) ==
                    JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }

        int index = pane.getSelectedIndex();
        TablePanel tPanel = (TablePanel) pane.getComponentAt(index);
        if (!tPanel.isActivated) {
            return;
        }

        if (e.getActionCommand().equals("adduser")) {
            if (!pane.getTitleAt(index).equals(REGISTRY_TAB_NAME)) {
                new AddDialog(this);
            } else {
                new AddRegDialog(this);
            }

        }

        if (e.getActionCommand().equals("register")) {
            new AddRegDialog(this);
        }

        TableAdapter adapter = tPanel.getAdapter();
        JTable tableUpdated = tPanel.getTable();

        if (e.getActionCommand().equals("delete")) {
            int selectedRowIndex = tableUpdated.getSelectedRow();
            if (selectedRowIndex == -1 ) {
                return;
            }

            String tableName = adapter.getTableName();
            String rolename = tableName.toLowerCase().substring(0, tableName.length() - 1);
            String userMsg = "Delete this " + rolename + "?";
            if (JOptionPane.showConfirmDialog(this,
                    userMsg,
                    "Delete " + rolename, JOptionPane.YES_NO_OPTION) !=
                    JOptionPane.YES_OPTION) {
                return;
            }
            try {
                adapter.deleteRow(tableUpdated);
            } catch (ServerResponseException e1) {
                String msg = e1.getMessage();
                if (e1.getErrorCode() == FOREIGN_KEY_CONSTRAINT_ERROR) {
                    msg = "You cannot delete " + rolename + " if he is present in registry cards!\n" +
                            "Find and delete the apropriate registry cards first!";
                }
                JOptionPane.showMessageDialog(this, msg);
            }


        }

        if (e.getActionCommand().equals("refresh")) {
            adapter.update();
            tableUpdated.removeColumn(tableUpdated.getColumnModel().getColumn(0));
        }
    }
}

