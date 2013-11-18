package com.exigen.school.hospital.client.gui.dialogs;

import com.exigen.school.hospital.client.gui.MainWindow;
import com.exigen.school.hospital.client.gui.TableAdapter;
import com.exigen.school.hospital.client.gui.TablePanel;
import com.exigen.school.hospital.client.gui.exceptions.ServerResponseException;
import com.exigen.school.hospital.client.network.ClientMessageTypes;
import com.exigen.school.hospital.client.network.NetworkConfig;
import com.exigen.school.hospital.server.storage.jdbc.JdbcConfig;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 11.11.13
 */
public class SearchRegPanel extends JPanel implements ActionListener, NetworkConfig, JdbcConfig {
    final Logger logger;
    final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    final SearchDialog parentDialog;
    final JFrame parentWindow;
    final JRadioButton rbDoctor;
    final JRadioButton rbPatient;
    final JRadioButton rbAll;
    final DatePicker datePicker;

    public SearchRegPanel(final SearchDialog parentDialog) {
        logger = Logger.getLogger(this.getClass().getName());
        logger.setLevel(NetworkConfig.LOG_LEVEL);

        this.parentDialog = parentDialog;
        this.parentWindow = parentDialog.getParentWindow();
        rbDoctor = new JRadioButton("By selected doctor");
        rbPatient = new JRadioButton("By selected patient");
        rbAll = new JRadioButton("Watch all regcards");
        rbAll.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(rbDoctor);
        group.add(rbPatient);
        group.add(rbAll);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setBorder(new TitledBorder(""));

        GroupLayout optionsPanelLayout = new GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setAutoCreateGaps(true);
        optionsPanelLayout.setAutoCreateContainerGaps(true);

        optionsPanelLayout.setHorizontalGroup(
                optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(rbAll)
                        .addComponent(rbDoctor)
                        .addComponent(rbPatient)
        );
        optionsPanelLayout.linkSize(SwingConstants.HORIZONTAL, rbDoctor, rbPatient, rbAll);

        optionsPanelLayout.setVerticalGroup(
                optionsPanelLayout.createSequentialGroup()
                        .addComponent(rbAll)
                        .addComponent(rbDoctor)
                        .addComponent(rbPatient)

        );

        JLabel dateLabel = new JLabel("Select date:");
        datePicker = new DatePicker();
        try {
            datePicker.setDate(null);
        } catch (PropertyVetoException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        JPanel datePanel = new JPanel();

        GroupLayout datePanelLayout = new GroupLayout(datePanel);
        datePanel.setLayout(datePanelLayout);
        datePanelLayout.setAutoCreateGaps(true);
        datePanelLayout.setAutoCreateContainerGaps(true);
        datePanelLayout.setHorizontalGroup(
                datePanelLayout.createSequentialGroup()
                        .addComponent(dateLabel)
                        .addComponent(datePicker)
        );
        datePanelLayout.setVerticalGroup(
                datePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(dateLabel)
                        .addComponent(datePicker)
        );
        datePanel.setBorder(new TitledBorder(""));

        JButton findButton = new JButton("Find");
        findButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentDialog.dispose();
            }
        });
        JPanel controlPanel = new JPanel();
        GroupLayout controlPanelLayout = new GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setAutoCreateGaps(true);
        controlPanelLayout.setAutoCreateContainerGaps(true);
        controlPanelLayout.setHorizontalGroup(
                controlPanelLayout.createSequentialGroup()
                        .addComponent(findButton)
                        .addComponent(cancelButton)
        );
        controlPanelLayout.linkSize(SwingConstants.HORIZONTAL, findButton, cancelButton);
        controlPanelLayout.setVerticalGroup(
                controlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(findButton)
                        .addComponent(cancelButton)
        );

        BorderLayout mainPanelLayout = new BorderLayout(5, 5);
        setLayout(mainPanelLayout);
        add(optionsPanel, BorderLayout.NORTH);
        add(datePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);


    }

    private TableAdapter getAdapterAtIndex(int index) {
        JTabbedPane pane = ((MainWindow) parentWindow).getPane();
        return ((TablePanel) pane.getComponentAt(index)).getAdapter();
    }

    private JTable getTableAtIndex(int index) {
        JTabbedPane pane = ((MainWindow) parentWindow).getPane();
        return ((TablePanel) pane.getComponentAt(index)).getTable();
    }

    private TablePanel getTablePanelAtIndex(int index) {
        JTabbedPane pane = ((MainWindow) parentWindow).getPane();
        return ((TablePanel) pane.getComponentAt(index));
    }

    public void actionPerformed(ActionEvent e) {

        TablePanel tPanel = getTablePanelAtIndex(2);
        TableAdapter adapter = tPanel.getAdapter();

        ClientMessageTypes type = ClientMessageTypes.GET_REG;
        Map<String, String> params = new Hashtable<String, String>();

        Date date = datePicker.getDate();

        if (rbDoctor.isSelected()
                || rbPatient.isSelected()) {

            int tabIndex = (rbDoctor.isSelected()) ? 0 : 1;
            String rolename = (rbDoctor.isSelected()) ? "doctor" : "patient";
            String idAlias = (rbDoctor.isSelected()) ? DOCTOR_ID_ALIAS : PATIENT_ID_ALIAS;

            JTable table = getTableAtIndex(tabIndex);
            int selectedRow = table.getSelectedRow();
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this,
                        "You should select a " + rolename + "!");
                parentDialog.dispose();
                return;
            } else {
                String idValue =
                        String.valueOf(getTablePanelAtIndex(tabIndex).
                                getAdapter().getValueAt(selectedRow, 0));
                params.put(ID_FIELD_NAME, idAlias);
                params.put(idAlias, idValue);
            }
        }

        if (date != null) {
            params.put(REG_DATE_FIELD, dateFormatter.format(date));
        }

        try {
            adapter.executeQuery(ClientMessageTypes.GET_REG, params);
            tPanel.update();
            parentDialog.dispose();

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
