package yeti.environments.java;

/**

YETI - York Extensible Testing Infrastructure

Copyright (c) 2009-2010, Manuel Oriol <manuel.oriol@gmail.com> - University of York
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. All advertising materials mentioning features or use of this software
must display the following acknowledgement:
This product includes software developed by the University of York.
4. Neither the name of the University of York nor the
names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

**/

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import yeti.YetiIdentifier;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiType;
import yeti.YetiVariable;
import yeti.environments.YetiLoader;

/**
 * Class that represents primitive types.
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public class YetiJavaSpecificType extends YetiType {

	/**
	 * A version of the type name actually nice to read 
	 */
	String prettyPrintTypeName = null;

	/**
	 * Is it an array type? True if yes.
	 */
	@SuppressWarnings("unused")
	private boolean isArrayType = false;

	/**
	 * The number of levels of array if it is an array type.
	 */
	private int numDimensionsArray = 0;

	
	/**
	 * The base type if it is an array type.
	 */
	private String baseTypeForArrayName="";

	/**
	 * The base type if it is an array type.
	 */
	@SuppressWarnings("unchecked")
	private Class baseType=null;


	/**
	 * Is it a simple type?
	 */
	private boolean isSimpleType=false;

	/**
	 * Is this type a simple type?
	 * @return  the fact it is a simple type.
	 */
	public boolean isSimpleType() {
		return isSimpleType;
	}


	/**
	 * Sets the reason it is a simple type.
	 * @param isSimpleType  true if it is a simple type.
	 */
	public void setSimpleType(boolean isSimpleType) {
		this.isSimpleType = isSimpleType;
	}


	/**
	 * Did it go through the init phase in the custom class loader.
	 */
	private boolean isProperlyInitialized=false;


	/**
	 * Simple getter.
	 * 
	 * @return true if it went through the proper class loader initialization.
	 */
	public boolean isProperlyInitialized() {
		return isProperlyInitialized;
	}

	/**
	 * Simple setter.
	 * 
	 * @parameter isProperlyInitialized put true if it went through the proper class loader initialization.
	 */
	public void setProperlyInitialized(boolean isProperlyInitialized) {
		this.isProperlyInitialized = isProperlyInitialized;
	}


	/**
	 * Initialize the primitive types (as they are not classes, they need to be initialized in a specific way).
	 */
	public static void initPrimitiveTypes(){

		// we create the primitive types		
		YetiJavaSpecificType tBoolean=new YetiJavaSpecificType("boolean");
		tBoolean.setSimpleType(true);
		tBoolean.addInterestingValues(new Boolean(true));
		tBoolean.addInterestingValues(new Boolean(false));
		
		YetiJavaSpecificType tByte=new YetiJavaSpecificType("byte");
		tByte.setSimpleType(true);
		tByte.addInterestingValues(new Byte((byte)0));
		tByte.addInterestingValues(new Byte((byte)(1)));
		tByte.addInterestingValues(new Byte((byte)(2)));
		tByte.addInterestingValues(new Byte((byte)(255)));
		tByte.addInterestingValues(new Byte((byte)(254)));
		tByte.addInterestingValues(new Byte((byte)(253)));

		YetiJavaSpecificType tShort=new YetiJavaSpecificType("short");
		tShort.setSimpleType(true);
		for (short j = -10; j<11;j++)
			tShort.addInterestingValues(new Short((short)(j)));
		tShort.addInterestingValues(new Short(Short.MAX_VALUE));
		tShort.addInterestingValues(new Short((short)(Short.MAX_VALUE-1)));
		tShort.addInterestingValues(new Short((short)(Short.MAX_VALUE-2)));
		tShort.addInterestingValues(new Short((short)(Short.MIN_VALUE+2)));
		tShort.addInterestingValues(new Short((short)(Short.MIN_VALUE+1)));
		tShort.addInterestingValues(new Short(Short.MIN_VALUE));


		YetiJavaSpecificType tInt=new YetiJavaSpecificType("int");
		tInt.setSimpleType(true);
		for (int j = -10; j<11;j++)
			tInt.addInterestingValues(new Integer(j));
		tInt.addInterestingValues(new Integer(Integer.MAX_VALUE));
		tInt.addInterestingValues(new Integer(Integer.MAX_VALUE-1));
		tInt.addInterestingValues(new Integer(Integer.MAX_VALUE-2));
		tInt.addInterestingValues(new Integer(Integer.MIN_VALUE+2));
		tInt.addInterestingValues(new Integer(Integer.MIN_VALUE+1));
		tInt.addInterestingValues(new Integer(Integer.MIN_VALUE));

		YetiJavaSpecificType tLong=new YetiJavaSpecificType("long");
		tLong.setSimpleType(true);
		for (long j = -10; j<11;j++)
			tLong.addInterestingValues(new Long(j));
		tLong.addInterestingValues(new Long(Long.MAX_VALUE));
		tLong.addInterestingValues(new Long(Long.MAX_VALUE-1));
		tLong.addInterestingValues(new Long(Long.MAX_VALUE-2));
		tLong.addInterestingValues(new Long(Long.MIN_VALUE+2));
		tLong.addInterestingValues(new Long(Long.MIN_VALUE+1));
		tLong.addInterestingValues(new Long(Long.MIN_VALUE));
		tLong.addInterestingValues(new Long(Integer.MAX_VALUE));
		tLong.addInterestingValues(new Long(Integer.MAX_VALUE-1));
		tLong.addInterestingValues(new Long(Integer.MAX_VALUE-2));
		tLong.addInterestingValues(new Long(Integer.MIN_VALUE+2));
		tLong.addInterestingValues(new Long(Integer.MIN_VALUE+1));
		tLong.addInterestingValues(new Long(Integer.MIN_VALUE));



		YetiJavaSpecificType tDouble=new YetiJavaSpecificType("double");
		tDouble.setSimpleType(true);
		tDouble.addInterestingValues(new Double(Double.MAX_VALUE));
		tDouble.addInterestingValues(new Double(Double.MIN_VALUE));
		tDouble.addInterestingValues(new Double(Double.NaN));
		tDouble.addInterestingValues(new Double(Double.POSITIVE_INFINITY));
		tDouble.addInterestingValues(new Double(Double.NEGATIVE_INFINITY));
		tDouble.addInterestingValues(new Double((double)(Float.MAX_VALUE)));
		tDouble.addInterestingValues(new Double((double)(Float.MIN_VALUE)));
		tDouble.addInterestingValues(new Double((double)(Float.NaN)));
		tDouble.addInterestingValues(new Double((double)(Float.POSITIVE_INFINITY)));
		tDouble.addInterestingValues(new Double((double)(Float.NEGATIVE_INFINITY)));


		YetiJavaSpecificType tChar=new YetiJavaSpecificType("char");
		tChar.setSimpleType(true);
		tChar.addInterestingValues(new Character('\0'));
		tChar.addInterestingValues(new Character('\1'));
		tChar.addInterestingValues(new Character('\2'));
		tChar.addInterestingValues(new Character('\n'));
		tChar.addInterestingValues(new Character('\255'));
		tChar.addInterestingValues(new Character('\254'));
		tChar.addInterestingValues(new Character('\253'));
		tChar.addInterestingValues(new Character(Character.MAX_VALUE));
		tChar.addInterestingValues(new Character(Character.MIN_VALUE));



		YetiJavaSpecificType tFloat=new YetiJavaSpecificType("float");
		tFloat.setSimpleType(true);
		tFloat.addInterestingValues(new Float(Float.MAX_VALUE));
		tFloat.addInterestingValues(new Float(Float.MIN_VALUE));
		tFloat.addInterestingValues(new Float(Float.NaN));
		tFloat.addInterestingValues(new Float(Float.POSITIVE_INFINITY));
		tFloat.addInterestingValues(new Float(Float.NEGATIVE_INFINITY));

		// we add the helper class that has creating procedures.
		YetiLoader.yetiLoader.addDefinition(YetiJavaSpecificType.class);

	}


	/**
	 * A boolean random generator.
	 * 
	 * @return a random boolean.
	 */
	public static boolean __yetiValue_createRandomBoolean(){
		return (Math.random()<.5);
	}

	/**
	 * A byte random generator.
	 * 
	 * @return a random byte.
	 */
	public static byte __yetiValue_createRandomByte(){
		double d=Math.random();
		return ((byte) Math.floor(257*d));
	}

	/**
	 * A short random generator.
	 * 
	 * @return a random short.
	 */
	public static short __yetiValue_createRandomShort(){
		double d=Math.random();
		return((short) (Math.floor(65535*d)-32768));
	}

	/**
	 * A int random generator.
	 * 
	 * @return a random int.
	 */
	public static int __yetiValue_createRandomInt(){
		double d=Math.random();
		double d2=Math.random()*2-1.0d;
		return ((int) Math.floor(2147483647*d*d2));
	}

	/**
	 * A long random generator.
	 * 
	 * @return a random long.
	 */
	public static long __yetiValue_createRandomLong(){
		Double d=new Double(YetiJavaSpecificType.__yetiValue_createRandomDouble());
		return d.longValue();
	}

	/**
	 * A char random generator.
	 * 
	 * @return a random char.
	 */
	public static char __yetiValue_createRandomChar(){
		double d=Math.random();
		return (char)(Math.floor(0xFFFF*d));
	}

	/**
	 * A float random generator.
	 * 
	 * @return a random float.
	 */
	public static float __yetiValue_createRandomFloat(){
		int i = (int) Math.floor(11*Math.random());
		return ((float)Math.random()*(10^i));
	}

	/**
	 * A double random generator.
	 * 
	 * @return a random double.
	 */
	public static double __yetiValue_createRandomDouble(){
		int i = (int) Math.floor(15*Math.random());
		return (Math.random()*(10^i));
	}

	
	
	
	


	/**
	 * Constructor that creates an empty type.
	 * It also sets the pretty name to a good looking value.
	 * 
	 */
	public YetiJavaSpecificType(String name) {
		super(name);

		YetiLog.printDebugLog("Creating type: "+name, this);
		String uglyName=name, prettyName="";

		// we remove the "[" (for array types)
		while (uglyName.startsWith("[")){
			this.isArrayType=true;
			this.numDimensionsArray++;
			prettyName=prettyName+"[]";
			uglyName=uglyName.substring(1);
		}
		// for reference types, we actually extract the class name
		if (uglyName.contains(";")){
			this.baseTypeForArrayName=uglyName.substring(1, uglyName.indexOf(';'));
			try {
				this.baseType = Class.forName(baseTypeForArrayName);
	
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			prettyName=this.baseTypeForArrayName+prettyName;
			// for primitive types, we translate the name of the type.
		} else{
			if (uglyName.equals("I")) {
				this.baseTypeForArrayName = "int";
				this.baseType=int.class;
			}
			else if (uglyName.equals("Z")) {
				this.baseTypeForArrayName = "boolean";	
				this.baseType=boolean.class;
			}
			else if (uglyName.equals("C")) {
				this.baseTypeForArrayName = "char";
				this.baseType=char.class;
			}
			else if (uglyName.equals("B")) {
				this.baseTypeForArrayName = "byte";
				this.baseType=byte.class;
			}
			else if (uglyName.equals("D")) {
				this.baseTypeForArrayName = "double";
				this.baseType=double.class;
			}
			else if (uglyName.equals("F")) {
				this.baseTypeForArrayName = "float";
				this.baseType=float.class;
			}
			else if (uglyName.equals("J")) {
				this.baseTypeForArrayName = "long";
				this.baseType=long.class;
			}
			else if (uglyName.equals("S")) {
				this.baseTypeForArrayName = "short";
				this.baseType=short.class;
			}
			else
				this.baseTypeForArrayName=uglyName;
			prettyName=this.baseTypeForArrayName+prettyName;

		}
//		if (isArrayType) {
//			try {
//			
//				this.addCreationRoutine(new YetiJavaMethod(new YetiName("__yetiValue_createArrayOfCorrectLevel"), null, this, YetiModule.allModules.get("YetiJavaSpecificType"), this.getClass().getMethod("__yetiValue_createArrayOfCorrectLevel", (Class <?>[])null), true));
//			} catch (SecurityException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NoSuchMethodException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		this.prettyPrintTypeName=prettyName;
	}

	/**
	 * Returns an interesting value in a variable and does not remove it from the list of interesting values.
	 * 
	 * @param interestingValue the interesting value.
	 */
	public YetiVariable getRandomInterestingVariable() {
		Object value =this.getRandomInterestingValue();
		YetiLog.printDebugLog("Interesting variable: "+value, this);
		YetiIdentifier id=YetiIdentifier.getFreshIdentifier();
		YetiLog.printYetiLog(YetiJavaMethod.generateLogForValues(this.prettyPrintTypeName+" "+id.getValue()+"=",value), this);
		return new YetiVariable(id, this, value);

	}
	
	/* (non-Javadoc)
	 * 
	 * Returns the prettyPrint version of the name
	 * 
	 * @see yeti.YetiType#toString()
	 */
	@Override
	public String toString() {
		return this.prettyPrintTypeName;
	}

}
