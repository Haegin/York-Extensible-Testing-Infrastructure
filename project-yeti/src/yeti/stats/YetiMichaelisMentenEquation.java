package yeti.stats;

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
 * Class that represents a Michaelis-Menten function. 
 * Such a function is of the form: (Max*x)/(K+x)
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Mar 24, 2011
 *
 */
public class YetiMichaelisMentenEquation extends YetiEquation {

	/**
	 * The Max in the equation.
	 */
	private double Max;


	/**
	 * The K in the equation.
	 */
	private double K;

	/**
	 * Simple constructor for the function.
	 * 
	 * @param Max the Max constant.
	 * @param K the K constant.
	 */
	public YetiMichaelisMentenEquation(double Max, double K) {
		
		this.Max=Max;
		if (K!=0.0) this.K=K;
		else this.K = .00001;

	}

	/* 
	 * Returns the simple value for the equation.
	 * 
	 * @see yeti.stats.YetiEquation#valueOf(double)
	 */
	@Override
	double valueOf(double x) {
		return (Max*x)/(K+x);
	}

	/**
	 * Simple getter for Max
	 * 
	 * @return the value of Max
	 */
	public double getMax() {
		return Max;
	}

	/**
	 * Simple setter for Max
	 * 
	 * @param max the new Max
	 */
	public void setMax(double max) {
		Max = max;
	}

	/**
	 * Simple getter for K
	 * 
	 * @return the value of K
	 */	public double getK() {
		 return K;
	 }

	 /**
	  * Simple setter for Max
	  * 
	  * @param max the new Max
	  */
	 public void setK(double k) {
		 K = k;
	 }

	 /* 
	  * Presents a nice String representation of the equation f(x)=(Max*x)/(K+x)
	  * 
	  * @see java.lang.Object#toString()
	  */
	 public String toString() {
		 return "f(x)=("+Max+"*x)/("+K+"+x)";

	 }
}
