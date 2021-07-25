package com.github.marschall.jasperreports.javatoolcompiler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import net.sf.jasperreports.engine.design.JRCompilationUnit;

final class JRJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

  private final  JavaFileManager delegate;
  private final Map<String, JRCompilationUnit> unitsByName;

  JRJavaFileManager(JavaFileManager delegate, Map<String, JRCompilationUnit> unitsByName) {
    super(delegate);
    this.delegate = delegate;
    this.unitsByName = unitsByName;
  }

  @Override
  public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
    // FIXME
    return this.delegate.list(location, packageName, kinds, recurse);
  }

  @Override
  public String inferBinaryName(Location location, JavaFileObject file) {
    // FIXME
    return this.delegate.inferBinaryName(location, file);
  }

  @Override
  public boolean isSameFile(FileObject a, FileObject b) {
    // FIXME
    return this.delegate.isSameFile(a, b);
  }

  @Override
  public boolean hasLocation(Location location) {
    if ((location == StandardLocation.SOURCE_PATH) || (location == StandardLocation.CLASS_OUTPUT)) {
      return true;
    }
    return this.delegate.hasLocation(location);
  }

  @Override
  public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
    if (location == StandardLocation.SOURCE_PATH) {
      if (kind == Kind.SOURCE) {
        JRCompilationUnit compilationUnit = this.unitsByName.get(className);
        if (compilationUnit != null) {
          return new JavaFileObjectInputAdapter(compilationUnit, kind);
        }
      }
      return null;
    }
    return this.delegate.getJavaFileForInput(location, className, kind);
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
    if (location == StandardLocation.CLASS_OUTPUT) {
      if (kind == Kind.CLASS) {
        JRCompilationUnit compilationUnit = this.unitsByName.get(className);
        if (compilationUnit != null) {
          return new JavaFileObjectOutputAdapter(compilationUnit, kind);
        }
      }
      return null;
    }
    return this.delegate.getJavaFileForOutput(location, className, kind, sibling);
  }

  @Override
  public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
    // FIXME
    return this.delegate.getFileForInput(location, packageName, relativeName);
  }

  @Override
  public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
    // FIXME
    return this.delegate.getFileForOutput(location, packageName, relativeName, sibling);
  }

  @Override
  public boolean contains(Location location, FileObject fo) throws IOException {
    // FIXME
    return this.delegate.contains(location, fo);
  }

}
