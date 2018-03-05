package org.inspirecenter.indoorpositioningsystem.gui;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public static final String TITLE = "CAIPS Benchmarking Tool";

    private Main() {
        super(TITLE);
    }

    private static DatasetPanel datasetPanel = new DatasetPanel();
    private static ExperimentSetupPanel experimentSetupPanel = new ExperimentSetupPanel();
    private static ExperimentResults experimentResults = new ExperimentResults();

    public static void main(String[] args) {

        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 3));
        panel.add(datasetPanel);
        panel.add(experimentSetupPanel);
        panel.add(experimentResults);

        //1. Create the frame.
        JFrame frame = new JFrame(TITLE) {
            @Override public Dimension getPreferredSize() {
                return new Dimension(1024, 768);
            }
        };

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //3. Create components and put them in the frame (container).
        frame.setContentPane(panel);

        //4. Size the frame.
        frame.pack();

        //5. Show it.
        frame.setVisible(true);
    }
}
