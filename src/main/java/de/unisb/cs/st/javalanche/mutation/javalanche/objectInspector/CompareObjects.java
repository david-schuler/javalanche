package de.unisb.cs.st.javalanche.mutation.objectInspector;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class CompareObjects {
	private static Logger logger = Logger.getLogger(CompareObjects.class);

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Variables vars = new Variables(
				(Map<String, List<VariableInfo>>) XmlIo
						.fromXml(MutationProperties.RESULT_OBJECTS_DIR
								+ "de_st_cs_unisb_objectInspector_testClasses_ObjectsForMethod-variableNames.xml"));
		MethodVariables methodVariables1 = vars.getMethodVariables("method1");
		MethodVariables methodVariables2 = vars.getMethodVariables("method2");
		for (VariableInfoAndObject vi1 : methodVariables1.getVariables()) {
//			System.out.println(vi1.getName() + "  " + vi1.getObject());
			if (methodVariables2.hasVariable(vi1.getName())) {
				VariableInfoAndObject vi2 = methodVariables2.getVariable(vi1
						.getName());
				Object o1 = vi1.getObject();
				Object o2 = vi2.getObject();

				if (!o1.equals(o2)) {
					logger
							.info(String
									.format(
											"Found different Objects: Name: %s \n object-1: %s \n object-2: %s ",
											vi1.getName(), o1.toString(), o2
													.toString()));
				}
			}

		}
	}
}
