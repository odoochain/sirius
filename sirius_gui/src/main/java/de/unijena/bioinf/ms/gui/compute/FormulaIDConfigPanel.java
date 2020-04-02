package de.unijena.bioinf.ms.gui.compute;

import de.unijena.bioinf.ChemistryBase.chem.Element;
import de.unijena.bioinf.ChemistryBase.chem.FormulaConstraints;
import de.unijena.bioinf.ChemistryBase.chem.PeriodicTable;
import de.unijena.bioinf.ChemistryBase.chem.PrecursorIonType;
import de.unijena.bioinf.ChemistryBase.ms.MsInstrumentation;
import de.unijena.bioinf.chemdb.SearchableDatabase;
import de.unijena.bioinf.ms.frontend.core.ApplicationCore;
import de.unijena.bioinf.ms.gui.dialogs.ExceptionDialog;
import de.unijena.bioinf.ms.gui.utils.GuiUtils;
import de.unijena.bioinf.ms.gui.utils.TextHeaderBoxPanel;
import de.unijena.bioinf.ms.gui.utils.TwoColumnPanel;
import de.unijena.bioinf.ms.gui.utils.jCheckboxList.CheckBoxListItem;
import de.unijena.bioinf.ms.gui.utils.jCheckboxList.JCheckBoxList;
import de.unijena.bioinf.ms.gui.utils.jCheckboxList.JCheckboxListPanel;
import de.unijena.bioinf.projectspace.InstanceBean;
import de.unijena.bioinf.sirius.Ms1Preprocessor;
import de.unijena.bioinf.sirius.ProcessedInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Panel to configure SIRIUS Computations
 * Provides CONFIGS for SiriusSubTool
 *
 * @author Marcus Ludwig, Markus Fleischauer
 * @since 12.01.17
 */
public class FormulaIDConfigPanel extends ConfigPanel {
    protected Logger logger = LoggerFactory.getLogger(FormulaIDConfigPanel.class);

    public enum Instrument {
        QTOF("Q-TOF", MsInstrumentation.Instrument.QTOF, "qtof", 10), ORBI("Orbitrap", MsInstrumentation.Instrument.ORBI, "orbitrap", 5), FTICR("FT-ICR", MsInstrumentation.Instrument.FTICR, "orbitrap", 2), BRUKER("Q-TOF (isotopes)", MsInstrumentation.Instrument.BRUKER_MAXIS, "qtof", 10);

        public final String name, profile;
        public final MsInstrumentation instrument;
        public final int ppm;

        Instrument(String name, MsInstrumentation instrument, String profile, int ppm) {
            this.name = name;
            this.profile = profile;
            this.ppm = ppm;
            this.instrument = instrument;
        }

