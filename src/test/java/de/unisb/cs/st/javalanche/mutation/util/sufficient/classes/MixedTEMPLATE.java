package de.unisb.cs.st.javalanche.mutation.util.sufficient.classes;

import java.util.List;

public class MixedTEMPLATE {

    private List nodeSet;
	private int size;
	private int position;

	public int m1(List nodeSet, int pos)
    {  
        this.nodeSet = nodeSet; position =pos;
        this.size    = nodeSet.size();
		if (position >= size) this.position = 0;
		return position;
    }

}
