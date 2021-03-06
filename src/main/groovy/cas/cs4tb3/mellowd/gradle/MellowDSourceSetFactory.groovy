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

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.util.GradleVersion

class MellowDSourceSetFactory {
    private final boolean useFactory;
    private final FileResolver fileResolver
    private final ProjectInternal project

    public MellowDSourceSetFactory(ProjectInternal project) {
        this.fileResolver = project.fileResolver
        this.project = project
        this.useFactory = GradleVersion.current().compareTo(GradleVersion.version("2.12")) >= 0;
    }

    public SourceDirectorySet newSourceSetDirectory(String displayName) {
        if (useFactory) {
            SourceDirectorySetFactory factory = project.getServices().get(SourceDirectorySetFactory.class);
            return factory.create(displayName);
        } else {
            return new DefaultSourceDirectorySet(displayName, fileResolver);
        }
    }
}
