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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention

class MellowDPlugin implements Plugin<Project> {
    private Project project
    private MellowDMetaData metaData

    @Override
    void apply(Project project) {
        this.project = project
        this.metaData = new MellowDMetaData(new File("${project.buildDir}/mellowd/meta.json"))
        //We want the concept of source sets introduced with the lowest level of the standard plugins
        //TODO have different ways of adding source sets based on the other plugins present
        project.getPluginManager().apply(JavaBasePlugin)
        JavaPluginConvention jvc = project.getConvention().getPlugin(JavaPluginConvention)
        configureSourceSetDefaults(jvc);
    }

    private void configureSourceSetDefaults(JavaPluginConvention pluginConvention) {
        MellowDSourceSetFactory factory = new MellowDSourceSetFactory((ProjectInternal) project)
        project.extensions.create('mellowd', MellowDCompilationExtension, 'mellowd')
        def extFactory = new MellowDCompilationExtensionFactory(project)
        def defaultConfig = new MellowDCompilationExtension('default')

        pluginConvention.getSourceSets().all { srcSet ->
            MellowDSourceSet mellowdSrc = new MellowDDefaultSourceSet(((DefaultSourceSet) srcSet).getDisplayName(), factory)
            new DslObject(srcSet).getConvention().getPlugins().put("mellowd", mellowdSrc)

            final String defaultSourcePath = "src/${srcSet.name}/mellowd"
            mellowdSrc.getMellowDSrc().srcDir(defaultSourcePath)

            String compileTaskName = srcSet.getTaskName("compile", "MellowD")
            MellowDCompile compileTask = project.getTasks().create(compileTaskName, MellowDCompile)
            compileTask.setDescription("Compiles the ${srcSet.name} MellowD sources.")

            //Setup some an extension container so that each source file may be configured
            //to compile with specific parameters
            def configs = project.container(MellowDCompilationExtension, extFactory)
            project.mellowd.extensions.add(srcSet.name, configs)
            compileTask.configurations = configs
            compileTask.defaultConfig = defaultConfig
            compileTask.metaData = this.metaData
            compileTask.srcName = srcSet.name

            //Set the source set that the compile task is responsible for
            compileTask.setSource(mellowdSrc.getMellowDSrc())

            //Put the compiled songs into a mellowd folder in the build directory
            File destinationDir = new File("${project.buildDir}/mellowd/${srcSet.name}")
            compileTask.destinationDir = destinationDir
            //Include the compilation output in the source set's resources
            srcSet.resources.srcDir(destinationDir)

            //Make sure we compile the sources into the resources before the resources are processed
            project.getTasks().getByName(srcSet.getProcessResourcesTaskName()).dependsOn(compileTaskName);
        }
    }
}
