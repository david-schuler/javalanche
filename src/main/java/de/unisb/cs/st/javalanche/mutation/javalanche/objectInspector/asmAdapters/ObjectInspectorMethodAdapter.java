package de.unisb.cs.st.javalanche.mutation.objectInspector.asmAdapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import de.unisb.cs.st.javalanche.mutation.objectInspector.VariableInfo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class ObjectInspectorMethodAdapter extends AdviceAdapter {

	private Map<Integer, Integer> localVariables = new HashMap<Integer, Integer>();

//	private Map<Integer, Integer> countForVar = new HashMap<Integer, Integer>();

	private List<VariableInfo> variableInfo = new ArrayList<VariableInfo>();

	private String methodName;

	private String className;

	private static int id = 0;

	public ObjectInspectorMethodAdapter(MethodVisitor mv, int access,
			String name, String desc, String className) {
		super(mv, access, name, desc);
		this.methodName = name;
		this.className = className;
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature,
			Label start, Label end, int index) {
		super.visitLocalVariable(name, desc, signature, start, end, index);
		System.out.println("local Variable: " + name + " - " + index + " - "
				+ desc);
		variableInfo.add(new VariableInfo(name, desc, index,
				getFileNameForIndex(index, 0, className, methodName)));
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		super.visitVarInsn(opcode, var);
		if (opcode == ISTORE) {
			serializeVariable(opcode, var);
			localVariables.put(var, ILOAD);
		} else if (opcode == LSTORE) {
			serializeVariable(opcode, var);
			localVariables.put(var, LLOAD);
		} else if (opcode == FSTORE) {
			serializeVariable(opcode, var);
			localVariables.put(var, FLOAD);
		} else if (opcode == DSTORE) {
			serializeVariable(opcode, var);
			localVariables.put(var, DLOAD);
		} else if (opcode == ASTORE) {
			serializeVariable(opcode, var);
			localVariables.put(var, ALOAD);
		}
	}

	private void serializeVariable(int opcode, int var) {
//	 TODO introduce  a method that serializes these objects
//		String methodToCall = "toXMLPrimitive";
//		if (opcode == ASTORE) {
//			super.visitVarInsn(ALOAD, var);
//			insertFileNameStatements(var, getCountVorVar(var));
//			super.visitMethodInsn(INVOKESTATIC,
//					"de.unisb.cs.st.ds/util/io/XmlIo", "toXML",
//					"(Ljava/lang/Object;Ljava/lang/String;)V");
//
//		} else if (opcode == ISTORE) {
//			super.visitVarInsn(ILOAD, var);
//			insertFileNameStatements(var, getCountVorVar(var));
//			super.visitMethodInsn(INVOKESTATIC,
//					"de.unisb.cs.st.ds/util/io/XmlIo", methodToCall,
//					"(ILjava/lang/String;)V");
//
//		} else if (opcode == FSTORE) {
//			super.visitVarInsn(FLOAD, var);
//			insertFileNameStatements(var, getCountVorVar(var));
//			super.visitMethodInsn(INVOKESTATIC,
//					"de.unisb.cs.st.ds/util/io/XmlIo", methodToCall,
//					"(FLjava/lang/String;)V");
//
//		} else if (opcode == LLOAD) {
//			super.visitVarInsn(LLOAD, var);
//			insertFileNameStatements(var, getCountVorVar(var));
//			super.visitMethodInsn(INVOKESTATIC,
//					"de.unisb.cs.st.ds/util/io/XmlIo", methodToCall,
//					"(LLjava/lang/String;)V");
//
//		} else if (opcode == DLOAD) {
//			super.visitVarInsn(DLOAD, var);
//			insertFileNameStatements(var, getCountVorVar(var));
//			super.visitMethodInsn(INVOKESTATIC,
//					"de.unisb.cs.st.ds/util/io/XmlIo", methodToCall,
//					"(DLjava/lang/String;)V");
//
//		}
		//
//		public static void toXMLPrimitive(int i, String fileName) {
//			toXML(i, fileName);
//		}
	//
//		public static void toXMLPrimitive(float f, String fileName) {
//			toXML(f, fileName);
//		}
	//
//		public static void toXMLPrimitive(long l, String fileName) {
//			toXML(l, fileName);
//		}
	//
//		public static void toXMLPrimitive(double d, String fileName) {
//			toXML(d, fileName);
//		}
	}
//
//	private int getCountVorVar(int var) {
//		int count = 0;
//		if (countForVar.containsKey(var)) {
//			count = countForVar.get(var) + 1;
//			countForVar.put(var, count);
//		}
//		countForVar.put(var, count);
//		return count;
//	}

	// @Override
	protected void onMethodEnter() {
	}

	// @Override
	protected void onMethodExit(int opcode) {
		// System.out.println("Found " + localVariables.size() + " Variables");
		// mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
		// "Ljava/io/PrintStream;");
		// mv.visitLdcInsn("[Adapter] End of Method");
		// mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
		// "(Ljava/lang/String;)V");
		// String methodToCall = "toXMLPrimitive";
		//
		// for (Map.Entry<Integer, Integer> entry : localVariables.entrySet()) {
		// // // Type type = Type.getObjectType(entry.getValue());
		// // // int loadOpcode = type.getOpcode(Opcodes.ILOAD);
		// if (entry.getValue() == ALOAD) {
		// mv.visitVarInsn(entry.getValue(), entry.getKey());
		// insertFileNameStatements(entry.getKey());
		// mv.visitMethodInsn(INVOKESTATIC,
		// "de.unisb.cs.st.ds/util/io/XmlIo", "toXML",
		// "(Ljava/lang/Object;Ljava/lang/String;)V");
		//
		// // mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
		// // "Ljava/io/PrintStream;");
		// // mv.visitVarInsn(entry.getValue(), entry.getKey());
		// // mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object",
		// // "getClass", "()Ljava/lang/Class;");
		// // // mv.visitLdcInsn(entry.toString());
		// // mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream",
		// // "println", "(Ljava/lang/Object;)V");
		// } else if (entry.getValue() == ILOAD) {
		// mv.visitVarInsn(entry.getValue(), entry.getKey());
		// insertFileNameStatements(entry.getKey());
		// mv.visitMethodInsn(INVOKESTATIC,
		// "de.unisb.cs.st.ds/util/io/XmlIo", methodToCall,
		// "(ILjava/lang/String;)V");
		//
		// } else if (entry.getValue() == FLOAD) {
		// mv.visitVarInsn(entry.getValue(), entry.getKey());
		// insertFileNameStatements(entry.getKey());
		// mv.visitMethodInsn(INVOKESTATIC,
		// "de.unisb.cs.st.ds/util/io/XmlIo", methodToCall,
		// "(FLjava/lang/String;)V");
		//
		// }
		//
		// else if (entry.getValue() == LLOAD) {
		// mv.visitVarInsn(entry.getValue(), entry.getKey());
		// insertFileNameStatements(entry.getKey());
		// mv.visitMethodInsn(INVOKESTATIC,
		// "de.unisb.cs.st.ds/util/io/XmlIo", methodToCall,
		// "(LLjava/lang/String;)V");
		//
		// } else if (entry.getValue() == DLOAD) {
		// mv.visitVarInsn(entry.getValue(), entry.getKey());
		// insertFileNameStatements(entry.getKey());
		// mv.visitMethodInsn(INVOKESTATIC,
		// "de.unisb.cs.st.ds/util/io/XmlIo", methodToCall,
		// "(DLjava/lang/String;)V");
		//
		// }
		//
		// }
	}

//	private void insertFileNameStatements(int n, int count) {
//		mv.visitIntInsn(SIPUSH, n);
//		mv.visitIntInsn(SIPUSH, count);
//		mv.visitLdcInsn(className);
//		mv.visitLdcInsn(methodName);
//		mv
//				.visitMethodInsn(
//						INVOKESTATIC,
//						"de.unisb.cs.st.javalanche.mutation/objectInspector/asmAdapters/ObjectInspectorMethodAdapter",
//						"getFileNameForIndex",
//						"(IILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
//	}

	public static String getFileNameForIndex(int index, int count,
			String className, String methodName) {
		String mutationID = MutationProperties.NOT_MUTATED;
		String prop = System
				.getProperty(MutationProperties.ACTUAL_MUTATION_KEY);
		if (prop != null) {
			mutationID = prop;
		}
		id++;
		String fileName = MutationProperties.RESULT_OBJECTS_DIR + "/"
				+ className + "-" + methodName + "-" + mutationID + "-" + index
				+ "-" + count + "-" + id + ".xml";

		return fileName;
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

	/**
	 * @return the variableInfo
	 */
	public List<VariableInfo> getVariableInfo() {
		return variableInfo;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}



}
