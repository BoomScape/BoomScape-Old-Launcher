package org.launch.frame;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Frame extends JFrame {
    @Getter
    private JProgressBar progressBar = new JProgressBar();
    @Getter
    private JLabel task = new JLabel(String.format("<html><div style=\"text-align:center;width:%dpx;\">%s</div></html>", 180, "Loading Launcher.."));
    public void setTask(String task) {
        this.task.setText(String.format("<html><div style=\"text-align:center;width:%dpx;\">%s</div></html>", 180, task));
        this.task.repaint();
    }
    private JLabel logo;
    public Frame() {
        setSize(250, 300);
        setDefaultLookAndFeelDecorated(true);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        validate();
        setIconImage(new ImageIcon(getClass().getResource("favicon.png")).getImage());
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked: " + e.getPoint().toString());
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        getContentPane().setBackground(new Color(0x262626));
        setForeground(Color.DARK_GRAY);

        logo = new JLabel();
        logo.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("logo.png")).getImage()));
        logo.setIconTextGap(0);
        logo.setBorder(null);
        logo.setBounds(6, 15, 250, 115);
        getContentPane().add(logo);
        task.setForeground(Color.WHITE);
        task.setBounds(1, 257, 250, 60);
        getContentPane().add(task);
        progressBar.setBackground(new Color(0x31B800FF, true));
        progressBar.setForeground(new Color(0xB800FF));
        progressBar.setValue(12);
        progressBar.setBounds(0, 275, 250, 25);
        progressBar.setBorderPainted(false);
        progressBar.setBorder(null);
        getContentPane().add(progressBar);
    }
}
