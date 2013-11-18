package com.exigen.school.hospital.client.gui.dialogs;

import com.exigen.school.hospital.client.gui.MainWindow;
import com.exigen.school.hospital.client.gui.TableAdapter;
import com.exigen.school.hospital.client.gui.TablePanel;
import com.exigen.school.hospital.client.gui.exceptions.ServerResponseException;
import com.exigen.school.hospital.client.network.ClientMessageTypes;
import com.exigen.school.hospital.client.network.NetworkConfig;
import com.exigen.school.hospital.server.storage.jdbc.JdbcConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.swing.GroupLayout.Alignment.LEADING;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 07.11.13
 */
public class AddPanel extends JPanel implements JdbcConfig, NetworkConfig, ActionListener {

    final Logger logger;
    final JFrame parentWindow;
    final JPanel instance;
    final TableAdapter adapter;
    AddOptions addOptions;
    String tableName;
    Component contextTabbedItem;

    JPanel buttonPanel;

    public AddPanel(final AddDialog parentDialog) {
        logger = Logger.getLogger(this.getClass().getName());
        logger.setLevel(NetworkConfig.LOG_LEVEL);
        parentWindow = parentDialog.getParentWindow();
        instance = this;

        contextTabbedItem = getContextTabbedItem();
        adapter = ( (TablePanel) contextTabbedItem).getAdapter();
        tableName = getContextTabbedItemTitle().toUpperCase();

        createButtons();
        createOptionsPanel();

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(addOptions)
                .addComponent(buttonPanel));

        layout.linkSize(SwingConstants.VERTICAL, addOptions, buttonPanel);

        layout.setVerticalGroup(layout.createParallelGroup(LEADING)
                .addComponent(addOptions)
                .addComponent(buttonPanel));

    }

    private void createButtons() {
        JButton addButton = new JButton("Add");
        addButton.setActionCommand("add");
        addButton.addActionListener(this);

        JButton updateButton = new JButton("Update");
        updateButton.setActionCommand("update");
        updateButton.addActionListener(this);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ( (AddDialog) findParentComponent(instance, AddDialog.class)).dispose();
            }
        });

        buttonPanel = new JPanel();
        GroupLayout layout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup(LEADING)
                .addComponent(addButton)
                .addComponent(updateButton)
                .addComponent(cancelButton));
        layout.linkSize(SwingConstants.HORIZONTAL, addButton, updateButton, cancelButton);
        layout.setVerticalGroup(layout.createSequentialGroup()
                    .addComponent(addButton)
                    .addComponent(updateButton)
                    .addComponent(cancelButton));
    }

    private void createOptionsPanel() {

        Map<String, Integer> paramSizes = new HashMap<String, Integer>();
        paramSizes.put(ROOM_FIELD_NAME, 3);
        paramSizes.put(SECTOR_FIELD_NAME, 3);

        JTable table = ( (TablePanel) contextTabbedItem).getTable();
        int selectedRow = table.getSelectedRow();
        List<String> paramValues = (selectedRow != -1) ? new ArrayList<String>() :
                null;
        if (paramValues != null) {
            for (int i = 0; i < adapter.getColumnCount(); i++) {
                String columnName = adapter.getColumnName(i);
                if (!columnName.equals(ID_FIELD_NAME)) {
                    paramValues.add("" + adapter.getValueAt(selectedRow, i));
                }
            }
        }

        String[] searchableFields = null;
        if (tableName.equals(DOCTORS_TABLE_NAME)) {
            searchableFields = SEARCHABLE_DOCTOR_FIELDS;
        } else if (tableName.equals(PATIENTS_TABLE_NAME)) {
            searchableFields = SEARCHABLE_PATIENT_FIELDS;
        }
        addOptions = new AddOptions(10, paramSizes, paramValues, searchableFields);

    }

    public AddOptions getAddOptions() {
        return addOptions;
    }

    private Component getContextTabbedItem() {
        JTabbedPane pane = ( (MainWindow) parentWindow).getPane();
        return pane.getComponentAt(pane.getSelectedIndex());
    }

    private String getContextTabbedItemTitle() {
        JTabbedPane pane = ( (MainWindow) parentWindow).getPane();
        return pane.getTitleAt(pane.getSelectedIndex());
    }

    private Component findParentComponent(Component component, Class<?> whatToFind) {
        component = component.getParent();
        return (component.getClass().equals(whatToFind)) ? component :
                findParentComponent(component, whatToFind);
    }

    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();
        JTable table = ( (TablePanel) contextTabbedItem).getTable();

        try {
            String rolename = tableName.toLowerCase()
                    .substring(0, tableName.length() - 1);
            String confirmMessage = "";
            String confirmTitle = "";
            String resultMsg = "";
            ClientMessageTypes type = null;

            Map<String, String> params = addOptions.getOptionsResult();
            params.put("tableName", tableName);

            if (command.equals("add")) {
                confirmMessage = "Add new " + rolename + " to database?";
                confirmTitle = "Add new " + rolename;
                resultMsg = "A new " + rolename + " was successfully added!";
                type = ClientMessageTypes.ADD;

            } else if (command.equals("update")) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }
                params.put(ID_FIELD_NAME, adapter.getSelectedRowId(table));
                String surname = (String) adapter.getValueAt(selectedRow, 1);
                String name = (String) adapter.getValueAt(selectedRow, 2);
                confirmMessage = "Update " + surname + " " + name + "?";
                confirmTitle = "Updating " + rolename;
                resultMsg = "The " + rolename + " was successfully updated!";
                type = ClientMessageTypes.UPDATE_ROW;
            }

            if (JOptionPane.showConfirmDialog(this,
                    confirmMessage,
                    confirmTitle, JOptionPane.YES_NO_OPTION) !=
                    JOptionPane.YES_OPTION) {
                return;
            }



            adapter.executeQuery(type, params);

            AddDialog parentDialog = (AddDialog)
                    findParentComponent(instance, AddDialog.class);
            parentDialog.dispose();

            adapter.update();
            table.removeColumn(table.getColumnModel().getColumn(0));

            JOptionPane.showMessageDialog(this, resultMsg);
            ((MainWindow) parentWindow).getStatusLabel().setText("  " + resultMsg);

        } catch (ServerResponseException sre) {
            logger.log(Level.SEVERE, sre.getMessage());
        } catch (IOException e1) {
            if (e1.getMessage().equals(CONNECTION_REFUSED)) {
                JOptionPane.showMessageDialog(this, CONNECTION_FAILED_MESSAGE);
            } else {
                logger.log(Level.SEVERE, e1.getMessage());
            }
        } catch (ClassNotFoundException e1) {
            logger.log(Level.SEVERE, e1.getMessage());
        }
    }

}
