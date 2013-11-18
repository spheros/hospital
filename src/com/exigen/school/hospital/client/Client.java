package com.exigen.school.hospital.client;

import com.exigen.school.hospital.client.gui.MainWindow;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 28.10.13
 */
public class Client {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                try {
                    new MainWindow("Masha");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
