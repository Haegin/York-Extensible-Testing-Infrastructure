package yeti;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import yeti.environments.java.YetiJavaMethod;

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
 * Class that represents cards. A card is a placeholder either for variables or wildcards. A wildcard for example, can be used to generate primitive types values on-the-fly.
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public class YetiCard {

	/**
	 * The identity of the card.
	 */
	protected YetiIdentifier identity=null;

	/**
	 * The type of the card.
	 */
	protected YetiType type;

	/**
	 * The value of the card.
	 */
	protected Object value; //=null;

	/**
	 * Getter for the identity.
	 * @return  the identity of the card.
	 */
	public YetiIdentifier getIdentity(){
		return identity;
	}

	/**
	 * Getter for the type.
	 * @return  the type of this card.
	 */
	public YetiType getType(){
		return type;
	}


	/**
	 * Getter for the value of this card.
	 * @return  the value of the card.
	 */
	public Object getValue(){
		return value;
	}

	/**
	 * Creation procedure for the class.
	 * 
	 * @param id the identity to use for the class.
	 * @param t the type of this card.
	 * @param value the value of this card.
	 */
	public YetiCard(YetiIdentifier id, YetiType t, Object value){
		identity=id;
		type=t;
		this.value=value;

	}

	/**
	 * Creation procedure for the class using only a type.
	 * This is intended to represent null values for that type.
	 * 
	 * @param id the identity to use for the class.
	 * @param t the type of this card.
	 * @param value the value of this card.
	 */
	public YetiCard(YetiType t){
		type=t;
	}

	/* 
	 * (non-Javadoc)
	 * Override of the <code>toString()</code> method.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return type.name+" "+identity.value;
	}

	/**
	 * Setter for the identity of the card
	 * @param identity  the identity of the card
	 */
	public void setIdentity(YetiIdentifier identity) {
		this.identity = identity;
	}

	/**
	 * Setter for the type of the card.
	 * @param type  the type of the card.
	 */
	public void setType(YetiType type) {
		this.type = type;
	}

	/**
	 * Setter for the value of the card.
	 * @param value  the value of the card.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * A method that returns the variable part of the card
	 * 
	 * @return the String representation of the variable part of the card
	 */
	public String toStringVariable() {
		// here we generate the variable name
		if (value==null) return "("+this.getType().toString()+")null";
		try {
			String loggedValue = YetiJavaMethod.getJavaCodeRepresentation(value);
			if (loggedValue!=null) {
				return "("+this.getType().toString()+")"+loggedValue;
			}
		} catch(Throwable t) {}
		return "("+this.getType().toString()+")"+this.getIdentity().toString();
	}

	/**
	 * A method that returns the prefix part of the card
	 * 
	 * @return the String representation of the prefix part of the card
	 */
	public String toStringPrefix() {
		if (value==null) return "";
		String valueAsAString = null;
		try {
			valueAsAString = value.toString();
		} catch (Throwable t){
			valueAsAString = "** error while computing toString() representation**";
		}
		String loggedValue = YetiJavaMethod.getJavaCodeRepresentation(value);
		// if there is no direct literal representation
		if (loggedValue!=null) {
			return "";
		}
		try {
			// otherwise we try to serialize it and build one
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			String serializedValue= "";
			try {
				oos.writeObject(this.value);
				byte []serializedForm = baos.toByteArray();
				baos.reset();
				serializedValue= "{(byte)"+serializedForm[0];
				for (int i=1;i<serializedForm.length;i++) {
					serializedValue=serializedValue+",(byte)"+serializedForm[i];
				}
				serializedValue=serializedValue+"};// class is not serializable, toString() returns: "+valueAsAString;

			} catch (java.io.NotSerializableException e) {
				// If we cannot build one we simply generate an empty byte array and return the toString value
				serializedValue="{};// class is not serializable, toString() returns: "+valueAsAString;
			}
			// we store this into a temporary variable ending with _bytes
			String serializedValueName = this.getIdentity().toString()+"_bytes";
			String serializedVariable = "byte []"+serializedValueName+"="+serializedValue;
			
			// we build the code to deserialize it
			String result=serializedVariable +"\n"+this.getType().toString()+" "+this.getIdentity().toString()+"=("+this.getType().toString()+")(new ObjectInputStream(new ByteArrayInputStream(";
			result=result+serializedValueName+")).readObject());";
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return "";
	}

}
