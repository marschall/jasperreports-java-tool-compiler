package com.github.marschall.jasperreports.javatoolcompiler;

import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

import net.sf.jasperreports.engine.design.JRCompilationUnit;

/**
 * Abstract base class for a {@link JavaFileObject} over a {@link JRCompilationUnit}.
 */
abstract class AbstractJRJavaFileObject implements JavaFileObject {

  protected final JRCompilationUnit jasperCompilationUnit;
  protected final Kind kind;

  AbstractJRJavaFileObject(JRCompilationUnit jasperCompilationUnit, Kind kind) {
    this.jasperCompilationUnit = jasperCompilationUnit;
    this.kind = kind;
  }

  @Override
  public URI toUri() {
    return URI.create("jasper://" + this.getCompilationUnitName());
  }

  String getCompilationUnitName() {
    return this.jasperCompilationUnit.getName();
  }

  @Override
  public long getLastModified() {
    return 0;
  }

  @Override
  public boolean delete() {
    return false;
  }

  @Override
  public Kind getKind() {
    return this.kind;
  }

  @Override
  public boolean isNameCompatible(String simpleName, Kind kind) {
    return (this.kind == kind) && this.getCompilationUnitName().equals(simpleName);
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