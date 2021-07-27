package com.github.marschall.jasperreports.javatoolcompiler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.tools.JavaFileObject;

import net.sf.jasperreports.engine.design.JRCompilationUnit;

/**
 * An input {@link JavaFileObject} over a {@link JRCompilationUnit}.
 */
final class JavaFileObjectInputAdapter extends AbstractJRJavaFileObject {

  JavaFileObjectInputAdapter(JRCompilationUnit jasperCompilationUnit, Kind kind) {
    super(jasperCompilationUnit, kind);
  }

  JavaFileObjectInputAdapter(JRCompilationUnit jasperCompilationUnit) {
    this(jasperCompilationUnit, Kind.SOURCE);
  }

  @Override
  public String getName() {
    return this.getCompilationUnitName() + ".java";
  }

  @Override
  public InputStream openInputStream() {
    // not optimized but never clalled
    return new ByteArrayInputStream(this.jasperCompilationUnit.getSourceCode().getBytes());
  }

  @Override
  public OutputStream openOutputStream() {
    throw new IllegalStateException("not for writing");
  }

  @Override
  public Reader openReader(boolean ignoreEncodingErrors) {
    return new StringReader(this.jasperCompilationUnit.getSourceCode());
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return this.jasperCompilationUnit.getSourceCode();
  }

  @Override
  public Writer openWriter() {
    throw new IllegalStateException("not for writing");
  }

  @Override
  public String toString() {
    return "input for: " + this.getCompilationUnitName() + ".java";
  }

}