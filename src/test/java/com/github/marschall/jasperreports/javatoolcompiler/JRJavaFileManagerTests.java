package com.github.marschall.jasperreports.javatoolcompiler;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.design.JRCompiler;

class JRJavaFileManagerTests {

  private JasperCompileManager compileManager;

  @BeforeEach
  void setUp() {
    JasperReportsContext jrContext = new SimpleJasperReportsContext();
    jrContext.setProperty(JRCompiler.COMPILER_PREFIX + JRReport.LANGUAGE_JAVA, JRJavaToolCompiler.class.getName());
    this.compileManager = JasperCompileManager.getInstance(jrContext);
  }

  static List<Path> testReports() throws IOException {
    Path reportsFolder = Paths.get("src", "test", "jasperreports");
    try (Stream<Path> paths = Files.find(reportsFolder, 3, (path, attributes) -> {
      return attributes.isRegularFile() && path.getFileName().toString().endsWith(".jrxml");
    })) {
      return paths.collect(toList());
    }
  }

  @ParameterizedTest
  @MethodSource("testReports")
  void compileReport(Path reportFile) throws IOException, JRException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(reportFile))) {
      this.compileManager.compileToStream(inputStream, outputStream);
    }
    byte[] byteCode = outputStream.toByteArray();
    assertNotNull(byteCode);
    assertTrue(byteCode.length > 0);
  }

}
