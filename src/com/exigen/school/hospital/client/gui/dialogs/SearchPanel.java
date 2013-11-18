package com.exigen.school.hospital.client.gui.dialogs;

import com.exigen.school.hospital.client.gui.GuiConfig;
import com.exigen.school.hospital.client.gui.MainWindow;
import com.exigen.school.hospital.client.gui.TableAdapter;
import com.exigen.school.hospital.client.gui.TablePanel;
import com.exigen.school.hospital.client.gui.exceptions.ServerResponseException;
import com.exigen.school.hospital.client.network.ClientMessageTypes;
import com.exigen.school.hospital.client.network.NetworkConfig;
import com.exigen.school.hospital.server.ServerConfig;
import com.exigen.school.hospital.server.storage.jdbc.JdbcConfig;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
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
public class SearchPanel extends JPanel implements JdbcConfig, NetworkConfig,
        GuiConfig, ActionListener {
    final Logger logger;
    final SearchDialog parentDialog;
    final JFrame parentWindow;
    final JPanel instance;
    final SearchOptions searchOptions;
    final String tableName;
    final Component contextTabbedItem;

    public SearchPanel(final SearchDialog parentDialog) {
        logger = Logger.getLogger(this.getClass().getName());
        logger.setLevel(NetworkConfig.LOG_LEVEL);
        this.parentDialog = parentDialog;
        parentWindow = parentDialog.getParentWindow();
        instance = this;
        contextTabbedItem = getContextTabbedItem();

        JButton findButton = new JButton(FIND_ACTION_TITLE);
        findButton.setActionCommand(FIND_ACTION_TITLE);
        JButton cancelButton = new JButton(CANCEL_ACTION_TITLE);
        cancelButton.setActionCommand(CANCEL_ACTION_TITLE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(findButton);
        buttonPanel.add(cancelButton);

        findButton.addActionListener(this);
        cancelButton.addActionListener(this);

        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put(ROOM_FIELD_NAME, 3);
        params.put(SECTOR_FIELD_NAME, 3);

        String[] searchableFields = {};
        String contextTitle = getContextTabbedItemTitle();
        tableName = contextTitle.toUpperCase();
        if (contextTitle.equals(DOCTORS_TAB_NAME)) {
            searchableFields = SEARCHABLE_DOCTOR_FIELDS;
        } else if (contextTitle.equals(PATIENTS_TAB_NAME)) {
            searchableFields = SEARCHABLE_PATIENT_FIELDS;
        }

        searchOptions = new SearchOptions(10, params, searchableFields);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(LEADING)
                .addComponent(searchOptions)
                .addComponent(buttonPanel));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(searchOptions)
                .addComponent(buttonPanel));


    }

    private Component getContextTabbedItem() {
        JTabbedPane pane = ( (MainWindow) parentWindow).getPane();
        return pane.getComponentAt(pane.getSelectedIndex());
    }

    private String getContextTabbedItemTitle() {
        JTabbedPane pane = ( (MainWindow) parentWindow).getPane();
        return pane.getTitleAt(pane.getSelectedIndex());
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals(CANCEL_ACTION_TITLE)) {
            parentDialog.dispose();
            return;
        }

        TablePanel tPanel = (TablePanel) contextTabbedItem;
        TableAdapter adapter = tPanel.getAdapter();
        Map<String, String> params = searchOptions.getOptionsResult();

        if (params == null) {
            params = new HashMap<String, String>();
            params.put(GET_ALL_FIELDS_PARAM, GET_ALL_FIELDS_PARAM);
        }

        params.put(TABLE_NAME_PARAM, tableName);

        try {
            adapter.executeQuery(ClientMessageTypes.GET, params);
            tPanel.update();
            parentDialog.dispose();
            ((MainWindow) parentWindow).getStatusLabel().setText("  Online");

        } catch (ServerResponseException sre) {
            logger.log(Level.SEVERE, sre.getMessage());
        } catch (IOException e1) {
            if (e1.getMessage().equals(CONNECTION_REFUSED)) {
                JOptionPane.showMessageDialog(this, CONNECTION_FAILED_MESSAGE);
            } else {
                logger.log(NetworkConfig.LOG_LEVEL, e1.getMessage());
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }


}
