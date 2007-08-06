package org.softevo.mutation.mutationPossibilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;

public class MutationPossibilityCollector {

	private List<Mutation> possibilities = new ArrayList<Mutation>();

	public void addPossibility(Mutation mutationPossibility) {
		possibilities.add(mutationPossibility);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Mutation mutation :  possibilities){
			sb.append(mutation);
			sb.append('\n');
		}
		return sb.toString();
	}

	public void toXML(){
		XmlIo.toXML(possibilities,new File(MutationProperties.MUTATIONS_TO_APPLY_FILE));
	}
}
