package com.exigen.school.hospital.client.gui.dialogs;

import com.exigen.school.hospital.client.gui.MainWindow;
import com.exigen.school.hospital.client.gui.TablePanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 11.11.13
 */
public class AddRegDialog extends ActionDialog {
    public AddRegDialog(final JFrame frame) {
        super(frame);

        JTabbedPane pane = ( (MainWindow) getParentWindow()).getPane();
        int selectedDoctorIndex =
                ((TablePanel) pane.getComponentAt(0)).getTable().getSelectedRow();
        int selectedPatientIndex =
                ((TablePanel) pane.getComponentAt(1)).getTable().getSelectedRow();

        if (selectedDoctorIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor record in Doctors tab!");
            return;
        }

        if (selectedPatientIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient in Patients tab!");
            return;
        }

        add( new AddRegPanel(this));

        pack();
        setVisible(true);
    }
}
