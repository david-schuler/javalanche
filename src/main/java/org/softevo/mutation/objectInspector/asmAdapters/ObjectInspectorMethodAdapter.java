package org.softevo.mutation.objectInspector.asmAdapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import org.softevo.mutation.objectInspector.VariableInfo;
import org.softevo.mutation.properties.MutationProperties;

public class ObjectInspectorMethodAdapter extends AdviceAdapter {

	private Map<Integer, Integer> localVariables = new HashMap<Integer, Integer>();

	private List<VariableInfo> variableInfo = new ArrayList<VariableInfo>();

	private String methodName;

	private String className;

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
				getFileNameForIndex(index)));
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		super.visitVarInsn(opcode, var);
		if (opcode == ISTORE) {
			localVariables.put(var, ILOAD);
		} else if (opcode == LSTORE) {
			localVariables.put(var, LLOAD);
		} else if (opcode == FSTORE) {
			localVariables.put(var, FLOAD);
		} else if (opcode == DSTORE) {
			localVariables.put(var, DLOAD);
		} else if (opcode == ASTORE) {
			localVariables.put(var, ALOAD);
		}
	}

	@Override
	protected void onMethodEnter() {
	}

	@Override
	protected void onMethodExit(int opcode) {
		System.out.println("Found " + localVariables.size() + " Variables");
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
				"Ljava/io/PrintStream;");
		mv.visitLdcInsn("[Adapter] End of Method");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
				"(Ljava/lang/String;)V");
		String methodToCall = "toXMLPrimitive";

		for (Map.Entry<Integer, Integer> entry : localVariables.entrySet()) {
			// // Type type = Type.getObjectType(entry.getValue());
			// // int loadOpcode = type.getOpcode(Opcodes.ILOAD);
			if (entry.getValue() == ALOAD) {
				mv.visitVarInsn(entry.getValue(), entry.getKey());
				String fileName = getFileNameForIndex(entry.getKey());
				mv.visitLdcInsn(fileName);
				mv.visitMethodInsn(INVOKESTATIC,
						"org/softevo/mutation/io/XmlIo", "toXML",
						"(Ljava/lang/Object;Ljava/lang/String;)V");

				// mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
				// "Ljava/io/PrintStream;");
				// mv.visitVarInsn(entry.getValue(), entry.getKey());
				// mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object",
				// "getClass", "()Ljava/lang/Class;");
				// // mv.visitLdcInsn(entry.toString());
				// mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream",
				// "println", "(Ljava/lang/Object;)V");
			}
			else if(entry.getValue() == ILOAD){
				mv.visitVarInsn(entry.getValue(), entry.getKey());
				String fileName = getFileNameForIndex(entry.getKey());
				mv.visitLdcInsn(fileName);
				mv.visitMethodInsn(INVOKESTATIC,
						"org/softevo/mutation/io/XmlIo", methodToCall,
						"(ILjava/lang/String;)V");

			}
			else if(entry.getValue() == FLOAD){
				mv.visitVarInsn(entry.getValue(), entry.getKey());
				String fileName = getFileNameForIndex(entry.getKey());
				mv.visitLdcInsn(fileName);
				mv.visitMethodInsn(INVOKESTATIC,
						"org/softevo/mutation/io/XmlIo", methodToCall,
						"(FLjava/lang/String;)V");

			}

			else if(entry.getValue() == LLOAD){
				mv.visitVarInsn(entry.getValue(), entry.getKey());
				String fileName = getFileNameForIndex(entry.getKey());
				mv.visitLdcInsn(fileName);
				mv.visitMethodInsn(INVOKESTATIC,
						"org/softevo/mutation/io/XmlIo", methodToCall,
						"(LLjava/lang/String;)V");

			}
			else if(entry.getValue() == DLOAD){
				mv.visitVarInsn(entry.getValue(), entry.getKey());
				String fileName = getFileNameForIndex(entry.getKey());
				mv.visitLdcInsn(fileName);
				mv.visitMethodInsn(INVOKESTATIC,
						"org/softevo/mutation/io/XmlIo", methodToCall,
						"(DLjava/lang/String;)V");

			}

		}
	}

	private String getFileNameForIndex(int index) {
		String fileName = MutationProperties.RESULT_OBJECTS_DIR + "/" + className + "-" + methodName + "-" + index + ".xml";
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