package com.github.marschall.jasperreports.javatoolcompiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.tools.JavaFileObject;

import net.sf.jasperreports.engine.design.JRCompilationUnit;

/**
 * An output {@link JavaFileObject} over a {@link JRCompilationUnit}, saves the output using
 * {@link JRCompilationUnit#setCompileData(java.io.Serializable)}.
 */
final class OutputCompilationUnitJavaFileObject extends AbstractCompilationUnitJavaFileObject {

  OutputCompilationUnitJavaFileObject(JRCompilationUnit jasperCompilationUnit, Kind kind) {
    super(jasperCompilationUnit, kind);
  }

  OutputCompilationUnitJavaFileObject(JRCompilationUnit jasperCompilationUnit) {
    this(jasperCompilationUnit, Kind.CLASS);
  }

  @Override
  public String getName() {
    return this.getCompilationUnitName() + "class";
  }

  @Override
  public InputStream openInputStream() {
    throw new IllegalStateException("not for reading");
  }

  @Override
  public OutputStream openOutputStream() {
    return new CompilationUnitOutputStream(this.jasperCompilationUnit);
  }

  @Override
  public Reader openReader(boolean ignoreEncodingErrors) {
    throw new IllegalStateException("not for reading");
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    throw new IllegalStateException("not for reading");
  }

  @Override
  public Writer openWriter() {
    throw new IllegalStateException("only binary writing supported");
  }

  @Override
  public String toString() {
    return "output for: " + this.getCompilationUnitName() + ".class";
  }

  /**
   * An {@link OutputStream} that calls {@link JRCompilationUnit#setCompileData(java.io.Serializable)} when closed.
   */
  static final class CompilationUnitOutputStream extends OutputStream {

    private final JRCompilationUnit compilationUnit;

    private final ByteArrayOutputStream delegate;

    CompilationUnitOutputStream(JRCompilationUnit compilationUnit) {
      this.compilationUnit = compilationUnit;
      this.delegate = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) {
      this.delegate.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
      this.delegate.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
      this.delegate.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
      this.delegate.flush();
    }

    @Override
    public void close() throws IOException {
      this.delegate.close();
      this.compilationUnit.setCompileData(this.delegate.toByteArray());
    }

  }

}