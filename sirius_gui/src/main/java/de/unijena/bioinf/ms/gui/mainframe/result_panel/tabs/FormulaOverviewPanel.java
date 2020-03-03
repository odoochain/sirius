package de.unijena.bioinf.ms.gui.mainframe.result_panel.tabs;
/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com)
 * as part of the sirius_frontend
 * 26.01.17.
 */

import de.unijena.bioinf.ms.gui.mainframe.result_panel.PanelDescription;
import de.unijena.bioinf.ms.gui.molecular_formular.FormulaList;
import de.unijena.bioinf.ms.gui.molecular_formular.FormulaListDetailView;

import javax.swing.*;
import java.awt.*;

/**
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 */
//todo this is beta state
public class FormulaOverviewPanel extends JPanel implements PanelDescription {
    @Override
    public String getDescription() {
        return "Overview about your Experiment and Results of the Formula Identification with Sirius";
    }

    public FormulaOverviewPanel(FormulaList suriusResultElements) {
        super(new BorderLayout());
        TreeVisualizationPanel overviewTVP = new TreeVisualizationPanel();
        suriusResultElements.addActiveResultChangedListener(overviewTVP);
        SpectraVisualizationPanel overviewSVP = new SpectraVisualizationPanel();
        suriusResultElements.addActiveResultChangedListener(overviewSVP);

        final FormulaListDetailView north = new FormulaListDetailView(suriusResultElements);


        JSplitPane east = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, overviewSVP, overviewTVP);
        east.setDividerLocation(.5d);
        east.setResizeWeight(.5d);
        JSplitPane major = new JSplitPane(JSplitPane.VERTICAL_SPLIT, north, east);
        major.setDividerLocation(250);
        add(major, BorderLayout.CENTER);
    }
}