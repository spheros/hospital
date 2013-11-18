package com.exigen.school.hospital.client.gui.dialogs;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 11.11.13
 */
public class ActionDialog extends JDialog {
    final JFrame parent;

    public ActionDialog(JFrame parent) {
        this.parent =  parent;
        setupLocation();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
    }

    private void setupLocation() {
        setLocationRelativeTo(null);
        int parentX = parent.getX();
        int parentY = parent.getY();

        int thisX = this.getX();
        int thisY = this.getY();

        int newX = (thisX - parentX) / 2 + parentX;
        int newY = (thisY - parentY) / 2 + parentY;
        setLocation(newX, newY);
    }

    public JFrame getParentWindow() {
        return parent;
    }
}
