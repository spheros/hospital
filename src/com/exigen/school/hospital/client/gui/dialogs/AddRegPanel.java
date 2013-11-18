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
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 11.11.13
 */
public class AddRegPanel extends JPanel implements JdbcConfig, NetworkConfig, ActionListener {
    final Logger logger;
    final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    final JFrame parentWindow;
    final JDialog parentDialog;
    final JPanel instance;
    final AddOptions doctorOptions, patientOptions;
    final DatePicker datePicker = new DatePicker();;
    final Map<String, String> idMap = new HashMap<String, String>();
    TableAdapter adapter;
    TablePanel tPanel;

    public AddRegPanel(final AddRegDialog parentDialog) {
        logger = Logger.getLogger(this.getClass().getName());
        logger.setLevel(NetworkConfig.LOG_LEVEL);
        this.parentDialog = parentDialog;
        parentWindow = parentDialog.getParentWindow();
        instance = this;

        doctorOptions = createOptions(0);
        doctorOptions.disableTextFields();
        doctorOptions.setBorderTitle("Doctor");
        patientOptions = createOptions(1);
        patientOptions.disableTextFields();
        patientOptions.setBorderTitle("Patient");

        setLayout(new BorderLayout());

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout( new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
        optionsPanel.add(patientOptions);
        optionsPanel.add(doctorOptions);

        add(optionsPanel, BorderLayout.CENTER);


        JPanel controlPanel = new JPanel();
        JLabel dateLabel = new JLabel("Select date");

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentDialog.dispose();
            }
        });

        GroupLayout layout = new GroupLayout(controlPanel);
        controlPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(LEADING)
                    .addComponent(dateLabel)
                    .addComponent(registerButton))
                .addGroup(layout.createParallelGroup(LEADING)
                    .addComponent((Component) datePicker)
                    .addComponent(cancelButton)));
        layout.linkSize(SwingConstants.VERTICAL, dateLabel, registerButton, cancelButton, datePicker);
        layout.linkSize(SwingConstants.HORIZONTAL, dateLabel, registerButton, cancelButton, datePicker);
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(dateLabel)
                    .addComponent((Component)datePicker))
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(registerButton)
                    .addComponent(cancelButton)));

        controlPanel.setBorder( new TitledBorder("Register") );
        add(controlPanel, BorderLayout.NORTH);
    }

    private AddOptions createOptions(int paneIndex) {

        JTable table = getTableAtIndex(paneIndex);
        adapter = getAdapterAtIndex(paneIndex);

        int selectedRow = table.getSelectedRow();

        Map<String, Integer> paramSizes = new HashMap<String, Integer>();
        paramSizes.put(ROOM_FIELD_NAME, 3);
        paramSizes.put(SECTOR_FIELD_NAME, 3);

        String tableName = adapter.getTableName().toUpperCase();
        List<String> paramValues = (selectedRow != -1) ? new ArrayList<String>() :null;
        if (paramValues != null) {
            for (int i = 0; i < adapter.getColumnCount(); i++) {
                String columnName = adapter.getColumnName(i);
                String value = "" + adapter.getValueAt(selectedRow, i);
                if (!columnName.equals(ID_FIELD_NAME)) {
                    paramValues.add(value);
                } else {
                    idMap.put(tableName, value);
                }
            }
        }

        String[] searchableFields = null;
        if (tableName.equals(DOCTORS_TABLE_NAME)) {
            searchableFields = SEARCHABLE_DOCTOR_FIELDS;
        } else if (tableName.equals(PATIENTS_TABLE_NAME)) {
            searchableFields = SEARCHABLE_PATIENT_FIELDS;
        }
        return new AddOptions(10, paramSizes, paramValues, searchableFields);
    }

    private TableAdapter getAdapterAtIndex(int index) {
        JTabbedPane pane = ( (MainWindow) parentWindow).getPane();
        return ( (TablePanel) pane.getComponentAt(index)).getAdapter();
    }

    private JTable getTableAtIndex(int index) {
        JTabbedPane pane = ( (MainWindow) parentWindow).getPane();
        return ( (TablePanel) pane.getComponentAt(index)).getTable();
    }

    private TablePanel getTablePanelAtIndex(int index) {
        JTabbedPane pane = ( (MainWindow) parentWindow).getPane();
        return ( (TablePanel) pane.getComponentAt(index));
    }

    public void actionPerformed(ActionEvent e) {

        Date today = new Date(System.currentTimeMillis());
        Date regDate = datePicker.getDate();
        if (regDate.compareTo(today) == -1) {
            JOptionPane.showMessageDialog(this, "You cannot register on the past date!\n" +
                    "Only future dates are allowed!");
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put(TABLE_NAME_PARAM, REGCARDS_TABLE_NAME);
        params.put(REG_DATE_FIELD, dateFormatter.format(regDate));
        params.put(DOCTOR_ID_FIELD, idMap.get(DOCTORS_TABLE_NAME));
        params.put(PATIENT_ID_FIELD, idMap.get(PATIENTS_TABLE_NAME));

        logger.log(Level.INFO, params.toString());

        try {
            TableAdapter regAdapter = getAdapterAtIndex(2);
            regAdapter.executeQuery(ClientMessageTypes.ADD, params);

            parentDialog.dispose();
            JOptionPane.showMessageDialog(this, "Successfully registered patient!");
        } catch (ServerResponseException sre) {
            if (sre.getErrorCode() == UNIQUE_KEY_CONSTRAINT_ERROR) {
                JOptionPane.showMessageDialog(this,
                        "You cannot register a patient to " +
                                "the same doctor on the same date twice!\n" +
                                "Select another date!");
            }

            if (sre.getMessage().equals(DOCTOR_COUNT_LIMIT_ERROR)) {
                JOptionPane.showMessageDialog(this, DOCTOR_COUNT_LIMIT_ERROR);
                parentDialog.dispose();
            }

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
