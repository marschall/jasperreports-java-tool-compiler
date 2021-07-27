JavaTool Compiler for JasperReports  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.marschall/jasperreports-java-tool-compiler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.marschall/jasperreports-java-tool-compiler)
===================================

A compiler for JasperReports that uses the built in [JSR 199](https://jcp.org/en/jsr/detail?id=199) Java Compiler API to compile reports. No need for an external dependency if you deploy on a JDK or have the `java.compiler` module.

The compiler supports reading directly from memory and writing directly to memory without the need for any file system access.

```xml
<dependency>
  <groupId>com.github.marschall</groupId>
  <artifactId>jasperreports-java-tool-compiler</artifactId>
  <version>1.0.0</version>
</dependency>
```

Usage
-----

```java
JasperReportsContext jrContext = new SimpleJasperReportsContext();
// alternatively the deprecated JRCompiler.COMPILER_CLASS works as wll
jrContext.setProperty(JRCompiler.COMPILER_PREFIX + JRReport.LANGUAGE_JAVA, JRJavaToolCompiler.class.getName());
var compileManager = JasperCompileManager.getInstance(jrContext);
```
