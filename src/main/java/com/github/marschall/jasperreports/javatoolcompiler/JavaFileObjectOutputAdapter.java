package com.github.marschall.jasperreports.javatoolcompiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

import net.sf.jasperreports.engine.design.JRCompilationUnit;

final class JavaFileObjectOutputAdapter implements JavaFileObject {

  private final JRCompilationUnit jasperCompilationUnit;
  private final Kind kind;

  JavaFileObjectOutputAdapter(JRCompilationUnit jasperCompilationUnit, Kind kind) {
    this.jasperCompilationUnit = jasperCompilationUnit;
    this.kind = kind;
  }

  @Override
  public URI toUri() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return this.jasperCompilationUnit.getName();
  }

  @Override
  public InputStream openInputStream() throws IOException {
    throw new IllegalStateException("not for reading");
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
    throw new IllegalStateException("not for reading");
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    throw new IllegalStateException("not for reading");
  }

  @Override
  public Writer openWriter() throws IOException {
    throw new IllegalStateException("only binary writing supported");
  }

  @Override
  public long getLastModified() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean delete() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Kind getKind() {
    return this.kind;
  }

  @Override
  public boolean isNameCompatible(String simpleName, Kind kind) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public NestingKind getNestingKind() {
    return NestingKind.TOP_LEVEL;
  }

  @Override
  public Modifier getAccessLevel() {
    return Modifier.PUBLIC;
  }

}