package yeti.test;

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

import java.util.Vector;

import yeti.YetiRoutine;

/**
 * Class used to test the Yeti implementation.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 *
 * @param <T>
 */
public class YetiTest<T extends YetiRoutine> {
	
	public YetiTest(){
		System.out.println("YetiTest constructor called");
	}
	@SuppressWarnings("unchecked")
	public YetiTest(YetiTest yt){
		System.out.println("YetiTest(YetiTest yt) constructor called");
	}

	@SuppressWarnings("unchecked")
	public YetiTest(YetiTest yt, YetiTest yt2){
		System.out.println("YetiTest(YetiTest yt, YetiTest yt2) constructor called");
	}

	
	public void printRandomDouble(){
		System.out.println(Math.random());
	}
	public void printRandomFloat(float f){
		System.out.println((float)(Math.random()));
	}

	public void printByte(byte f){
		byte v3=57;
		System.out.println(f);
		System.out.println(v3);
	}

	public void printChar(char c){
		assert c!='m';
		System.out.println(c);
	}

	public void printInt(int a){
		assert a!=1;
		System.out.println((int)(1/a));
	}
	public T genT(Vector<String> t){
	
	return null;
	}

	
	public static int [] genInt2(){
		int [] is = {10};
		return is ;
	}
	
	public int genInt(){
		return 0;
	}

	public int genIntFake(){
		while (true) {}
	}
	public String toString(){
		return "a YetiTest";
	}

	
}
