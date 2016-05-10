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

class MellowDCompilationExtension {
    final String name

    OutputType outputType = OutputType.MIDI
    Tuple2<Byte, Byte> timeSig = new Tuple2<>((byte) 4, (byte) 4)
    int tempo = 120
    boolean verbose = false

    MellowDCompilationExtension(String name) {
        this.name = name
    }

    MellowDCompilationExtension(String name, MellowDCompilationExtension copy) {
        this.name = name
        this.outputType = copy.outputType
        this.timeSig = copy.timeSig
        this.tempo = copy.tempo
        this.verbose = copy.verbose
    }
}
