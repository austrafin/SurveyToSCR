package surveytoscr;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * @author Matti Syrjanen 2017
 */
public class SurveyToSCR extends JFrame {

    private HashMap<String, ArrayList> surveyPoints; // keep in HashMap so that can categorise by description
    private final HashMap<Integer, String> colours = new HashMap(); // Hash table for combobox index to colour name
    private final JList descriptionsList = new JList(new DefaultListModel());
    private final JComboBox layerComboBox = new JComboBox();
    private final JComboBox colourComboBox = new JComboBox();
    private final JCheckBox includeLabelCheckBox = new JCheckBox("Include label", true);
    private final JTextField prefixTextField = new JTextField();
    private final JTextField suffixTextField = new JTextField();
    private final JButton exportSelectedButton = new JButton("Export Selected");
    private final JButton exportAllButton = new JButton("Export All");
    private HashMap<String, Description> descriptions; // key is the description name in the list
    private HashMap<String, String> profileLayer = new HashMap();
    private HashMap<String, String> profileColour = new HashMap();
    private final String defaultLayer = "By Description";
    final JSpinner textHeightSpinner = new JSpinner(new SpinnerNumberModel(0.01, 0.001, 100, 0.001));

    public enum FileType {
        CSV, PROF, SCR
    }

    public SurveyToSCR() {
        init();
    }

