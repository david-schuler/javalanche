package de.unisb.cs.st.javalanche.mutation.objectInspector.asmAdapters;

import java.util.*;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.objectInspector.VariableInfo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class ObjectInspectorClassAdapter extends ClassAdapter {

	List<ObjectInspectorMethodAdapter> methodVisitors = new ArrayList<ObjectInspectorMethodAdapter>();

	private String className;

	public ObjectInspectorClassAdapter(ClassVisitor cv) {
		super(cv);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		this.className = name.replace("/", "_");
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
//		if (name.startsWith("test")) {
			ObjectInspectorMethodAdapter objectInspectorMethodAdapter = new ObjectInspectorMethodAdapter(
					mv, access, name, desc, className);
			methodVisitors.add(objectInspectorMethodAdapter);
			return objectInspectorMethodAdapter;
//		}
//	return mv;
	}

	public Map<String, List<VariableInfo>> getVariableNames() {
		Map<String, List<VariableInfo>> resultMap = new HashMap<String, List<VariableInfo>>();
		for (ObjectInspectorMethodAdapter oima : methodVisitors) {
			resultMap.put(oima.getMethodName(), oima.getVariableInfo());
		}
		return resultMap;
	}

	public void saveResultMap() {
		XmlIo.toXML(getVariableNames(), MutationProperties.RESULT_OBJECTS_DIR
				+ className + "-variableNames.xml");
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
		saveResultMap();
	}

}
