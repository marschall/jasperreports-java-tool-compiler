package com.github.marschall.jasperreports.javatoolcompiler;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRAbstractJavaCompiler;
import net.sf.jasperreports.engine.design.JRClassGenerator;
import net.sf.jasperreports.engine.design.JRCompilationSourceCode;
import net.sf.jasperreports.engine.design.JRCompilationUnit;
import net.sf.jasperreports.engine.design.JRSourceCompileTask;

/**
 * A compiler for that uses the built in {@link JavaCompiler} to compile reports.
 */
public final class JRJavaToolCompiler extends JRAbstractJavaCompiler {

  private static final Log LOG = LogFactory.getLog(MethodHandles.lookup().lookupClass());

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

    JavaFileManager standardFileManager = this.compiler.getStandardFileManager(null, null, null);
    //@formatter:off
    Map<String, JRCompilationUnit> unitsByName = Arrays.stream(units)
                                                       .collect(toMap(JRCompilationUnit::getName, identity()));
    //@formatter:on
    JavaFileManager jrFileManager = new CompilationUnitJavaFileManager(standardFileManager, unitsByName);

    Writer out = new LoggingWriter(LOG);
    ReportingDiagnosticListener diagnosticListener = new ReportingDiagnosticListener();
    Iterable<String> options = classpath != null ? Arrays.asList("-classpath", classpath) : null;
    Iterable<String> classesToBeProcessed = null;
    //@formatter:off
    Iterable<? extends JavaFileObject> compilationUnits = unitsByName.values().stream()
                                                                              .map(JavaFileObjectInputAdapter::new)
                                                                              .collect(Collectors.toList());
    //@formatter:on
    CompilationTask task = this.compiler.getTask(out, jrFileManager, diagnosticListener, options, classesToBeProcessed, compilationUnits);
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

  /**
   * A {@link Writer} that writes to a {@link Log}.
   */
  static final class LoggingWriter extends Writer {

    private final Log log;

    LoggingWriter(Log log) {
      this.log = log;
    }

    @Override
    public void write(char[] cbuf) {
      this.log.error(new String(cbuf));
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
      this.log.error(new String(cbuf, off, len));
    }

    @Override
    public void write(String str) {
      this.log.error(str);
    }

    @Override
    public void write(String str, int off, int len) {
      this.log.error(str.substring(off, off + len));
    }

    @Override
    public Writer append(CharSequence csq) {
      this.log.error(csq);
      return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) {
      this.log.error(csq.subSequence(start, end));
      return this;
    }

    @Override
    public Writer append(char c) {
      this.log.error(c);
      return this;
    }

    @Override
    public void flush() {
      // ignore
    }

    @Override
    public void close() {
      // ignore
    }

  }

}
