package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import org.jaxen.expr.DefaultNameStep;
import org.jaxen.expr.DefaultStep;

public class If2TEMPLATE {

	public static int m(Object step)
    {
        if ( step instanceof DefaultNameStep )
        {
			return 11;
        }
        else
        if ( step.getClass().equals( DefaultStep.class ) )
        {
			return 12;
        }
        else 
        {
			return 13;
        }
    }

}
