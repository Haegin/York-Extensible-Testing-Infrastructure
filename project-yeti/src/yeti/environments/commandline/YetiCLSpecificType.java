package yeti.environments.commandline;

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

import yeti.YetiCard;
import yeti.YetiIdentifier;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.YetiVariable;
import yeti.environments.commandline.YetiCLSpecificType;
import yeti.environments.commandline.YetiCLSpecificType.YetiCLRoutine;
import yeti.environments.java.YetiJavaSpecificType;

/**
 * Class that represents the types to pass on the command-line 
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Aug 30, 2011
 *
 */
public class YetiCLSpecificType extends YetiType {

	public class YetiCLRoutine extends YetiRoutine{
		public YetiCLRoutine(String name, YetiType type) {
			this.name = new YetiName(name);
			this.openSlots = new YetiType[0];
			this.returnType = type;
		}
		
		@Override
		public boolean checkArguments(YetiCard[] arg) {
			return true;
		}

		@Override
		public Object makeCall(YetiCard[] arg) {
			try {
				// make effecttive call to create the value of certain type 
				makeEffectiveCall(arg);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			// create a yeti variable by the value 
			return new YetiVariable(YetiIdentifier.getFreshIdentifier(),
					returnType, value);
		}
		
		@Override
		public String makeEffectiveCall(YetiCard[] arg) throws Throwable {
			// TODO fix
			//if (returnType.getName().equals("int"))
			//	return __yetiValue_createRandomInt();
			
			// create value for certain type
			if (returnType.getName().equals("int")){
				System.out.println("creation routine is called for " + returnType.getName());
				 value = __yetiValue_createRandomInt();
			}else if (returnType.getName().equals("long")){
				System.out.println("creation routine is called for " + returnType.getName());
				 value = __yetiValue_createRandomLong();
			}else if (returnType.getName().equals("boolean")){
				System.out.println("creation routine is called for " + returnType.getName());
				 value = __yetiValue_createRandomBoolean();
			}else if (returnType.getName().equals("byte")){
				System.out.println("creation routine is called for " + returnType.getName());
				 value = __yetiValue_createRandomByte();
			}else if (returnType.getName().equals("short")){
				System.out.println("creation routine is called for " + returnType.getName());
				 value = __yetiValue_createRandomShort();
			}else if (returnType.getName().equals("char")){
				System.out.println("creation routine is called for " + returnType.getName());
				 value = __yetiValue_createRandomChar();
			}else if (returnType.getName().equals("float")){
				System.out.println("creation routine is called for " + returnType.getName());
				 value = __yetiValue_createRandomFloat();
			}else if (returnType.getName().equals("double")){
				System.out.println("creation routine is called for " + returnType.getName());
				 value = __yetiValue_createRandomDouble();
			}else if (returnType.getName().equals("String")){
				System.out.println("creation routine is called for " + returnType.getName());
				 value = __yetiValue_createRandomString();
			}else{
				throw new Exception("Can not Create value for type " + returnType.getName());
			}
			return "";
		}
		
		/**
		 * return value of creation routine
		 * */
		Object value = null;
	}
	
	
	public YetiCLSpecificType(String name) {
		super(name);
		// FIX
		//this.addCreationRoutine(name, this);
		
		// create and add the creation routine for this type
		YetiCLRoutine cr = new YetiCLRoutine(name, this);
		addCreationRoutine(cr);
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
	 * A String random generator.
	 * 
	 * @return a random String.
	 */
	//TODO need review !!!
	public static String __yetiValue_createRandomString(){
		int len = (int)(Math.random()*50); // randomly create the length of the string
		char c[] = new char [len];         // create a char array
		int i = 0;
		while(i < len){                    // randomly generate char value for each element in the char array
			c[i] = __yetiValue_createRandomChar();
			i ++;
		}
		
		if(len < 5){					 // return null by chance  
			return null;
		}else{
			return new String(c);        // return the string value of the char array
		}
	}

	public static void initPrimitiveTypes() {

		// we create the primitive types		
		YetiCLSpecificType tBoolean=new YetiCLSpecificType("boolean");
		tBoolean.addInterestingValues(true);
		tBoolean.addInterestingValues(false);
		
		YetiCLSpecificType tByte=new YetiCLSpecificType("byte");
		tByte.addInterestingValues((byte)0);
		tByte.addInterestingValues((byte)1);
		tByte.addInterestingValues((byte)2);
		tByte.addInterestingValues((byte)255);
		tByte.addInterestingValues((byte)254);
		tByte.addInterestingValues((byte)253);

		YetiCLSpecificType tShort=new YetiCLSpecificType("short");
		for (short j = -10; j<11;j++)
			tShort.addInterestingValues((short)j);
		tShort.addInterestingValues(Short.MAX_VALUE);
		tShort.addInterestingValues((short) (Short.MAX_VALUE-1));
		tShort.addInterestingValues((short)(Short.MAX_VALUE-2));
		tShort.addInterestingValues((short)(Short.MIN_VALUE+2));
		tShort.addInterestingValues((short)(Short.MIN_VALUE+1));
		tShort.addInterestingValues(Short.MIN_VALUE);


		YetiCLSpecificType tInt=new YetiCLSpecificType("int");
		for (int j = -10; j<11;j++)
			tInt.addInterestingValues(j);
		tInt.addInterestingValues(Integer.MAX_VALUE);
		tInt.addInterestingValues(Integer.MAX_VALUE-1);
		tInt.addInterestingValues(Integer.MAX_VALUE-2);
		tInt.addInterestingValues(Integer.MIN_VALUE+2);
		tInt.addInterestingValues(Integer.MIN_VALUE+1);
		tInt.addInterestingValues(Integer.MIN_VALUE);

		YetiCLSpecificType tLong=new YetiCLSpecificType("long");
		for (long j = -10; j<11;j++)
			tLong.addInterestingValues(j);
		tLong.addInterestingValues(Long.MAX_VALUE);
		tLong.addInterestingValues(Long.MAX_VALUE-1);
		tLong.addInterestingValues(Long.MAX_VALUE-2);
		tLong.addInterestingValues(Long.MIN_VALUE+2);
		tLong.addInterestingValues(Long.MIN_VALUE+1);
		tLong.addInterestingValues(Long.MIN_VALUE);
		tLong.addInterestingValues(Integer.MAX_VALUE);
		tLong.addInterestingValues(Integer.MAX_VALUE-1);
		tLong.addInterestingValues(Integer.MAX_VALUE-2);
		tLong.addInterestingValues(Integer.MIN_VALUE+2);
		tLong.addInterestingValues(Integer.MIN_VALUE+1);
		tLong.addInterestingValues(Integer.MIN_VALUE);



		YetiCLSpecificType tDouble=new YetiCLSpecificType("double");
		tDouble.addInterestingValues(Double.MAX_VALUE);
		tDouble.addInterestingValues(Double.MIN_VALUE);
		tDouble.addInterestingValues(Double.NaN);
		tDouble.addInterestingValues(Double.POSITIVE_INFINITY);
		tDouble.addInterestingValues(Double.NEGATIVE_INFINITY);
		tDouble.addInterestingValues(Float.MAX_VALUE);
		tDouble.addInterestingValues(Float.MIN_VALUE);
		tDouble.addInterestingValues(Float.NaN);
		tDouble.addInterestingValues(Float.POSITIVE_INFINITY);
		tDouble.addInterestingValues(Float.NEGATIVE_INFINITY);


		YetiCLSpecificType tChar=new YetiCLSpecificType("char");
		tChar.addInterestingValues('\0');
		tChar.addInterestingValues('\1');
		tChar.addInterestingValues('\2');
		tChar.addInterestingValues('\n');
		tChar.addInterestingValues('\255');
		tChar.addInterestingValues('\254');
		tChar.addInterestingValues('\253');



		YetiCLSpecificType tFloat=new YetiCLSpecificType("float");
		tFloat.addInterestingValues(Float.MAX_VALUE);
		tFloat.addInterestingValues(Float.MIN_VALUE);
		tFloat.addInterestingValues(Float.NaN);
		tFloat.addInterestingValues(Float.POSITIVE_INFINITY);
		tFloat.addInterestingValues(Float.NEGATIVE_INFINITY);	
		
		// add type String
		YetiCLSpecificType tString=new YetiCLSpecificType("String");
		// add interesing value (null) for type String
		tString.addInterestingValues(null);
		
		// add type void
		YetiCLSpecificType tVoid=new YetiCLSpecificType("void");
	}
}
