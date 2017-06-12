package de.unijena.bioinf.sirius.cli;
/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com)
 * as part of the sirius
 * 15.06.16.
 */

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import de.unijena.bioinf.sirius.core.ApplicationCore;
import de.unijena.bioinf.sirius.gui.mainframe.MainFrame;
import de.unijena.bioinf.sirius.gui.utils.SwingUtils;

import java.util.Arrays;

/**
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 */
public class SiriusApplication extends ApplicationCore {
    private SiriusApplication(){}
    //-c 50 --guession '[M]+,[M+H]+,[M+Na]+,[M+K]+' --beautifytrees -w /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/part.workspace /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/part.mgf
    //--zodiac --spectral-hits /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/cleanHitsTable.csv -o /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/gibbsOutput.csv --iterations 200000 --burn-in 20000 --thresholdFilter 0.95 --distribution exponential --sirius /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/part.workspace

    //--zodiac --spectral-hits /home/ge28quv/@data/gibbsTestData/METABOLOMICS-SNETS-ANNOTATED-PAIRS-66d88580-view_all_annotations_DB-main.tsv --spectra /home/ge28quv/@data/gibbsTestData/spectral_data_ms1-2.mgf -o /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/gibbsOutput.csv --iterations 200000 --burn-in 20000 --thresholdFilter 0.95 --distribution exponential --sirius /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/part.workspace
    public static void main(String[] args) {
//        final FingeridApplication cli = new FingeridApplication();
//        cli.parseArgs(args, FingerIdOptions.class);
        final ZodiacCLI cli = new ZodiacCLI();
        cli.parseArgs(args, FingerIdOptions.class);

        if (cli.options.isZodiac()){
            ZodiacOptions options = null;
            try {
                options = CliFactory.createCli(ZodiacOptions.class).parseArguments(Arrays.copyOfRange(args, 1, args.length));
            } catch (HelpRequestedException e) {
                cli.println(e.getMessage());
                cli.println("");
                System.exit(0);
            }

            cli.setup();
            cli.validate();
            Zodiac zodiac = new Zodiac(options);
            zodiac.run();
        } else if (cli.options.isGUI()) {
            SwingUtils.initUI();
            MainFrame.MF.setLocationRelativeTo(null);//init mainframe


        } else {
            cli.setup();
            cli.validate();
            cli.compute();
        }
    }
}
