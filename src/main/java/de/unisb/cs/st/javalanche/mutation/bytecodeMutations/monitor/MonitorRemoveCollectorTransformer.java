package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.CollectorByteCodeTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 *
 */
class MonitorRemoveCollectorTransformer extends CollectorByteCodeTransformer {

  public MonitorRemoveCollectorTransformer(MutationPossibilityCollector mpc) {
    this.mpc = mpc;
  }

  @Override
  protected ClassVisitor classVisitorFactory(ClassWriter cw) {
    ClassVisitor cc = new CheckClassAdapter(cw);
    return new MonitorRemovePossibilitiesClassAdapter(cc, mpc);
  }

}
