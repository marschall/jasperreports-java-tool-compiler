package com.github.marschall.jasperreports.javatoolcompiler;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRAbstractJavaCompiler;
import net.sf.jasperreports.engine.design.JRClassGenerator;
import net.sf.jasperreports.engine.design.JRCompilationSourceCode;
import net.sf.jasperreports.engine.design.JRCompilationUnit;
import net.sf.jasperreports.engine.design.JRSourceCompileTask;

public final class JRJavaToolCompiler extends JRAbstractJavaCompiler {

  // FIXME #loadClass

  private final JavaCompiler compiler;

  public JRJavaToolCompiler(JasperReportsContext jasperReportsContext) {
    super(jasperReportsContext, false);
    this.compiler = ToolProvider.getSystemJavaCompiler();
  }

  @Override
  protected void checkLanguage(String language) throws JRException {
    if (!JRReport.LANGUAGE_JAVA.equals(language)) {
      throw new JRException(EXCEPTION_MESSAGE_KEY_EXPECTED_JAVA_LANGUAGE,
              new Object[]{language, JRReport.LANGUAGE_JAVA});
    }
  }

  @Override
  protected JRCompilationSourceCode generateSourceCode(JRSourceCompileTask sourceTask) throws JRException {
    return JRClassGenerator.generateClass(sourceTask);
  }

  @Override
  protected String compileUnits(JRCompilationUnit[] units, String classpath, File tempDirFile) throws JRException {

    StandardJavaFileManager standardFileManager = this.compiler.getStandardFileManager(null, null, null);
    Map<String, JRCompilationUnit> unitsByName = Arrays.stream(units)
            .collect(toMap(JRCompilationUnit::getName, Function.identity()));
    JavaFileManager jrFileManager = new JRJavaFileManager(standardFileManager, unitsByName);

    // FIXME could also be a logger
    Writer out = new StringWriter();
    ReportingDiagnosticListener diagnosticListener = new ReportingDiagnosticListener();
    Iterable<String> options = classpath != null ? Arrays.asList("-classpath", classpath) : null;
    Iterable<String> classesToBeProcessed = null;
    Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(unitsByName.values());
    CompilationTask task = this.compiler.getTask(out, jrFileManager, diagnosticListener, options, classesToBeProcessed, null);
    task.call();

    String errors = diagnosticListener.getErrors();
    if (errors.isEmpty()) {
      return null;
    } else {
      return errors;
    }
  }

  @Override
  protected String getSourceFileName(String unitName) {
    return unitName + ".java";
  }

  static final class ReportingDiagnosticListener implements DiagnosticListener<JavaFileObject> {

    private final StringBuilder errors;

    ReportingDiagnosticListener() {
      this.errors = new StringBuilder();
    }

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
      if (diagnostic.getKind() == javax.tools.Diagnostic.Kind.ERROR) {
        this.errors.append(diagnostic.getMessage(null));
      }

    }

    String getErrors() {
      return this.errors.toString();
    }

  }

}
