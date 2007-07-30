package org.softevo.mutation.mutationPossibilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.properties.MutationProperties;

public class MutationPossibilityCollector {

	private List<MutationPossibility> possibilities = new ArrayList<MutationPossibility>();

	public void addPossibility(MutationPossibility mutationPossibility) {
		possibilities.add(mutationPossibility);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(MutationPossibility m :  possibilities){
			sb.append(m);
			sb.append('\n');
		}
		return sb.toString();
	}

	public void toXML(){
		XmlIo.toXML(possibilities,new File(MutationProperties.MUTATIONS_TO_APPLY_FILE));
	}
}
