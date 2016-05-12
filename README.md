mellowd-gradle-plugin
=====================

This plugin integrates **MellowD** compilation right into your gradle build
cycle. **MellowD** is a music description language that is the equivalent
of sheet music in the form of a programming language. You can find out more
information about **MellowD** over at the projects web site by clicking
[here](http://spencerpark.github.io/MellowD/build/docs/docco/).

Applying the plugin
===================
For gradle versions **<2.1** you can add the following to your build script:
```gradle
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.cas.cs4tb3:mellowd-gradle-plugin:1.0.2"
}

apply plugin: "io.github.spencerpark.mellowd"
```
The newer versions (>= 2.1) of gradle can use the following simpler 
notation to apply the plugin:
```gradle
plugins {
  id "io.github.spencerpark.mellowd" version "1.0.2"
}
```

Using the plugin
================
This plugin was designed with java development in mind but works great with
the groovy and scala plugins as well. Simply add a `mellowd` folder to one
of your source sets like you would normally add a `java` or `groovy` folder
where you would put your java or groovy source.

A standard project structure would look like the following in a java project:
```
projectRoot
└───main
    ├───java
    ├───mellowd
    └───resources
```

All of the **MellowD** source files would go into the mellowd folder and from
here all of your sources will be compiled and included in the resources for
the source set that the mellowd source set belongs to. This means that you
can treat the sources as playable music that is ready to go and inside your jar.

Configuring compilation options
===============================

Similar to how you could configure your java source sets you can do the same
with mellowd's source sets including some extra options for compilation arguments.

Configuring a source set would look something like this for the `main` source set:
```gradle
sourceSets {
    main {
        mellowd {
            exclude '**/Unwanted.mlod'
        }
    }
}
```

Actually editing the compilation options are done through the `mellowd`
container extension. 

You can set options at the top level that will cascade
the value down to the following configurations. For example if you would like
all of the configurations to be `.wav` files you can set this at the start.
Note that you must set this before the blocks it will apply to, therefor
also meaning that you can change it lower down and it will only affect the
configurations that follow.

Individual configurations are done inside a container for that source set
and then again inside an extension named the same as the file name in lowercase
without the extension. In the following example the tempo for the file
`Sample1.mlod` in the main source set is set to 180 bpm.

```gradle
mellowd {
    outputType = 'WAV'
    main {
        sample1 {
            tempo = 180
        }
    }
}
```

The following configuration options are available:

| key        | value                                                          | default|
|------------|----------------------------------------------------------------|--------|
| outputType | One of ['MIDI' or 'WAV']                                       | 'MIDI' |
| tempo      | An integer >0 describing the tempo in bpm                      | 120    |
| timeSig    | A tuple describing the time signature [numerator, denominator] | [4, 4] |
| verbose    | A boolean describing if the compiler should print some info    | false  |
