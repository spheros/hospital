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
public class SearchDialog extends ActionDialog  {

    public SearchDialog(final JFrame parent) {
        super(parent);

        int tabIndex = ((MainWindow) parent).getPane().getSelectedIndex();
        String tabTitle = ((MainWindow) parent).getPane().getTitleAt(tabIndex);
        tabTitle = tabTitle.substring(0, tabTitle.length() - 1);
        setTitle("Find " + tabTitle);

        switch (tabIndex) {
            case 0:
            case 1:
                add(new SearchPanel(this));
                break;
            case 2:
                add(new SearchRegPanel(this));
        }

        pack();
        setVisible(true);
    }
}
