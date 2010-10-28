package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;


public class If2TEMPLATE {

	public static int m(Object step)
    {
		if (step instanceof StringBuffer)
        {
			return 11;
        }
        else
 if (step.getClass().equals(CharSequence.class))
        {
			return 12;
        }
        else 
        {
			return 13;
        }
    }

}
