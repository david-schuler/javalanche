package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

public class If2Class {

	public void m1(int x){
		
		if(x 
				>
		10){
			System.out.println("a");
		}
		
		else{
			x =3;
			
		}
	}
	
	public void m2(int x){
		if(x == 2){ x = 1 ;}else{x++;}
	}
}
