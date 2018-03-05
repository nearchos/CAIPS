package org.inspirecenter.indoorpositioningsystem.gui;

import org.inspirecenter.indoorpositioningsystem.algorithms.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ExperimentSetupPanel extends JPanel {

    private static final LocationEstimationAlgorithm [] allAlgorithms = {
        new BruteForceLocationEstimationAlgorithm(),
        new BruteForceSameDeviceLocationEstimationAlgorithm(),
        new BruteForceSimilarBatteryLevelLocationEstimationAlgorithm(),
        new BruteForceTopMatchesOnlyLocationEstimationAlgorithm(),
        new TriangulationOfBestMatchesLocationEstimationAlgorithm()
    };

    private JList<LocationEstimationAlgorithm> algorithmsList = new JList<>();
    private JLabel selectedAlgorithmLabel = new JLabel();

    private LocationEstimationAlgorithm selectedAlgorithm;

    ExperimentSetupPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new TitledBorder("Experiment setup"));

        algorithmsList.setPreferredSize(new Dimension(Short.MAX_VALUE, algorithmsList.getPreferredSize().height));
        add(algorithmsList);
        add(selectedAlgorithmLabel);

        algorithmsList.setListData(allAlgorithms);
        algorithmsList.setBorder(new LineBorder(Color.BLUE));
        algorithmsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        algorithmsList.addListSelectionListener(e -> {
            selectedAlgorithm = allAlgorithms[algorithmsList.getSelectedIndex()];
            selectedAlgorithmLabel.setText("Selected algorithm: " + selectedAlgorithm.getName());
        });
        algorithmsList.setSelectedIndex(0);
    }
}