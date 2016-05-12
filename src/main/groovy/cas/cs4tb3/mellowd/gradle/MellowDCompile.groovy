/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Spencer Park
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cas.cs4tb3.mellowd.gradle

import cas.cs4tb3.mellowd.compiler.Compiler
import cas.cs4tb3.mellowd.compiler.MIDIIODelegate
import cas.cs4tb3.mellowd.compiler.SequenceIODelegate
import cas.cs4tb3.mellowd.compiler.WavIODelegate
import groovy.io.FileType
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ConfigureUtil

import javax.inject.Inject

class MellowDCompile extends SourceTask {
    //@OutputDirectory <- don't use this as we want to preform our own out-of-date checks
    File destinationDir

    @Inject
    NamedDomainObjectContainer<MellowDCompilationExtension> configurations
    @Inject
    MellowDCompilationExtension defaultConfig

    @Inject
    MellowDMetaData metaData
    @Inject
    String srcName

    protected void setSrcName(String srcName) {
        this.srcName = srcName
    }

    protected void setDestinationDir(File destinationDir) {
        this.destinationDir = destinationDir
    }

    protected void setConfigurations(NamedDomainObjectContainer<MellowDCompilationExtension> configurations) {
        this.configurations = configurations
    }

    protected void setDefaultConfig(MellowDCompilationExtension defaultConfig) {
        this.defaultConfig = defaultConfig
    }

    protected void setMetaData(MellowDMetaData metaData) {
        this.metaData = metaData
    }

    @TaskAction
    void process() {
        def srcData = metaData.getMetaForSourceSet(srcName)
        boolean allOutDated = srcData == null
        if (allOutDated) srcData = new LinkedHashMap()

        getSource().each { sourceFile ->
            //Try to resolve a configuration for the sourceFile with and without the extension defaulting to the default config
            String lookupKey = sourceFile.name.toLowerCase().replace('.mlod', '')
            MellowDCompilationExtension specConfig = configurations.findByName(lookupKey)
            //If there is no custom config use the default one
            if (specConfig == null) {
                specConfig = new MellowDCompilationExtension(lookupKey, defaultConfig)
            }

            //From the config select the appropriate io delegate
            SequenceIODelegate ioDelegate;
            switch (specConfig.outputType) {
                case OutputType.MIDI:
                    ioDelegate = MIDIIODelegate.getInstance();
                    break;
                case OutputType.WAV:
                    ioDelegate = WavIODelegate.getInstance();
                    break;
                default:
                    return;
            }

            File outputFile = new File(destinationDir, sourceFile.name.replace(".mlod", ioDelegate.extension))

            //We will preform a custom 'out of date' check
            //Compilation can be skipped if the output was last modified after the source
            boolean outputNewerThanSrc = outputFile.lastModified() > sourceFile.lastModified()

            //Compilation can only be skipped if the following hold:
            //   - some things may be outdated
            //   - there have been NO changes to the source since last compiling the output
            //   - there is a meta value for this source and it is the same as it's current config
            if (!allOutDated
                    && outputNewerThanSrc
                    && srcData."$lookupKey"
                    && !configOutDated(srcData."$lookupKey", specConfig)) {
                return //We can skip this one
            }

            //We didn't skip this source so save the config to the meta data
            srcData."$lookupKey" = specConfig

            //Actually do the compilation
            javax.sound.midi.Sequence compiledSource = Compiler.compile(sourceFile,
                    specConfig.timeSig.first.byteValue(), specConfig.timeSig.second.byteValue(),
                    specConfig.tempo,
                    specConfig.verbose);

            //Save the file to the output location
            ioDelegate.save(compiledSource, outputFile)
        }

        //Clean up removed sources
        destinationDir.eachFileRecurse(FileType.FILES) { compiledSrc ->
            //Check that the compiled source has a filetype that matches the configuration
            MellowDCompilationExtension specConfig = configurations.findByName(compiledSrc.name.toLowerCase().replace('.mid', '').replace('.wav', ''))
            if (specConfig == null)
                specConfig = defaultConfig
            //If it dosn't match the configuration we can delete it
            if (!compiledSrc.name.toLowerCase().endsWith(specConfig.outputType.extension)) {
                compiledSrc.delete()
                return
            }

            def srcName = compiledSrc.name.replaceAll(~/\.[^.]*$/, '')
            //If any of the source files match the name of the compiled source than the
            //compilation result can remain, otherwise we will clean it up
            boolean srcExists = getSource().any { srcFile ->
                if (srcFile.name.replaceFirst(~/\.mlod$/, '') == srcName) {
                    return true
                }
                return false
            }

            //No source file for the compilation result anymore so we will delete it
            if (!srcExists)
                compiledSrc.delete()
        }

        //Clean up any meta data entries for non-existent sources
        def iterator = srcData.entrySet().iterator()

        while (iterator.hasNext()) {
            String key = iterator.next().key
            boolean srcExists = getSource().any { srcFile ->
                srcFile.name.equalsIgnoreCase(key + ".mlod")
            }

            //If the src doesn't exist anymore then delete it's metadata
            if (!srcExists) {
                iterator.remove()
            }
        }

        //Write the meta data changes to file
        metaData.setMetaForSourceSet(srcName, srcData)
    }

    protected static boolean configOutDated(Object metaData, MellowDCompilationExtension srcConfig) {
        return !(metaData.timeSig == srcConfig.timeSig
                && metaData.tempo == srcConfig.tempo
                && metaData.outputType == srcConfig.outputType.name())
    }

    public MellowDCompile mellowd(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, defaultConfig);
        return this;
    }
}