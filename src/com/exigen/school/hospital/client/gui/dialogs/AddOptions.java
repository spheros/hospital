package com.exigen.school.hospital.client.gui.dialogs;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
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
public class AddOptions extends JPanel {

    private static final int DEFAULT_FIELD_SIZE = 10;
    List<JLabel> labels = new ArrayList<JLabel>();
    List<JTextField> textFields = new ArrayList<JTextField>();
    final TitledBorder titledBorder;

    public AddOptions(String... args) {
        this(DEFAULT_FIELD_SIZE, null, args);
    }

    public AddOptions(Map<String, Integer> fieldSizes,
                      String... args) {
        this(DEFAULT_FIELD_SIZE, fieldSizes, args);
    }

    public AddOptions(int defaultFieldSize, Map<String, Integer> fieldSizes,
                      String... paramNames) {

        this(defaultFieldSize, fieldSizes, null, paramNames);
    }

    public AddOptions(int defaultFieldSize, Map<String, Integer> fieldSizes,
                      List<String> paramValues, String... paramNames) {

        for (int i = 0; i < paramNames.length; i++) {

            String str = paramNames[i];

            JLabel label = new JLabel(str + " :");
            label.setName("label" + str);

            Integer size = (fieldSizes != null && fieldSizes.containsKey(str)) ?
                    fieldSizes.get(str) : defaultFieldSize;


            final JTextField textField = new JTextField(size);
            textField.setName(str);
            textField.setEditable(true);


            if (paramValues != null) {
                textField.setText(paramValues.get(i));
            }

            labels.add(label);
            textFields.add(textField);

        }

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup parallelInHoriz1 =
                layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        GroupLayout.ParallelGroup parallelInHoriz2 =
                layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        GroupLayout.SequentialGroup sequentialInVert =
                layout.createSequentialGroup();

        for (int i = 0; i < labels.size(); i++) {
            parallelInHoriz1.addComponent(labels.get(i));
            parallelInHoriz2.addComponent(textFields.get(i));
            sequentialInVert.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labels.get(i)).addComponent(textFields.get(i)));
            layout.linkSize(SwingConstants.HORIZONTAL, textFields.get(i));
        }


        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(parallelInHoriz1)
                .addGroup(parallelInHoriz2));


        layout.setVerticalGroup(sequentialInVert);

        titledBorder = new TitledBorder("");
        setBorder( titledBorder );
    }

    public Map<String, String> getOptionsResult() {
        Map<String, String> optionsMap = new HashMap<String, String>();
        for (int i = 0; i < labels.size(); i++) {
            optionsMap.put(textFields.get(i).getName(), textFields.get(i).getText());
        }

        return (optionsMap.size() > 0) ? optionsMap : null;
    }

    public void setBorderTitle(String title) {
        titledBorder.setTitle(title);
    }

    public void disableTextFields() {
        for (JTextField field : textFields) {
            field.setEditable(false);
        }
    }

    public void clearAll() {
        for (JTextField field : textFields)  {
            field.setText("");
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Map<String, Integer> fieldSizes = new HashMap<String, Integer>();
        fieldSizes.put("Room", 6);
        frame.getContentPane().add(new AddOptions(3, fieldSizes, "Surname", "Name", "Room", "Speciality"));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