        public String asProfile() {
            return profile;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    protected final JCheckboxListPanel<String> ionizationList;
    protected final JCheckboxListPanel<SearchableDatabase> searchDBList;
    protected final JComboBox<Instrument> profileSelector;
    protected final SpinnerNumberModel ppm, candidates, candidatesPerIon;
    protected final JSpinner ppmSpinner, candidatesSpinner, candidatesPerIonSpinner;
    protected final JCheckBox restrictToOrganics;
    protected ElementsPanel elementPanel;
    protected JButton elementAutoDetect;


    protected final List<InstanceBean> ecs;


    protected final Dialog owner;

    public FormulaIDConfigPanel(Dialog owner, List<InstanceBean> ecs) {
        super();
        this.ecs = ecs;
        this.owner = owner;


        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        final JPanel center = applyDefaultLayout(new JPanel());
        add(center);

        // configure database to search list
        searchDBList = new JCheckboxListPanel<>(new DBSelectionList(), "Consider only formulas in:");
        GuiUtils.assignParameterToolTip(searchDBList, "FormulaSearchDB");
        center.add(searchDBList);
        parameterBindings.put("FormulaSearchDB", () -> getFormulaSearchDBs().stream().map(SearchableDatabase::name).
                collect(Collectors.joining(",")));

        //configure ionization panels
        ionizationList = new JCheckboxListPanel<>(new JCheckBoxList<>(), "Possible Ionizations", "Set possible ionisation for data with unknown ionization");
        ionizationList.checkBoxList.setPrototypeCellValue(new CheckBoxListItem<>("[M + Na]+ ", false));
        center.add(ionizationList);

        // configure small stuff panel
        final TwoColumnPanel smallParameters = new TwoColumnPanel();
        center.add(new TextHeaderBoxPanel("Parameters", smallParameters));

        Vector<Instrument> instruments = new Vector<>();
        Collections.addAll(instruments, Instrument.values());
        profileSelector = new JComboBox<>(instruments);
        GuiUtils.assignParameterToolTip(profileSelector, "AlgorithmProfile");
        parameterBindings.put("AlgorithmProfile", () -> getInstrument().name);
        smallParameters.addNamed("Instrument", profileSelector);


        ppm = new SpinnerNumberModel(10, 0.25, 20, 0.25);
        ppmSpinner = new JSpinner(this.ppm);
        ppmSpinner.setMinimumSize(new Dimension(70, 26));
        ppmSpinner.setPreferredSize(new Dimension(70, 26));
        GuiUtils.assignParameterToolTip(ppmSpinner, "MS2MassDeviation.allowedMassDeviation");
        parameterBindings.put("MS2MassDeviation.allowedMassDeviation", () -> String.valueOf(getPpm()));
        smallParameters.addNamed("Ms2MassDev (ppm)", ppmSpinner);


        candidates = new SpinnerNumberModel(10, 1, 10000, 1);
        candidatesSpinner = new JSpinner(candidates);
        candidatesSpinner.setMinimumSize(new Dimension(70, 26));
        candidatesSpinner.setPreferredSize(new Dimension(70, 26));
        GuiUtils.assignParameterToolTip(candidatesSpinner, "NumberOfCandidates");
        parameterBindings.put("NumberOfCandidates", () -> String.valueOf(getNumOfCandidates()));
        smallParameters.addNamed("Candidates", candidatesSpinner);


        candidatesPerIon = new SpinnerNumberModel(0, 0, 10000, 1);
        candidatesPerIonSpinner = new JSpinner(candidates);
        candidatesPerIonSpinner.setMinimumSize(new Dimension(70, 26));
        candidatesPerIonSpinner.setPreferredSize(new Dimension(70, 26));
        GuiUtils.assignParameterToolTip(candidatesPerIonSpinner, "NumberOfCandidatesPerIon");
        parameterBindings.put("NumberOfCandidatesPerIon", () -> String.valueOf(getNumOfCandidatesPerIon()));
        smallParameters.addNamed("Candidates per Ion", candidatesPerIonSpinner);

        restrictToOrganics = new JCheckBox();
        GuiUtils.assignParameterToolTip(restrictToOrganics, "RestrictToOrganics");
        parameterBindings.put("RestrictToOrganics", () -> String.valueOf(restrictToOrganics.isSelected()));
        smallParameters.addNamed("Restrict to organics", restrictToOrganics);

        //sync profile with ppm spinner
        profileSelector.addItemListener(e -> {
            final Instrument i = (Instrument) e.getItem();
            final double recommendedPPM = i.ppm;
            ppmSpinner.setValue(recommendedPPM);
        });


        // configure Element panel
        makeElementPanel(ecs.size() > 1);
        add(elementPanel);
        parameterBindings.put("FormulaSettings.enforced", () -> {
            return elementPanel.getElementConstraints().toString(); //todo check if this makes scence
        });

        parameterBindings.put("FormulaSettings.detectable", () -> {
            final List<Element> elementsToAutoDetect = elementPanel.individualAutoDetect ? elementPanel.getElementsToAutoDetect() : Collections.emptyList();
            return (elementsToAutoDetect.isEmpty() ? "," :
                    elementsToAutoDetect.stream().map(Element::toString).collect(Collectors.joining(",")));
        }); //todo check if this makes scence

        //enable disable element panel if db is selected
        searchDBList.checkBoxList.addListSelectionListener(e -> {
            final List<SearchableDatabase> source = getFormulaSearchDBs();
            //todo does this make scence
            elementPanel.enableElementSelection(source == null || source.isEmpty());
            if (elementAutoDetect != null)
                elementAutoDetect.setEnabled(source == null || source.isEmpty());
        });

        refreshPossibleIonizations(ecs.stream().map(it -> it.getIonization().getIonization().toString()).collect(Collectors.toSet()));
    }

    public void refreshPossibleIonizations(Set<String> ionTypes) {
        java.util.List<String> ionizations = new ArrayList<>();

        if (!ionTypes.isEmpty()) {

            if (ionTypes.contains(PrecursorIonType.unknownPositive().getIonization().getName())) {
                ionizations.addAll(PeriodicTable.getInstance().getPositiveIonizationsAsString());
            }
            if (ionTypes.contains(PrecursorIonType.unknownNegative().getIonization().getName())) {
                ionizations.addAll(PeriodicTable.getInstance().getNegativeIonizationsAsString());
            }
        }
        if (ionizations.isEmpty()) {
            ionizationList.checkBoxList.replaceElements(ionTypes.stream().sorted().collect(Collectors.toList()));
            ionizationList.checkBoxList.checkAll();
            ionizationList.setEnabled(false);
        } else {
            Collections.sort(ionizations);
            ionizationList.checkBoxList.replaceElements(ionizations);
            ionizationList.checkBoxList.checkAll();
            ionizationList.setEnabled(true);
        }
    }

    protected void makeElementPanel(boolean multi) {
        List<Element> detectableElements = new ArrayList<>(ApplicationCore.SIRIUS_PROVIDER.sirius().getMs1Preprocessor().getSetOfPredictableElements());
        if (multi) {
            elementPanel = new ElementsPanel(owner, 4, detectableElements);
        } else {
            /////////////Solo Element//////////////////////
            elementPanel = new ElementsPanel(owner, 4);
            StringBuilder builder = new StringBuilder();
            builder.append("Auto detectable element are: ");
            for (int i = 0; i < detectableElements.size(); i++) {
                if (i != 0) builder.append(", ");
                builder.append(detectableElements.get(i).getSymbol());
            }
            elementAutoDetect = new JButton("Auto detect");
            elementAutoDetect.setToolTipText(builder.toString());
            elementAutoDetect.addActionListener(e -> detectElements());
            elementAutoDetect.setEnabled(true);
            elementPanel.lowerPanel.add(elementAutoDetect);
        }
        elementPanel.setBorder(BorderFactory.createEmptyBorder(0,GuiUtils.LARGE_GAP,0,0));
    }

    protected void detectElements() {
        String notWorkingMessage = "Element detection requires MS1 spectrum with isotope pattern.";
        InstanceBean ec = ecs.get(0);
        if (!ec.getMs1Spectra().isEmpty() || ec.getMergedMs1Spectrum() != null) {
            final Ms1Preprocessor pp = ApplicationCore.SIRIUS_PROVIDER.sirius().getMs1Preprocessor();
            ProcessedInput pi = pp.preprocess(ec.getExperiment());

            pi.getAnnotation(FormulaConstraints.class).
                    ifPresentOrElse(c -> {
                                final Set<Element> pe = pp.getSetOfPredictableElements();
                                for (Element element : c.getChemicalAlphabet()) {
                                    if (!pe.contains(element)) {
                                        c.setLowerbound(element, 0);
                                        c.setUpperbound(element, 0);
                                    }
                                }
                                elementPanel.setSelectedElements(c);
                            },
                            () -> new ExceptionDialog(owner, notWorkingMessage)
                    );

        } else {
            new ExceptionDialog(owner, notWorkingMessage);
        }
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        ionizationList.setEnabled(enabled);
        searchDBList.setEnabled(enabled);
        profileSelector.setEnabled(enabled);
        ppmSpinner.setEnabled(enabled);
        candidatesSpinner.setEnabled(enabled);
        candidatesPerIonSpinner.setEnabled(enabled);
        restrictToOrganics.setEnabled(enabled);
    }

    public Instrument getInstrument() {
        return (Instrument) profileSelector.getSelectedItem();
    }

    public double getPpm() {
        return ppm.getNumber().doubleValue();
    }

    public int getNumOfCandidates() {
        return candidates.getNumber().intValue();
    }

    public int getNumOfCandidatesPerIon() {
        return candidatesPerIon.getNumber().intValue();
    }

    public List<SearchableDatabase> getFormulaSearchDBs() {
        return searchDBList.checkBoxList.getCheckedItems();
    }




}
