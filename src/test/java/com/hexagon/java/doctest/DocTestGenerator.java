package com.hexagon.java.doctest;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Functions;
import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import jdk.jshell.execution.LocalExecutionControlProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

class DocTestGenerator {

    private
    static final String DOCTEST_TAG = "@doctest";

    @TestFactory
    Stream<DynamicTest> generate() {
        File sourcesDir = new File("src/main/java");

        List<Stream<DynamicTest>> allTestCases = new ArrayList<>();

        new DirExplorer((
                (level, path, file) -> path.endsWith(".java")),
                (level, path, file) -> {
                    try {
                        Optional<ClassOrInterfaceDeclaration> compilationUnit = new JavaParser()
                                .parse(file)
                                .getResult()
                                .map(CompilationUnit::getPrimaryType)
                                .flatMap(Functions.identity())
                                .filter(td -> td instanceof ClassOrInterfaceDeclaration)
                                .map(ClassOrInterfaceDeclaration.class::cast);

                        compilationUnit.ifPresent(declaration -> new VoidVisitorAdapter<>() {
                            @Override
                            public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                                super.visit(n, arg);

                                for (MethodDeclaration method : n.getMethods()) {
                                    if (method.getComment().isPresent() && method.getComment().get().isJavadocComment()) {
                                        Stream<DynamicTest> testCases =
                                                handleJavaDoc(n, method.getComment().map(JavadocComment.class::cast).get());
                                        allTestCases.add(testCases);
                                    }
                                }
                            }
                        }.visit(declaration, null));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ).explore(sourcesDir);

        return allTestCases.stream().flatMap(Function.identity());
    }

    private Stream<DynamicTest> handleJavaDoc(ClassOrInterfaceDeclaration n, JavadocComment javadocComment) {
        String content = javadocComment.getContent();
        String tagContent = content.substring(content.indexOf(DOCTEST_TAG));
        String[] lines = tagContent.split("\n");
        return Stream.of(lines)
                //pierwszy wiersz to sam tag wiec go nie potrzebujemy
                .skip(1)
                //usuwamy * ktora zaczyna kazda linie java doc
                .map(l -> l.replaceFirst("\\*", ""))
                //usuwamy biale znaki
                .map(String::strip)
                //usuwamy puste linie
                .filter(s -> !s.isBlank())
                //zmianiamy nazwe tak by zawierala pakietowanie, wymaga tego jshell
                .map(s -> s.replaceAll(n.getName().asString(), n.getFullyQualifiedName().get()))
                //tworzymy wlasciwy test
                .map(this::createTestFromText);
    }

    private DynamicTest createTestFromText(final String line) {
        return DynamicTest.dynamicTest(line, () -> {
            JShell shell =
                    JShell.builder()
                            .executionEngine(new LocalExecutionControlProvider(), null)
                            .build();
            List<SnippetEvent> result = shell.eval(line);
            System.out.println(line);
            Assertions.assertFalse(result.isEmpty());
            SnippetEvent event = result.get(0);
            Assertions.assertEquals(Snippet.Status.VALID, event.status());
            Assertions.assertNull(event.exception());
            Assertions.assertTrue(Boolean.parseBoolean(event.value()));
        });
    }
}
