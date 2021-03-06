package com.njlabs.showjava.processor;

import com.crashlytics.android.Crashlytics;
import com.njlabs.showjava.Constants;
import com.njlabs.showjava.utils.SourceInfo;
import com.njlabs.showjava.utils.logging.Ln;

import org.benf.cfr.reader.Main;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;

import java.io.File;

/**
 * Created by Niranjan on 29-05-2015.
 */
public class JavaExtractor extends ProcessServiceHelper {

    public JavaExtractor(ProcessService processService) {
        this.processService = processService;
        this.UIHandler = processService.UIHandler;
        this.packageFilePath = processService.packageFilePath;
        this.packageName = processService.packageName;
        this.exceptionHandler = processService.exceptionHandler;
        this.sourceOutputDir = processService.sourceOutputDir;
        this.javaSourceOutputDir = processService.javaSourceOutputDir;
    }

    public void extract(){

        broadcastStatus("jar2java");
        File JarInput;

        Ln.d("jar location:" + sourceOutputDir + "/" + packageName +".jar");
        JarInput = new File(sourceOutputDir+"/"+ packageName +".jar");
        final File JavaOutputDir = new File(javaSourceOutputDir);

        if (!JavaOutputDir.isDirectory()) {
            JavaOutputDir.mkdirs();
        }

        processService.javaSourceOutputDir = JavaOutputDir.toString();
        String[] args = { JarInput.toString(), "--outputdir", JavaOutputDir.toString() };
        GetOptParser getOptParser = new GetOptParser();

        Options options = null;
        try {
            options = getOptParser.parse(args, OptionsImpl.getFactory());
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }

        final DCCommonState dcCommonState = new DCCommonState(options);
        final String path = options != null ? options.getFileName() : null;

        ThreadGroup group = new ThreadGroup("Jar 2 Java Group");
        Thread javaExtractionThread = new Thread(group, new Runnable(){
            @Override
            public void run(){
                try {
                    Main.doJar(dcCommonState, path);
                    startXMLExtractor();
                }
                catch(Exception | StackOverflowError e) {
                    Ln.e(e);
                    processService.publishProgress("start_activity_with_error");
                }
            }
        }, "Jar to Java Thread", Constants.STACK_SIZE);

        javaExtractionThread.setPriority(Thread.MAX_PRIORITY);
        javaExtractionThread.setUncaughtExceptionHandler(exceptionHandler);
        javaExtractionThread.start();
    }

    private void startXMLExtractor(){
        SourceInfo.setjavaSourceStatus(processService,true);
        ((new XmlExtractor(processService))).extract();
    }
}
