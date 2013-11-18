package com.exigen.school.hospital.client.gui.dialogs;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 07.11.13
 */
public class SearchOptions extends JPanel {

    private static final int DEFAULT_FIELD_SIZE = 10;
    List<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
    List<JTextField> textFields = new ArrayList<JTextField>();


    public SearchOptions(String... args) {
        this(DEFAULT_FIELD_SIZE, null, args);
    }

    public SearchOptions(Map<String, Integer> fieldSizes,
                         String... args) {
        this(DEFAULT_FIELD_SIZE, fieldSizes, args);
    }

    public SearchOptions(int defaultFieldSize, Map<String, Integer> fieldSizes,
                         String... args) {


        for (String str : args) {
            JCheckBox cb = new JCheckBox(str);
            cb.setName("cb" + str);


            Integer size = (fieldSizes != null && fieldSizes.containsKey(str)) ?
                    fieldSizes.get(str) : defaultFieldSize;


            final JTextField textField = new JTextField(size);
            textField.setName(str);
            textField.setEditable(false);

            cb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    textField.setEditable(((JCheckBox) e.getSource()).isSelected());
                }
            });

            checkBoxes.add(cb);
            textFields.add(textField);


        }

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup parallelInHoriz1 =
                layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.ParallelGroup parallelInHoriz2 =
                layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        GroupLayout.SequentialGroup sequentialInVert =
                layout.createSequentialGroup();

        for (int i = 0; i < checkBoxes.size(); i++) {
            parallelInHoriz1.addComponent(checkBoxes.get(i));
            parallelInHoriz2.addComponent(textFields.get(i));
            sequentialInVert.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxes.get(i)).addComponent(textFields.get(i)));
            layout.linkSize(SwingConstants.HORIZONTAL, textFields.get(i));
        }


        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(parallelInHoriz1)
                .addGroup(parallelInHoriz2));


        layout.setVerticalGroup(sequentialInVert);

        setBorder( new EtchedBorder() );
    }

    public Map<String, String> getOptionsResult() {
        Map<String, String> optionsMap = new HashMap<String, String>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                optionsMap.put(textFields.get(i).getName(), textFields.get(i).getText());
            }
        }

        return (optionsMap.size() > 0) ? optionsMap : null;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Map<String, Integer> fieldSizes = new HashMap<String, Integer>();
        fieldSizes.put("Room", 6);
        frame.getContentPane().add(new SearchOptions(3, fieldSizes, "Surname", "Name", "Room", "Speciality"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
