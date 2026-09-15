package view;

import javax.swing.JFrame;

import model.Model;

public class View {

    Model model;

    public View(Model model) {

        this.model = model;

        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Entropy Lab 2");

        Panel panel = new Panel(model);
        frame.add(panel);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.startThread();

    }

}
