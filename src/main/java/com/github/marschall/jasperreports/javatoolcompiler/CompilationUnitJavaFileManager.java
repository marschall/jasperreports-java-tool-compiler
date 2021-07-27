package com.github.marschall.jasperreports.javatoolcompiler;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import net.sf.jasperreports.engine.design.JRCompilationUnit;

/**
 * A file manager that delegate to a {@link JRCompilationUnit} for reading sources and writing classes.
 * All other operations a delegate the a default {@link JavaFileManager}.
 */
final class CompilationUnitJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

  private final Map<String, JRCompilationUnit> unitsByName;

  CompilationUnitJavaFileManager(JavaFileManager delegate, Map<String, JRCompilationUnit> unitsByName) {
    super(delegate);
    this.unitsByName = unitsByName;
  }

  @Override
  public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
    List<JavaFileObject> ownFiles;
    if (isSourcePath(location) && kinds.contains(Kind.SOURCE)) {
      //@formatter:off
      ownFiles = this.unitsByName.values().stream()
                                          .map(JavaFileObjectInputAdapter::new)
                                          .collect(toList());
      //@formatter:on
    } else if (isClassOutput(location) && kinds.contains(Kind.CLASS)) {
      //@formatter:off
        ownFiles = this.unitsByName.values().stream()
                                            .map(JavaFileObjectOutputAdapter::new)
                                            .collect(toList());
        //@formatter:on
    } else {
      ownFiles = List.of();
    }
    Iterable<JavaFileObject> delegateFiles = super.list(location, packageName, kinds, recurse);
    if (ownFiles.isEmpty()) {
      return delegateFiles;
    } else {
      List<JavaFileObject> merged = new ArrayList<>(ownFiles);
      for (JavaFileObject toAdd : delegateFiles) {
        merged.add(toAdd);
      }
      return merged;
    }
  }

  @Override
  public String inferBinaryName(Location location, JavaFileObject file) {
    if (this.isSourcePathOrClassOutput(location)) {
      if (file instanceof AbstractJRJavaFileObject) {
        AbstractJRJavaFileObject jrFileObject = (AbstractJRJavaFileObject) file;
        return jrFileObject.getCompilationUnitName();
      }
    }
    return super.inferBinaryName(location, file);
  }

  @Override
  public boolean isSameFile(FileObject a, FileObject b) {
    if (a instanceof AbstractJRJavaFileObject) {
      if (!(b instanceof AbstractJRJavaFileObject)) {
        return false;
      }
      AbstractJRJavaFileObject first = (AbstractJRJavaFileObject) a;
      AbstractJRJavaFileObject second = (AbstractJRJavaFileObject) b;
      return (first.getKind() == second.getKind())
                && first.getCompilationUnitName().equals(second.getCompilationUnitName());
    } else if (b instanceof AbstractJRJavaFileObject) {
      return false;
    }
    return super.isSameFile(a, b);
  }

  @Override
  public boolean hasLocation(Location location) {
    if (this.isSourcePathOrClassOutput(location)) {
      return true;
    }
    return super.hasLocation(location);
  }

  @Override
  public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
    if (isSourcePath(location)) {
      if (kind == Kind.SOURCE) {
        JRCompilationUnit compilationUnit = this.unitsByName.get(className);
        if (compilationUnit != null) {
          return new JavaFileObjectInputAdapter(compilationUnit, kind);
        }
      }
    }
    return super.getJavaFileForInput(location, className, kind);
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
    if (isClassOutput(location)) {
      if (kind == Kind.CLASS) {
        JRCompilationUnit compilationUnit = this.unitsByName.get(className);
        if (compilationUnit != null) {
          return new JavaFileObjectOutputAdapter(compilationUnit, kind);
        }
      }
    }
    return super.getJavaFileForOutput(location, className, kind, sibling);
  }

  @Override
  public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
    if (isSourcePath(location) && packageName.equals("")) {
      JRCompilationUnit compilationUnit = this.unitsByName.get(relativeName);
      if (compilationUnit != null) {
        return new JavaFileObjectInputAdapter(compilationUnit);
      }
    }
    return super.getFileForInput(location, packageName, relativeName);
  }

  @Override
  public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
    if (isClassOutput(location) && packageName.equals("")) {
      JRCompilationUnit compilationUnit = this.unitsByName.get(relativeName);
      if (compilationUnit != null) {
        return new JavaFileObjectOutputAdapter(compilationUnit);
      }
    }
    return super.getFileForOutput(location, packageName, relativeName, sibling);
  }

  @Override
  public boolean contains(Location location, FileObject file) throws IOException {
    if (this.isSourcePathOrClassOutput(location)) {
      if (file instanceof AbstractJRJavaFileObject) {
        AbstractJRJavaFileObject jrFileObject = (AbstractJRJavaFileObject) file;
        if (this.unitsByName.containsKey(jrFileObject.getCompilationUnitName())) {
          return true;
        }
      }
    }
    return super.contains(location, file);
  }

  private boolean isSourcePathOrClassOutput(Location location) {
    return isSourcePath(location) || isClassOutput(location);
  }

  private static boolean isClassOutput(Location location) {
    return location == StandardLocation.CLASS_OUTPUT;
  }

  private static boolean isSourcePath(Location location) {
    return location == StandardLocation.SOURCE_PATH;
  }

}
