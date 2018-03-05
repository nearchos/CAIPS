package org.inspirecenter.indoorpositioningsystem.gui;

import com.google.gson.Gson;
import org.inspirecenter.indoorpositioningsystem.model.Dataset;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;

class DatasetPanel extends JPanel {

    private File currentPath = new File( System.getProperty("user.dir"));
    private Dataset dataset = null;
    private String [] caipsFiles = {};

    private JList<String> datasetsList = new JList<>();
    private DatasetFilePanel datasetFilePanel = new DatasetFilePanel();

    DatasetPanel() {
        super(new BorderLayout());
        setBorder(new TitledBorder("Datasets"));

        final ChooseFolderPanel chooseFolderPanel = new ChooseFolderPanel();

        add(chooseFolderPanel, BorderLayout.NORTH);

        datasetsList.setBorder(new LineBorder(Color.BLUE));
        datasetsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        datasetsList.addListSelectionListener(e -> {
            final String selectedFilename = caipsFiles[datasetsList.getSelectedIndex()];
            final File currentFile = new File(currentPath, selectedFilename);
            // read dataset from file
            try {
                dataset = new Gson().fromJson(new String(Files.readAllBytes(currentFile.toPath())), Dataset.class);
            } catch (IOException ioe) {
                // todo show pop up with error
                dataset = null;
            }
            datasetFilePanel.updateDatasetFileDetails();
        });
        add(datasetsList, BorderLayout.CENTER);
        add(datasetFilePanel, BorderLayout.SOUTH);

        updateList();
        datasetFilePanel.updateDatasetFileDetails();
    }

    private void updateList() {
        if(currentPath.isDirectory()) {
            caipsFiles = currentPath.list(jsonFilenameFilter);
            datasetsList.setListData(caipsFiles);
        }
    }

    class ChooseFolderPanel extends JPanel {
        private JLabel folderPathLabel = new JLabel();
        private JButton selectPathButton = new JButton("...");

        ChooseFolderPanel() {
            super();
            setLayout(new BorderLayout());
            try {
                folderPathLabel.setText(currentPath.getCanonicalPath());
            } catch (IOException ioe) {
                folderPathLabel.setText("Error: " + ioe.getMessage());
            }
            selectPathButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // todo select path
                    System.out.println("select path");
                    // todo set folderPathLabel
//                    if path is different,             datasetFilePanel.updateDatasetFileDetails();
                }
            });
            add(folderPathLabel, BorderLayout.CENTER);
            add(selectPathButton, BorderLayout.EAST);
        }
    }

    private FilenameFilter jsonFilenameFilter = (dir, name) -> name.endsWith(".caips");

    class DatasetFilePanel extends JPanel {
        private JTextPane datasetFileDetailsTextPane = new JTextPane();
        private JButton anonymizeButton = new JButton("Anonymize");

        DatasetFilePanel() {
            super();
            setLayout(new BorderLayout());

            datasetFileDetailsTextPane.setBorder(new LineBorder(Color.BLACK));
            datasetFileDetailsTextPane.setEditable(false);

            anonymizeButton.addActionListener(e -> anonymize());
            add(datasetFileDetailsTextPane, BorderLayout.CENTER);
            add(anonymizeButton, BorderLayout.SOUTH);
        }

        void updateDatasetFileDetails() {
            final String details = dataset == null ? "Invalid or no file selected" :
                    "status: " + dataset.getStatus() + "\n"
                    + "location UUID: " + dataset.getLocationUUID().substring(dataset.getLocationUUID().length() - 8) + "\n"
                    + "num of entries: " + dataset.getMeasurements().size() + "\n";
            datasetFileDetailsTextPane.setText(details);
        }

        private void anonymize() {
            // todo
            System.out.println("Anonymize");
        }
    }
}