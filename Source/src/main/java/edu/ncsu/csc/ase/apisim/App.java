package edu.ncsu.csc.ase.apisim;

import edu.ncsu.csc.ase.apisim.util.StringUtil;

/**
 * Hello world!
 *
 */
public class App 
{
    /**
     * Hello World!
     * @param args
     */
	public static void main( String[] args ) {
		System.out.println((StringUtil.splitCamelCase("aqr12")));
		System.out.println("Asd:f1234_".matches("^[A-Za-z_][A-Za-z\\d_]*$"));
		System.out.println("Asdf1234_".matches("^[A-Za-z_][A-Za-z\\d_]*$"));
		System.out.println("1sdf1234_".matches("^[A-Za-z_][A-Za-z\\d_]*$"));
		
		
		
	}
    
}