    private void init() {
        final int gap = 5;
        final JPanel buttonPanel = new JPanel(new GridLayout(2, 3, gap, gap));
        final JPanel comboBoxPanel = new JPanel(new GridLayout(3, 1, gap, gap));
        final JPanel labelPanel = new JPanel(new GridLayout(3, 1, gap, gap));
        final JPanel cbButtonPanel = new JPanel(new GridLayout(3, 1, gap, gap));
        final JPanel deleteLayerButtonPanel = new JPanel(new GridLayout(3, 1, gap, gap));
        final JPanel rightPanel = new JPanel(new BorderLayout());
        final JPanel centrePanel = new JPanel(new BorderLayout());
        final JPanel centrePanel2 = new JPanel(new BorderLayout());
        final JPanel centrePanel3 = new JPanel(new BorderLayout());
        final JPanel layerSettingsPanel = new JPanel();
        final JPanel prefixSuffixPanel = new JPanel();
        final JPanel prefixPanel = new JPanel(new BorderLayout());
        final JPanel suffixPanel = new JPanel(new BorderLayout());
        final JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        final JButton importCSVButton = new JButton("Import CSV");
        final JButton loadProfileButton = new JButton("Load Profile");
        final JButton saveProfileButton = new JButton("Save Profile");
        final JButton resetProfileButton = new JButton("Reset to Default");
        final JButton setAllLayerButton = new JButton("Set All");
        final JButton setAllColourButton = new JButton("Set All");
        final JButton addLayerButton = new JButton("Add");
        final JButton deleteLayerButton = new JButton("Delete");
        final JScrollPane descriptionsScrollPane = new JScrollPane(descriptionsList);
        final JTextField addLayerTextField = new JTextField();

        // Add to components
        getContentPane().add(descriptionsScrollPane);
        getContentPane().add(rightPanel);

        rightPanel.add(layerSettingsPanel, BorderLayout.NORTH);
        rightPanel.add(centrePanel, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        centrePanel.add(prefixSuffixPanel, BorderLayout.NORTH);
        centrePanel.add(centrePanel2);

        centrePanel2.add(centrePanel3);
        centrePanel2.add(includeLabelCheckBox, BorderLayout.NORTH);

        centrePanel3.add(spinnerPanel, BorderLayout.NORTH);

        spinnerPanel.add(new JLabel("Text height:"));
        spinnerPanel.add(textHeightSpinner);

        layerSettingsPanel.add(labelPanel);
        layerSettingsPanel.add(comboBoxPanel);
        layerSettingsPanel.add(cbButtonPanel);
        layerSettingsPanel.add(deleteLayerButtonPanel);

        labelPanel.add(new JLabel("Layer"));
        labelPanel.add(new JLabel("Colour"));
        labelPanel.add(new JLabel("Add layer"));

        comboBoxPanel.add(layerComboBox);
        comboBoxPanel.add(colourComboBox);
        comboBoxPanel.add(addLayerTextField);

        cbButtonPanel.add(setAllLayerButton);
        cbButtonPanel.add(setAllColourButton);
        cbButtonPanel.add(addLayerButton);

        deleteLayerButtonPanel.add(deleteLayerButton);

        buttonPanel.add(importCSVButton);
        buttonPanel.add(exportSelectedButton);
        buttonPanel.add(exportAllButton);
        buttonPanel.add(loadProfileButton);
        buttonPanel.add(saveProfileButton);
        buttonPanel.add(resetProfileButton);

        prefixSuffixPanel.add(prefixPanel);
        prefixSuffixPanel.add(suffixPanel);

        prefixPanel.add(new JLabel("Layer prefix:  "), BorderLayout.WEST);
        prefixPanel.add(prefixTextField, BorderLayout.CENTER);
        suffixPanel.add(new JLabel("Layer suffix:  "), BorderLayout.WEST);
        suffixPanel.add(suffixTextField, BorderLayout.CENTER);

        colourComboBox.addItem("ByLayer");
        colourComboBox.addItem("ByBlock");
        colourComboBox.addItem("Red");
        colourComboBox.addItem("Yellow");
        colourComboBox.addItem("Green");
        colourComboBox.addItem("Cyan");
        colourComboBox.addItem("Blue");
        colourComboBox.addItem("Magenta");
        colourComboBox.addItem("White");

        addLayer(defaultLayer);

        // initialise colours
        for (int i = 8; i <= 255; ++i) {
            colourComboBox.addItem("Colour " + i);
        }

        colours.put(-1, "ByLayer");

        for (int i = 0; i < colourComboBox.getItemCount() - 1; ++i) {
            colours.put(i, colourComboBox.getItemAt(i + 1).toString()); // skip ByLayer
        }

        // Set sizes
        this.setMinimumSize(new Dimension(800, 300));
        this.setSize(new Dimension(900, 400));
        comboBoxPanel.setPreferredSize(new Dimension(10000, 20));
        buttonPanel.setMinimumSize(new Dimension(200, 20));
        prefixPanel.setPreferredSize(new Dimension(10000, 25));
        suffixPanel.setPreferredSize(new Dimension(10000, 25));

        // Set layouts
        this.setLayout(new GridLayout());
        layerSettingsPanel.setLayout(new BoxLayout(layerSettingsPanel, BoxLayout.LINE_AXIS));
        prefixSuffixPanel.setLayout(new BoxLayout(prefixSuffixPanel, BoxLayout.LINE_AXIS));

        // Set Borders
        Border margins = BorderFactory.createEmptyBorder(gap, gap, gap, gap);
        Border rightMargin = BorderFactory.createEmptyBorder(0, 0, 0, gap);
        layerSettingsPanel.setBorder(margins);
        prefixSuffixPanel.setBorder(margins);
        buttonPanel.setBorder(margins);
        prefixPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        labelPanel.setBorder(rightMargin);
        comboBoxPanel.setBorder(rightMargin);
        cbButtonPanel.setBorder(rightMargin);

        // Set listeners
        importCSVButton.addActionListener((ActionEvent ae) -> {
            importCSV();
        });

        addLayerButton.addActionListener((ActionEvent ae) -> {
            addLayer(addLayerTextField.getText());
            addLayerTextField.setText("");
        });

        exportSelectedButton.addActionListener((ActionEvent ae) -> {
            List dList = descriptionsList.getSelectedValuesList();
            Map<String, ArrayList> points = new HashMap();
            for (int i = 0; i < dList.size(); ++i) {
                points.put(dList.get(i).toString(), surveyPoints.get(dList.get(i).toString()));
            }
            exportSCR(points);
        });

        exportAllButton.addActionListener((ActionEvent ae) -> {
            exportSCR(surveyPoints);
        });

        deleteLayerButton.addActionListener((ActionEvent ae) -> {
            deleteLayer(layerComboBox.getSelectedItem().toString());
        });

        setAllLayerButton.addActionListener((ActionEvent ae) -> {
            for (Description d : descriptions.values()) {
                d.setLayer(layerComboBox.getSelectedItem().toString());
            }
        });

        setAllColourButton.addActionListener((ActionEvent ae) -> {
            for (Description d : descriptions.values()) {
                d.setColour(colourComboBox.getSelectedItem().toString());
            }
        });

        loadProfileButton.addActionListener((ActionEvent ae) -> {
            JFileChooser browse = getBrowse(FileType.PROF);

            if (browse.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                loadProfile(browse.getSelectedFile());
            }
        });

        saveProfileButton.addActionListener((ActionEvent ae) -> {
            JFileChooser browse = getBrowse(FileType.PROF);

            if (browse.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                saveProfile(new File(browse.getSelectedFile() + ".prof"));
            }
        });

        resetProfileButton.addActionListener((ActionEvent ae) -> {
            resetToDefault();
        });

        layerComboBox.addItemListener((ItemEvent ie) -> {
            if (layerComboBox.getSelectedIndex() > -1) {
                List dList = descriptionsList.getSelectedValuesList();

                for (int i = 0; i < dList.size(); ++i) {
                    descriptions.get(dList.get(i).toString()).setLayer(layerComboBox.getSelectedItem().toString());
                }
                deleteLayerButton.setEnabled(!layerComboBox.getSelectedItem().equals(defaultLayer));
            }
        });

        colourComboBox.addItemListener((ItemEvent ie) -> {
            if (colourComboBox.getSelectedIndex() > -1) {
                List dList = descriptionsList.getSelectedValuesList();

                for (int i = 0; i < dList.size(); ++i) {
                    descriptions.get(dList.get(i).toString()).setColour(colourComboBox.getSelectedItem().toString());
                }

            }
        });

        descriptionsList.addListSelectionListener((ListSelectionEvent ev) -> {
            List dList = descriptionsList.getSelectedValuesList();
            if (dList.size() >= 1) {
                String first = dList.get(0).toString();
                boolean layerDifferent = false, colourDifferent = false;

                for (int i = 0; i < dList.size(); ++i) {
                    if (!descriptions.get(first).getLayer().equals(descriptions.get(dList.get(i).toString()).getLayer())) {
                        layerDifferent = true;
                    }
                    if (!descriptions.get(first).getColour().equals(descriptions.get(dList.get(i).toString()).getColour())) {
                        colourDifferent = true;
                    }
                }

                if (layerDifferent) {
                    layerComboBox.setSelectedIndex(-1);
                } else {
                    layerComboBox.setSelectedItem(descriptions.get(first).getLayer());
                }
                if (colourDifferent) {
                    colourComboBox.setSelectedIndex(-1);
                } else {
                    colourComboBox.setSelectedItem(descriptions.get(first).getColour());
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                saveProfile(new File("last_used.prof"));
            }
        });

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("Survey to SCR");
        exportSelectedButton.setEnabled(false);
        exportAllButton.setEnabled(false);
        deleteLayerButton.setEnabled(false);
        loadProfile(new File("last_used.prof"));
    }

    private boolean layerExists(String item) {
        for (int i = 0; i < layerComboBox.getItemCount(); ++i) {
            if (layerComboBox.getItemAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }

    private void addLayer(String layer) {
        if (!layerExists(layer) && !layer.equals("")) {
            layerComboBox.addItem(layer);
        }
    }

    private void resetToDefault() {
        while (layerComboBox.getItemCount() > 1) {
            deleteLayer(layerComboBox.getItemAt(layerComboBox.getItemCount() - 1).toString());
        }

        addLayer("0");
        addLayer("Defpoints");

        if (descriptions != null) {
            for (Description d : descriptions.values()) {
                d.setLayer(defaultLayer);
                d.setColour(colours.get(-1)); // by layer
            }
        }
    }

    private void deleteLayer(String layer) {
        if (!layer.equals(defaultLayer)) {
            layerComboBox.removeItem(layer);
            layerComboBox.setSelectedItem(defaultLayer);

            if (descriptions != null) {
                for (Description d : descriptions.values()) {
                    if (d.getLayer().equals(layer)) {
                        d.setLayer(defaultLayer);
                        profileLayer.put(d.toString(), defaultLayer);
                    }
                }
            }
        }
    }

    private JFileChooser getBrowse(FileType fileType) {
        JFileChooser browse = new JFileChooser();
        FileFilter filter;

        switch (fileType) {
            case PROF:
                filter = new FileNameExtensionFilter("Profile file", "prof", "PROF");
                break;
            case SCR:
                filter = new FileNameExtensionFilter("AutoCad script file", "scr", "SCR");
                break;
            default:
                filter = new FileNameExtensionFilter("csv file", "csv", "CSV");
        }

        browse.addChoosableFileFilter(filter);
        browse.setFileFilter(filter);

        return browse;
    }

    private void importCSV() {
        JFileChooser browse = getBrowse(FileType.CSV);

        if (browse.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(browse.getSelectedFile().toString()));
                String str, description;
                String[] values;
                DefaultListModel model = (DefaultListModel) descriptionsList.getModel();

                model.removeAllElements();
                surveyPoints = new HashMap();
                descriptions = new HashMap();

                exportSelectedButton.setEnabled(true);
                exportAllButton.setEnabled(true);

                while ((str = in.readLine()) != null) {
                    values = str.split(",");
                    description = values[4];

                    if (surveyPoints.get(description) == null) {
                        surveyPoints.put(description, new ArrayList());
                    }

                    surveyPoints.get(description).add(new SurveyPoint(values[0], values[1], values[2], values[3], description));

                    if (!model.contains(description)) {
                        String layer, colour;
                        if (profileLayer.get(description) == null) {
                            layer = defaultLayer;
                            colour = colourComboBox.getSelectedItem().toString();
                        } else {
                            layer = profileLayer.get(description);
                            colour = profileColour.get(description);
                        }
                        descriptions.put(description, new Description(description, layer, colour));
                        model.addElement(description);
                    }
                }
                in.close();
            } catch (java.io.IOException ex) {
                System.err.println("Failed to open file.");
            }
        }
    }

    private void exportSCR(Map<String, ArrayList> points) {
        JFileChooser browse = getBrowse(FileType.SCR);

        if (browse.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(browse.getSelectedFile().toString() + ".scr", "UTF-8")) {
                String textSize = textHeightSpinner.getValue().toString(), textAngle = "0", prefix = prefixTextField.getText(), suffix = suffixTextField.getText();

                writer.append("OSMODE\n0\n");

                for (ArrayList<SurveyPoint> arr : points.values()) {
                    String layer = descriptions.get(arr.get(0).getDescription()).getLayer();
                    if (layer.equals(defaultLayer)) {
                        layer = arr.get(0).getDescription();
                    }
                    layer = prefix + layer + suffix;

                    writer.append("-LAYER\nnew\n\"" + layer + "\"\nset\n\"" + layer
                            + "\"\n\ncolor\n" + descriptions.get(arr.get(0).getDescription()).getColour() + "\n");

                    for (int i = 0; i < arr.size(); ++i) {
                        writer.append("POINT\n" + arr.get(i).getX() + "," + arr.get(i).getY() + "," + arr.get(i).getZ() + "\n");

                        if (includeLabelCheckBox.isSelected()) {
                            writer.append("TEXT\n" + arr.get(i).getX() + "," + arr.get(i).getY()
                                    + "\n" + textSize + "\n" + textAngle + "\n" + arr.get(i).getDescription() + "\n");
                        }
                    }
                }
            } catch (IOException e) {
                // do something
            }
        }
    }

    private DocumentBuilder getDocumentBuilder() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SurveyToSCR.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dBuilder;
    }

