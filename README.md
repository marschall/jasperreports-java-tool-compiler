JavaTool Compiler for JasperReports
===================================

A compiler for JasperReports that uses the built in [JSR 199](https://jcp.org/en/jsr/detail?id=199) Java Compiler API to compile reports. No need for an external dependency.

Usage
-----

```java
JasperReportsContext jrContext = new SimpleJasperReportsContext();
// alternatively the deprecated JRCompiler.COMPILER_CLASS works as wll
jrContext.setProperty(JRCompiler.COMPILER_PREFIX + "java", JRJavaToolCompiler.class.getName());
var compileManager = JasperCompileManager.getInstance(jrContext);
```
