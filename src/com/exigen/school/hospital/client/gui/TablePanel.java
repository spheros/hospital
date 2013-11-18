package com.exigen.school.hospital.client.gui;

import com.exigen.school.hospital.client.gui.dialogs.AddDialog;
import com.exigen.school.hospital.client.gui.exceptions.ServerResponseException;
import com.exigen.school.hospital.client.network.ClientMessageTypes;
import com.exigen.school.hospital.server.storage.jdbc.JdbcConfig;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 30.10.13
 */
public class TablePanel extends JPanel implements JdbcConfig{

    final TableAdapter adapter;
    final JTabbedPane ctx;
    final JTable table = new JTable();
    final Checkbox cbEnableEdit = new Checkbox("Editable cells mode");
    boolean isActivated;

    public TablePanel(final TableAdapter adapter, final JTabbedPane ctx) {
        this.adapter = adapter;
        this.ctx = ctx;
        adapter.setContext(this);
        setLayout(new BorderLayout());
        setOpaque(true);
        isActivated = false;
        showTableWithControls();
    }

    public boolean isTablePanelActivated() {
        return isActivated;
    }

    public void showTableWithControls() {
        cbEnableEdit.setVisible(false);
        cbEnableEdit.addItemListener( new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                switch (e.getStateChange()) {
                    case ItemEvent.SELECTED:
                        adapter.setEditable(true);
                        break;
                    case ItemEvent.DESELECTED:
                        adapter.setEditable(false);
                          break;
                }
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.add(cbEnableEdit);
        add(controlPanel, BorderLayout.NORTH);

        table.setModel(adapter);
        table.setAutoCreateRowSorter(true);
        table.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JFrame mainWindow = (JFrame) getParentComponent(ctx);
                    new AddDialog(mainWindow);
                }
            }
        });

        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(700, 300));
        add(scrollpane, BorderLayout.CENTER);
    }

    Component getParentComponent(Component component) {
        component = component.getParent();
        return (component.getClass().equals(MainWindow.class)) ? component :
                getParentComponent(component);
    }


    public void update() {
        if (!adapter.getTableName().equals(REGCARDS_TABLE_NAME)) {
            cbEnableEdit.setVisible(true);
        }
        isActivated = true;
        table.removeColumn(table.getColumnModel().getColumn(0));
        ctx.repaint();
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public TableAdapter getAdapter() {
        return adapter;
    }

    public JTable getTable() {
        return table;
    }
}
