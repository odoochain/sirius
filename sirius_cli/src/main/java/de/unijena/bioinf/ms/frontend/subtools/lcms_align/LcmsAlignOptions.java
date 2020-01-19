package de.unijena.bioinf.ms.frontend.subtools.lcms_align;

import de.unijena.bioinf.ms.frontend.io.projectspace.ProjectSpaceManager;
import de.unijena.bioinf.ms.frontend.subtools.InputFilesOptions;
import de.unijena.bioinf.ms.frontend.subtools.PreprocessingTool;
import de.unijena.bioinf.ms.frontend.subtools.Provide;
import picocli.CommandLine;

@CommandLine.Command(name = "lcms-align", aliases = {"A"}, description = "<PREPROCESSING> Align and merge compounds of multiple LCMS Runs. Use this tool if you want to import from mzML/mzXml", defaultValueProvider = Provide.Defaults.class, versionProvider = Provide.Versions.class, mixinStandardHelpOptions = true)
public class LcmsAlignOptions implements PreprocessingTool<LcmsAlignSubToolJob> {

    @Override
    public LcmsAlignSubToolJob makePreprocessingJob(InputFilesOptions input, ProjectSpaceManager space) {
        return new LcmsAlignSubToolJob(input.msInput.msParserfiles, space);
    }
}

