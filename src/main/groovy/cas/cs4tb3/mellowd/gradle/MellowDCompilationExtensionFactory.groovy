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

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
/**
 * Created on 2016-05-09.
 */
class MellowDCompilationExtensionFactory implements NamedDomainObjectFactory<MellowDCompilationExtension> {
    private Project project;

    MellowDCompilationExtensionFactory(Project project) {
        this.project = project
    }

    @Override
    MellowDCompilationExtension create(String name) {
        MellowDCompilationExtension extension = new MellowDCompilationExtension(name)
        //Set all of the values to the values described in the root
        extension.outputType = project.mellowd.outputType
        extension.timeSig = project.mellowd.timeSig
        extension.tempo = project.mellowd.tempo
        extension.verbose = project.mellowd.verbose
        return extension
    }
}
