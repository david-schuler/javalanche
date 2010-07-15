package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Document;

import de.unisb.cs.st.ds.util.io.DirectoryFileSource;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class SourceScanner {

	private static final String AST_RESULT_FILENAME = "ast-result.xml";


	public static Map<String, ASTParseResult> parseDirectory(File baseDir) {
		Set<File> files = getFiles(baseDir);
		Map<String, ASTParseResult> results = new HashMap<String, ASTParseResult>();
		for (File file : files) {
			Map<String, ASTParseResult> result = analyzeJavaFile(file);
			results.putAll(result);
		}
		return results;
	}

	public static Map<String, ASTParseResult> analyzeJavaFile(File f) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable options = JavaCore.getOptions();
		options.put("org.eclipse.jdt.core.compiler.source", "1.6");
		parser.setCompilerOptions(options);
		String source = getSource(f);
		parser.setSource(source.toCharArray());
		parser.setResolveBindings(false);
		final CompilationUnit compU = (CompilationUnit) parser
				.createAST((IProgressMonitor) null);
		final Document doc = new Document(source);
		MutationASTTypeVisitor visitor = new MutationASTTypeVisitor(doc, f
				.getName());
		compU.accept(visitor);
		Map<String, ASTParseResult> parseResults = visitor.getParseResults();
		return parseResults;
	}

	private static String getSource(File file) {
		try {
			if (!file.exists()) {
				throw new RuntimeException("" + file);
			}
			String content = FileUtils.readFileToString(file);
			return content;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Set<File> getFiles(File baseDir) {
		Set<File> result = new HashSet<File>();
		try {
			result.addAll(DirectoryFileSource.getFilesByExtension(baseDir,
					"java"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static void parseAndSave(File baseDir) {
		Map<String, ASTParseResult> result = parseDirectory(baseDir);
		File outDir = new File(OUTPUT_DIR);
		XmlIo.toXML(result, new File(outDir, AST_RESULT_FILENAME));
	}

	public static Map<String, ASTParseResult> read() {
		File outDir = new File(OUTPUT_DIR);
		Object fromXml = XmlIo.fromXml(new File(outDir, AST_RESULT_FILENAME));
		return (Map<String, ASTParseResult>) fromXml;
	}
}
