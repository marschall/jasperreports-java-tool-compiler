package com.github.marschall.jasperreports.javatoolcompiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
  public InputStream openInputStream() throws IOException {
    // FIXME
    return new ByteArrayInputStream(this.jasperCompilationUnit.getSourceCode().getBytes());
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    throw new IllegalStateException("not for writing");
  }

  @Override
  public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
    return new StringReader(this.jasperCompilationUnit.getSourceCode());
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    return this.jasperCompilationUnit.getSourceCode();
  }

  @Override
  public Writer openWriter() throws IOException {
    throw new IllegalStateException("not for writing");
  }

  @Override
  public String toString() {
    return "input for: " + this.getCompilationUnitName() + ".java";
  }

}