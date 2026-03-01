package DbConnection;
import java.awt.Component;
import java.awt.Container;
import javax.swing.*;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Admin
 */
public class ClearFields {
    public static void clear(Container container){
        for (Component c : container.getComponents()){
            if (c instanceof JTextField){
                ((JTextField) c).setText("");
            }else if(c instanceof JPasswordField){
                ((JPasswordField) c).setText("");
            }else if(c instanceof JTextArea){
                ((JTextArea) c).setText("");
            }else if(c instanceof JComboBox){
                JComboBox<?> combo = (JComboBox<?>) c;
                if (combo.getItemCount() > 0)
                    combo.setSelectedIndex(0);
            }else if (c instanceof Container){
                clear((Container) c);
            }
        }
    }
}
