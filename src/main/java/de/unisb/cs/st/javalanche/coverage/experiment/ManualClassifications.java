/*
* Copyright (C) 2011 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.coverage.experiment;

import java.util.HashMap;
import java.util.Map;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

public class ManualClassifications {

	
	static enum Project {
		ASPECTJ("org.aspectj.ajdt", aspectj), JTOPAS("de.susebox", jtopas), JODA(
				"org.joda", joda), BARBECUE(
				"net.sourceforge.barbecue", barbecue), XSTREAM(
				"xcom.thoughtworks.xstream.", xstream), COMMONS(
				"xorg.apache.commons.lang", commons), JAXEN("org.jaxen", jaxen); 
		

		private final String prefix;
		private final Map<Mutation, Boolean> results;

		private Project(String prefix, Map<Mutation, Boolean> results) {
			this.prefix = prefix;
			this.results = results;
		}

		public String getPrefix() {
			return prefix;
		}

		public Map<Mutation, Boolean> getResults() {
			return results;
		}
		
		
	}
	
	static Map<Mutation, Boolean> aspectj = new HashMap<Mutation, Boolean>();
	static {
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.core.builder.AsmHierarchyBuilder",
		// 447, 0, RIC_MINUS_1, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.compiler.WeaverMessageHandler", 202,
		// 1, NEGATE_JUMP, false), false);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.compiler.ast.AstUtil", 224, 0,
		// RIC_MINUS_1, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.core.builder.EclipseAdapterUtils",
		// 72, 3, RIC_ZERO, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.compiler.problem.AjProblemReporter",
		// 186, 0, NEGATE_JUMP, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.core.builder.AjCompilerOptions",
		// 292, 0, NEGATE_JUMP, false), true);
		// aspectj
		// .put(
		// new Mutation(
		// "org.aspectj.ajdt.internal.compiler.lookup.HelperInterfaceBinding",
		// 78, 2, REMOVE_CALL, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.compiler.ast.PointcutDeclaration",
		// 88, 0, RIC_ZERO, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.core.builder.AjBuildManager", 981,
		// 0, REMOVE_CALL, false), true);
		// aspectj
		// .put(
		// new Mutation(
		// "org.aspectj.ajdt.internal.compiler.ast.InterTypeConstructorDeclaration",
		// 228, 1, RIC_PLUS_1, false), false);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.compiler.ast.IfPseudoToken", 59, 0,
		// NEGATE_JUMP, false), true);
		// aspectj
		// .put(
		// new Mutation(
		// "org.aspectj.ajdt.internal.compiler.AjPipeliningCompilerAdapter",
		// 184, 0, NEGATE_JUMP, false), true);
		// aspectj.put(new Mutation("org.aspectj.ajdt.ajc.AjdtCommand", 112, 2,
		// REMOVE_CALL, false), false);
		// aspectj
		// .put(
		// new Mutation(
		// "org.aspectj.ajdt.internal.compiler.ast.AjConstructorDeclaration",
		// 47, 0, RIC_PLUS_1, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.core.builder.AjState", 929, 0,
		// REMOVE_CALL, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.compiler.ast.AspectClinit", 54, 0,
		// REMOVE_CALL, false), false);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.compiler.ast.AdviceDeclaration",
		// 123, 0, RIC_PLUS_1, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.compiler.lookup.EclipseScope", 208,
		// 5, REMOVE_CALL, false), true);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.core.builder.AjBuildConfig", 64, 0,
		// RIC_MINUS_1, false), true);
		// aspectj.put(new Mutation("org.aspectj.ajdt.ajc.BuildArgParser", 145,
		// 0,
		// RIC_PLUS_1, false), false);
		// aspectj.put(new Mutation(
		// "org.aspectj.ajdt.internal.compiler.InterimCompilationResult",
		// 43, 1, REMOVE_CALL, false), true);

	}
	static Map<Mutation, Boolean> jtopas = new HashMap<Mutation, Boolean>();
	
	static {
		// jtopas.put(new Mutation("de.susebox.java.util.SortedArray",2587,0,
		// NEGATE_JUMP, false), true);
		// jtopas.put(new
		// Mutation("de.susebox.java.util.AbstractTokenizer",1097,0,
		// REMOVE_CALL, false), true);
		// jtopas.put(new Mutation("de.susebox.java.util.Token",424,2,
		// REMOVE_CALL, false), true);
		// jtopas.put(new
		// Mutation("de.susebox.java.util.KeywordIterator",2764,0, RIC_MINUS_1,
		// false), true);
		// jtopas.put(new
		// Mutation("de.susebox.java.util.SpecialSequencesIterator",2885,0,
		// RIC_PLUS_1, false), false);
		// jtopas.put(new
		// Mutation("de.susebox.java.lang.ThrowableMessageFormatter",106,0,
		// REMOVE_CALL, false), true);
		// jtopas.put(new Mutation("de.susebox.jtopas.PluginTokenizer",228,0,
		// NEGATE_JUMP, false), true);
		// jtopas.put(new
		// Mutation("de.susebox.java.lang.ExtIndexOutOfBoundsException",249,0,
		// RIC_PLUS_1, false), false);
		// jtopas.put(new
		// Mutation("de.susebox.java.lang.ExtRuntimeException",204,0,
		// RIC_PLUS_1, false), false);
		// jtopas.put(new
		// Mutation("de.susebox.java.util.TokenizerException",211,0, RIC_PLUS_1,
		// false), false);
		// jtopas.put(new Mutation("de.susebox.java.io.ExtIOException",254,0,
		// RIC_MINUS_1, false), false);
		// jtopas.put(new
		// Mutation("de.susebox.java.util.InputStreamTokenizer",95,0,
		// RIC_PLUS_1, false), true);
		// jtopas.put(new Mutation("de.susebox.java.util.Token",424,0,
		// REMOVE_CALL, false), true);
		// jtopas.put(new
		// Mutation("de.susebox.java.util.AbstractTokenizer",1740,0,
		// REMOVE_CALL, false), true);
		// jtopas.put(new Mutation("de.susebox.java.io.ExtIOException",209,0,
		// RIC_PLUS_1, false), false);
		// jtopas.put(new
		// Mutation("de.susebox.java.lang.ThrowableMessageFormatter",105,0,
		// REMOVE_CALL, false), false);
		// jtopas.put(new Mutation("de.susebox.jtopas.PluginTokenizer",311,0,
		// REMOVE_CALL, false), true);
		// jtopas.put(new
		// Mutation("de.susebox.java.lang.ExtRuntimeException",248,0,
		// RIC_PLUS_1, false), false);
		// jtopas.put(new
		// Mutation("de.susebox.java.lang.ExtIndexOutOfBoundsException",204,0,
		// RIC_PLUS_1, false), false);
		// jtopas.put(new
		// Mutation("de.susebox.java.util.SpecialSequencesIterator",2904,0,
		// RIC_PLUS_1, false), false);
	}
	
	static Map<Mutation, Boolean> joda = new HashMap<Mutation, Boolean>();
	
	static {
		// joda.put(new Mutation("org.joda.time.Partial",656,0, REMOVE_CALL,
		// false), true);
		// joda.put(new Mutation("org.joda.time.format.ISODateTimeFormat",517,0,
		// RIC_MINUS_1, false), true);
		// joda.put(new
		// Mutation("org.joda.time.format.DateTimeFormatterBuilder",1055,1,
		// REMOVE_CALL, false), true);
		// joda.put(new Mutation("org.joda.time.base.BasePeriod",149,0,
		// NEGATE_JUMP, false), true);
		// joda.put(new
		// Mutation("org.joda.time.format.PeriodFormatterBuilder",734,0,
		// REMOVE_CALL, false), true);
		// joda.put(new
		// Mutation("org.joda.time.base.BaseSingleFieldPeriod",306,1,
		// RIC_PLUS_1, false), false);
		// joda.put(new Mutation("org.joda.time.tz.DateTimeZoneBuilder",412,0,
		// RIC_PLUS_1, false), true);
		// joda.put(new Mutation("org.joda.time.tz.CachedDateTimeZone",148,0,
		// RIC_MINUS_1, false), false);
		// joda.put(new Mutation("org.joda.time.Period",1441,0, RIC_PLUS_1,
		// false), true);
		// joda.put(new
		// Mutation("org.joda.time.convert.ReadableIntervalConverter",104,1,
		// REMOVE_CALL, false), true);
		// joda.put(new Mutation("org.joda.time.convert.ConverterManager",513,4,
		// REMOVE_CALL, false), true);
		// joda.put(new Mutation("org.joda.time.tz.ZoneInfoCompiler",153,0,
		// REMOVE_CALL, false), false);
		// joda.put(new Mutation("org.joda.time.chrono.StrictChronology",92,0,
		// REMOVE_CALL, false), true);
		// joda.put(new Mutation("org.joda.time.DateTimeUtils",347,1,
		// RIC_PLUS_1, false), false);
		// joda.put(new
		// Mutation("org.joda.time.IllegalFieldValueException",84,0,
		// RIC_MINUS_1, false), true);
		// joda.put(new
		// Mutation("org.joda.time.chrono.BasicFixedMonthChronology",112,1,
		// RIC_MINUS_1, false), true);
		// joda.put(new
		// Mutation("org.joda.time.chrono.BasicDayOfYearDateTimeField",92,0,
		// ARITHMETIC_REPLACE, false), true);
		// joda.put(new Mutation("org.joda.time.LocalTime",506,3, REMOVE_CALL,
		// false), true);
		// joda.put(new Mutation("org.joda.time.base.AbstractDuration",157,0,
		// RIC_PLUS_1, false), false);
		// joda.put(new
		// Mutation("org.joda.time.chrono.AssembledChronology",370,0,
		// RIC_PLUS_1, false), false);
	}
	
	static Map<Mutation, Boolean> barbecue = new HashMap<Mutation, Boolean>();
	
	static{
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.twod.pdf417.PDF417Module",201,0,
		// RIC_ZERO, false), true);
		// barbecue.put(new Mutation("net.sourceforge.barbecue.Barcode",191,0,
		// REMOVE_CALL, false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.linear.twoOfFive.Int2of5ModuleFactory",69,18,
		// RIC_ZERO, false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.linear.twoOfFive.Std2of5Barcode",134,2,
		// REMOVE_CALL, false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.linear.code128.Code128Barcode",290,0,
		// RIC_PLUS_1, false), true);
		// barbecue.put(new Mutation("net.sourceforge.barbecue.Modulo10",110,1,
		// RIC_PLUS_1, false), false);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.linear.code39.Code39Barcode",108,0,
		// RIC_PLUS_1, false), false);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.linear.twoOfFive.Std2of5ModuleFactory",94,0,
		// RIC_PLUS_1, false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.BarcodeFactory",114,0, RIC_PLUS_1,
		// false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.linear.codabar.CodabarBarcode",174,1,
		// RIC_MINUS_1, false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.BarcodeServlet",104,0,
		// RIC_MINUS_1, false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.linear.LinearBarcode",47,0,
		// REMOVE_CALL, false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.BarcodeImageHandler",77,0,
		// RIC_MINUS_1, false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.output.CenteredLabelLayout",8,0,
		// RIC_ZERO, false), false);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.linear.twoOfFive.Int2of5Barcode",103,0,
		// RIC_PLUS_1, false), false);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.CompositeModule",125,0,
		// ARITHMETIC_REPLACE, false), true);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.twod.pdf417.PDF417Barcode",145,0,
		// REMOVE_CALL, false), true);
		// barbecue.put(new Mutation("net.sourceforge.barbecue.Module",111,0,
		// RIC_PLUS_1, false), false);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.output.SizingOutput",46,1,
		// RIC_MINUS_1, false), false);
		// barbecue.put(new
		// Mutation("net.sourceforge.barbecue.linear.codabar.ModuleFactory",88,0,
		// REMOVE_CALL, false), true);
	}
	
	static Map<Mutation, Boolean> xstream = new HashMap<Mutation, Boolean>();
	static{
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.reflection.SortableFieldKeySorter",35,1,
		// REMOVE_CALL, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.core.util.QuickWriter",83,0,
		// REMOVE_CALL, false), true);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.reflection.CGLIBEnhancedConverter",226,0,
		// NEGATE_JUMP, false), true);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.reflection.FieldDictionary",75,0,
		// RIC_PLUS_1, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.io.xml.PrettyPrintWriter",276,0,
		// RIC_PLUS_1, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.basic.ByteConverter",28,0,
		// RIC_PLUS_1, false), true);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.core.util.CompositeClassLoader",61,0,
		// REMOVE_CALL, false), true);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.reflection.SerializationMethodInvoker",110,0,
		// ARITHMETIC_REPLACE, false), true);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter",251,5,
		// REMOVE_CALL, false), true);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.io.xml.Dom4JDriver",40,0,
		// RIC_MINUS_1, false), true);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.io.json.JsonWriter",198,0,
		// ARITHMETIC_REPLACE, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.extended.FontConverter",30,0,
		// RIC_PLUS_1, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.basic.DateConverter",93,0,
		// RIC_MINUS_1, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.io.path.PathTracker",125,0,
		// RIC_PLUS_1, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.core.util.Base64Encoder",63,8,
		// RIC_PLUS_1, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.javabean.PropertyDictionary$PropertyKey",171,0,
		// ARITHMETIC_REPLACE, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.core.util.CustomObjectInputStream$CustomGetField",224,0,
		// NEGATE_JUMP, false), true);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.extended.SubjectConverter",49,1,
		// REMOVE_CALL, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.converters.reflection.AbstractAttributedCharacterIteratorAttributeConverter",62,0,
		// REMOVE_CALL, false), false);
		// xstream.put(new
		// Mutation("xcom.thoughtworks.xstream.io.binary.Token",146,0,
		// ARITHMETIC_REPLACE, false), false);
	}

	static Map<Mutation, Boolean> commons= new HashMap<Mutation, Boolean>();
	static {
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.reflect.MemberUtils",170,0,
		// REMOVE_CALL, false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.StringUtils",4106,0, RIC_MINUS_1,
		// false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.text.ExtendedMessageFormat",319,0,
		// REMOVE_CALL, false), true);
		// commons.put(new Mutation("xorg.apache.commons.lang.CharUtils",376,0,
		// RIC_PLUS_1, false), true);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.math.NumberUtils",1395,0,
		// RIC_PLUS_1, false), true);
		// commons.put(new Mutation("xorg.apache.commons.lang.CharRange",207,0,
		// RIC_ZERO, false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.math.FloatRange",416,0,
		// RIC_PLUS_1, false), false);
		// commons.put(new Mutation("xorg.apache.commons.lang.math.Range",406,0,
		// RIC_ZERO, false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.text.StrBuilder",2520,0,
		// RIC_PLUS_1, false), false);
		// commons.put(new Mutation("xorg.apache.commons.lang.WordUtils",546,1,
		// RIC_PLUS_1, false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.time.DurationFormatUtils",198,0,
		// RIC_ZERO, false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.builder.CompareToBuilder",862,0,
		// RIC_MINUS_1, false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.RandomStringUtils",273,0,
		// RIC_PLUS_1, false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.time.FastDateFormat$Pair",1726,0,
		// NEGATE_JUMP, false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.math.IntRange",375,1,
		// ARITHMETIC_REPLACE, false), false);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.math.LongRange",386,0, RIC_PLUS_1,
		// false), false);
		// commons.put(new Mutation("xorg.apache.commons.lang.ClassUtils",401,0,
		// RIC_PLUS_1, false), true);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.text.translate.LookupTranslator",31,0,
		// RIC_PLUS_1, false), true);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.builder.HashCodeBuilder",592,0,
		// RIC_PLUS_1, false), true);
		// commons.put(new
		// Mutation("xorg.apache.commons.lang.ArrayUtils",2073,0, RIC_MINUS_1,
		// false), false);
	}
	
	static Map<Mutation, Boolean> jaxen= new HashMap<Mutation, Boolean>();

	static{
		// jaxen.put(new
		// Mutation("org.jaxen.saxpath.base.Verifier",278,0,RIC_MINUS_1,false),
		// true);
		// jaxen.put(new
		// Mutation("org.jaxen.dom.DocumentNavigator",360,0,REMOVE_CALL,false),
		// false);
		// jaxen.put(new
		// Mutation("org.jaxen.pattern.UnionPattern",62,1,RIC_PLUS_1,false),
		// false);
		// jaxen.put(new
		// Mutation("org.jaxen.jdom.DocumentNavigator",256,0,REMOVE_CALL,false),
		// false); // 0
		// jaxen.put(new
		// Mutation("org.jaxen.expr.DefaultStep",159,0,REMOVE_CALL,false),
		// true);
		// jaxen.put(new
		// Mutation("org.jaxen.dom.NamespaceNode",147,0,REMOVE_CALL,false),
		// false);
		// jaxen.put(new
		// Mutation("org.jaxen.pattern.LocationPathPattern",103,0,REMOVE_CALL,false),
		// true);
		// jaxen.put(new
		// Mutation("org.jaxen.saxpath.base.XPathLexer",482,0,REMOVE_CALL,false),
		// false); // 0
		// jaxen.put(new
		// Mutation("org.jaxen.expr.NodeComparator",147,0,RIC_PLUS_1,false),
		// false);
		// jaxen.put(new
		// Mutation("org.jaxen.function.ext.LocaleFunctionSupport",98,0,NEGATE_JUMP,false),
		// true);
		// jaxen.put(new
		// Mutation("org.jaxen.pattern.PatternParser",104,0,REMOVE_CALL,false),
		// false);
		// jaxen.put(new
		// Mutation("org.jaxen.expr.DefaultLocationPath",147,0,RIC_ZERO,false),
		// false);
		// jaxen.put(new
		// Mutation("org.jaxen.saxpath.base.XPathReader",131,1,REMOVE_CALL,false),
		// true);
		// jaxen.put(new
		// Mutation("org.jaxen.xom.DocumentNavigator",377,0,RIC_ZERO,false),
		// false);
		// jaxen.put(new
		// Mutation("org.jaxen.xom.DocumentNavigator$IndexIterator",217,1,RIC_ZERO,false),
		// false);
		// jaxen.put(new
		// Mutation("org.jaxen.function.NumberFunction",210,0,RIC_MINUS_1,false),
		// true);
		// jaxen.put(new
		// Mutation("org.jaxen.JaxenRuntimeException",66,1,RIC_MINUS_1,false),
		// true);
		// jaxen.put(new
		// Mutation("org.jaxen.XPathSyntaxException",135,0,RIC_MINUS_1,false),
		// true);
		// jaxen.put(new Mutation("org.jaxen.Context",101,0,RIC_MINUS_1,false),
		// true);
		// jaxen.put(new
		// Mutation("org.jaxen.expr.DefaultMultiplyExpr",75,2,REMOVE_CALL,false),
		// true);
	}

	public static Map<Mutation, Boolean> getManualClassification() {
		String prefix = ConfigurationLocator.getJavalancheConfiguration()
				.getProjectPrefix();
		Project[] values = Project.values();
		for (Project project : values) {
			if (prefix.equals(project.getPrefix())
					|| project.getPrefix().contains(prefix)) {
				return project.getResults();
			}
		}
		return null;
	}
	
}
