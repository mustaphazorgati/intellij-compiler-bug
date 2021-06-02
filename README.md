# Java Compiler Bug?
This example project displays a weird behaviour of the java compiler I found.
The reason I have created this repository is to have some reproducible code which can be 
used to identify what's wrong.

## How to reproduce 
1. This repository is an IntelliJ Idea Project. Just open that.
2. Read the main method of the class Main carefully.
3. Uncomment the breaking lines and compile either through maven (see maven wrapper) or in IntelliJ directly.
4. Find out if this breaks on your System ;)

## My Environment

### Java
``` 
openjdk version "11.0.11" 2021-04-20
OpenJDK Runtime Environment (build 11.0.11+9)
OpenJDK 64-Bit Server VM (build 11.0.11+9, mixed mode)
```

### IDE
```
IntelliJ IDEA 2021.1.2 (Ultimate Edition)
Build #IU-211.7442.40, built on June 1, 2021
Licensed to Mustapha Zorgati
Subscription is active until August 18, 2021.
For educational use only.
Runtime version: 11.0.11+9-b1341.57 amd64
VM: Dynamic Code Evolution 64-Bit Server VM by JetBrains s.r.o.
Linux 5.4.118-1-manjaro
GC: ParNew, ConcurrentMarkSweep
Memory: 4029M
Cores: 8
Registry: debugger.watches.in.variables=false
Non-Bundled Plugins: CMD Support (1.0.5), IdeaVIM (0.67), de.ax.powermode (100.001), google-java-format (1.9.0.1), some.awesome (1.14), CheckStyle-IDEA (5.53.0), maven-wrapper-plugin (0.0.1), intellij.prettierJS (211.7142.13), org.jetbrains.kotlin (211-1.5.10-release-891-IJ7142.45), org.asciidoctor.intellij.asciidoc (0.32.51)
Kotlin: 211-1.5.10-release-891-IJ7142.45
Current Desktop: i3
```

### Maven 
I've provided a maven wrapper for this. But Maybe this information is useful for someone?
``` 
Maven home: /home/mustapha/.m2/wrapper/dists/apache-maven-3.6.3-bin/1iopthnavndlasol9gbrbg6bf2/apache-maven-3.6.3
Java version: 11.0.11, vendor: Oracle Corporation, runtime: /usr/lib/jvm/java-11-openjdk
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.4.118-1-manjaro", arch: "amd64", family: "unix"
```