package yeti;

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

/**
 * Class that represents an identity on Yeti
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public class YetiIdentifier {
	
	
	/**
	 * The value of the identity (usually a string representing a variable name)
	 */
	protected String value;
	
	/**
	 * The index to generate fresh names.
	 */
	protected static long currentIndex=0;
	
	/**
	 * Constructor of the identifier using a clear name.
	 * 
	 * @param value the name to be used.
	 */
	public YetiIdentifier(String value) {
		super();
		this.value = value;
	}
	
	
	
	/* (non-Javadoc)
	 * 
	 * Overriding of the toString method. Standard implementation returns 
	 * the value of the string representing the identifier.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value;
	}



	/**
	 * Return a fresh identifier with a fresh name (implementation may vary).
	 * 
	 * @return the new identifyer.
	 */
	public static YetiIdentifier getFreshIdentifier(){
		return new YetiIdentifier("v"+(currentIndex++));
	}



	/**
	 * Getter for the index.
	 * @return  The current index.
	 */
	public static long getCurrentIndex() {
		return currentIndex;
	}



	/**
	 * Setter for the current index.
	 * @param currentIndex  the index to set.
	 */
	public static void setCurrentIndex(long currentIndex) {
		YetiIdentifier.currentIndex = currentIndex;
	}



	/**
	 * Getter for the value of the identifier.
	 * @return  the value of the identifier.
	 */
	public String getValue() {
		return value;
	}



	/**
	 * Setter for the value of the identifier.
	 * @param value  the value of the identifier.
	 */
	public void setValue(String value) {
		this.value = value;
	}



	/**
	 * Resets this class.
	 */
	public static void reset() {
		currentIndex = 0;
	}

}
