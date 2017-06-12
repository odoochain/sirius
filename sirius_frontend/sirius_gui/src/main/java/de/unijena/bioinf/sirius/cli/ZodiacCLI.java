package de.unijena.bioinf.sirius.cli;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.lexicalscope.jewel.cli.InvalidOptionSpecificationException;
import de.unijena.bioinf.sirius.cli.CLI;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by ge28quv on 23/05/17.
 */
public class ZodiacCLI extends FingeridApplication {
//todo another one to override?????

    private ZodiacOptions zodiacOptions;
//--zodiac --spectral-hits /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/cleanHitsTable.csv -o /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/gibbsOutput.csv --iterations 200000 --burn-in 20000 --thresholdFilter 0.95 --distribution exponential --sirius /home/ge28quv/Downloads/sirius3-linux64-3.4.1/bin/newSirius/part.workspace
    @Override
    public void compute() {
        if (options.isZodiac()){
            Zodiac zodiac = new Zodiac(zodiacOptions);
            zodiac.run();
        } else {
            super.compute();
        }

    }

    @Override
    public void parseArgs(String[] args) {
        super.parseArgs(args);
    }

    @Override
    protected void parseArgsAndInit(String[] args) {
        super.parseArgsAndInit(args);
    }

    @Override
    public void parseArgs(String[] args, Class<FingerIdOptions> optionsClass) {
        try {
            if (isZodiac(args)) {
                if (args.length==1){
                    System.out.println(CliFactory.createCli(ZodiacOptions.class).getHelpMessage());
                    System.exit(0);
                }
                zodiacOptions = CliFactory.createCli(ZodiacOptions.class).parseArguments(Arrays.copyOfRange(args, 1, args.length));
                super.parseArgs(new String[]{"--zodiac"}, optionsClass);
            } else {
                super.parseArgs(args, optionsClass);
            }
        } catch (HelpRequestedException e) {
            super.parseArgs(args, optionsClass);
        }
    }


    private boolean isZodiac(String[] args){
        for (String arg : args) {
            if (arg.toLowerCase().equals("--zodiac")) return true;
        }
        return false;
    }


    @Override
    public void setup() {
        if (!isZodiac()) super.setup();
        Path output = Paths.get(zodiacOptions.getOutputPath());
        if (!Files.exists(output)){
            try {
                Files.createDirectories(output);
            } catch (IOException e) {
                LoggerFactory.getLogger(this.getClass()).error("Cannot create output directory: "+e.getMessage());
            }
        }
    }

    @Override
    public void validate() {
        if (!isZodiac()) super.validate();
        Path output = Paths.get(zodiacOptions.getOutputPath());
        if (!Files.isDirectory(output) && Files.exists(output)){
            LoggerFactory.getLogger(this.getClass()).error("the output must be a directory or non-existing.");
            System.exit(1);
        }
    }

    private boolean isZodiac(){
        return zodiacOptions!=null;
    }
}
