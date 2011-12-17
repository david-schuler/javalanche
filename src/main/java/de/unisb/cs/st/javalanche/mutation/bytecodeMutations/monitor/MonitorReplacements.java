package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 *
 */
public class MonitorReplacements {

  static int[][] replaceArray = new int[][] {
      { Opcodes.MONITORENTER, Opcodes.POP },
      { Opcodes.MONITOREXIT, Opcodes.POP }, };

  private static Map<Integer, Integer> replaceMap;

  private MonitorReplacements() {
  }

  public static Map<Integer, Integer> getReplaceMap() {
    if (replaceMap != null) {
      return replaceMap;
    }
    replaceMap = new HashMap<Integer, Integer>();
    for (int[] replace : replaceArray) {
      assert replace.length >= 2;
      replaceMap.put(replace[0], replace[1]);
    }
    return replaceMap;
  }

}
