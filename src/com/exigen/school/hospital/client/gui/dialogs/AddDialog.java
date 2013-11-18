package com.exigen.school.hospital.client.gui.dialogs;

import com.exigen.school.hospital.client.gui.MainWindow;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 05.11.13
 */
public class AddDialog extends ActionDialog  {

    public AddDialog(final JFrame parent) {
        super(parent);
        final AddPanel addPanel = new AddPanel(this);
        getContentPane().add(addPanel);
        int tabIndex = ((MainWindow) parent).getPane().getSelectedIndex();
        String tabTitle = ((MainWindow) parent).getPane().getTitleAt(tabIndex);
        tabTitle = tabTitle.substring(0, tabTitle.length() - 1);
        setTitle("Add / Edit " + tabTitle.toLowerCase());

        pack();
        setVisible(true);
    }



}