    private void loadProfile(File fileName) {
        try {
            DocumentBuilder dBuilder = getDocumentBuilder();
            Document doc = dBuilder.parse(fileName);
            doc.getDocumentElement().normalize();

            prefixTextField.setText(doc.getElementsByTagName("layer_prefix").item(0).getAttributes().item(0).getTextContent());
            suffixTextField.setText(doc.getElementsByTagName("layer_suffix").item(0).getAttributes().item(0).getTextContent());
            textHeightSpinner.setValue(Double.parseDouble(doc.getElementsByTagName("text_height").item(0).getAttributes().item(0).getTextContent()));
            includeLabelCheckBox.setSelected(doc.getElementsByTagName("include_label").item(0).getAttributes().item(0).getTextContent().equals("true"));

            NodeList descElements = doc.getElementsByTagName("description");
            NodeList layerElements = doc.getElementsByTagName("layer");
            DefaultListModel model = (DefaultListModel) descriptionsList.getModel();

            profileLayer = new HashMap();
            profileColour = new HashMap();

            for (int i = 0; i < layerElements.getLength(); ++i) {
                Node nNode = layerElements.item(i);
                Element e = (Element) nNode;
                String layerName = e.getAttribute("name");
                addLayer(layerName);
            }

            for (int i = 0; i < descElements.getLength(); ++i) {
                Node nNode = descElements.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) nNode;
                    String descriptionName = e.getAttribute("name");
                    String layer = e.getElementsByTagName("layer").item(0).getTextContent();
                    String colour = e.getElementsByTagName("colour").item(0).getTextContent();
                    profileLayer.put(descriptionName, layer);
                    profileColour.put(descriptionName, colour);

                    if (!layerExists(layer)) {
                        addLayer(layer);
                    }

                    if (model.contains(descriptionName)) {
                        descriptions.get(descriptionName).setLayer(layer);
                        descriptions.get(descriptionName).setColour(colour);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    private void saveProfile(File fileName) {
        try {
            DocumentBuilder dBuilder = getDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("profile"),
                    layersElement = doc.createElement("layers"),
                    descriptionsElement = doc.createElement("descriptions"),
                    prefixElement = doc.createElement("layer_prefix"),
                    suffixElement = doc.createElement("layer_suffix"),
                    textHeightElement = doc.createElement("text_height"),
                    includeLabelElement = doc.createElement("include_label");
            Attr prefixAttr = doc.createAttribute("prefix"),
                    suffixAttr = doc.createAttribute("suffix"),
                    textHeightAttr = doc.createAttribute("height"),
                    includeLabelAttr = doc.createAttribute("label");

            prefixAttr.setValue(prefixTextField.getText());
            suffixAttr.setValue(suffixTextField.getText());
            textHeightAttr.setValue(textHeightSpinner.getValue().toString());
            includeLabelAttr.setValue(Boolean.toString(includeLabelCheckBox.isSelected()));

            prefixElement.setAttributeNode(prefixAttr);
            suffixElement.setAttributeNode(suffixAttr);
            textHeightElement.setAttributeNode(textHeightAttr);
            includeLabelElement.setAttributeNode(includeLabelAttr);

            doc.appendChild(rootElement);
            rootElement.appendChild(prefixElement);
            rootElement.appendChild(suffixElement);
            rootElement.appendChild(textHeightElement);
            rootElement.appendChild(includeLabelElement);
            rootElement.appendChild(layersElement);
            rootElement.appendChild(descriptionsElement);

            for (int i = 1; i < layerComboBox.getItemCount(); ++i) { // skip by description
                Element laElement = doc.createElement("layer");
                Attr layerNameAttr = doc.createAttribute("name");

                layerNameAttr.setValue(layerComboBox.getItemAt(i).toString());
                laElement.setAttributeNode(layerNameAttr);
                layersElement.appendChild(laElement);
            }

            if (descriptions != null) {
                for (Description d : descriptions.values()) {
                    Element descElement = doc.createElement("description"),
                            layerElement = doc.createElement("layer"), colourElement = doc.createElement("colour");
                    Attr descriptionNameAttr = doc.createAttribute("name");

                    descriptionNameAttr.setValue(d.toString());
                    descElement.setAttributeNode(descriptionNameAttr);

                    descriptionsElement.appendChild(descElement);

                    descElement.appendChild(layerElement);
                    descElement.appendChild(colourElement);

                    layerElement.appendChild(doc.createTextNode(d.getLayer()));
                    colourElement.appendChild(doc.createTextNode(d.getColour()));
                }
            }

            // write the content into xml file                              
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc),
                    new StreamResult(fileName));

        } catch (TransformerException ex) {
            Logger.getLogger(SurveyToSCR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
