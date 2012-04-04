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
 * Class that represents a report for a Yeti session.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Apr 7, 2011
 *
 */
public class YetiReport {

	private String moduleNames = null;
	private long nTests=0;
	private long time=0;
	private long nErrors=0;
	private double Max = 0.0;
	private double K = 0.0;
	private double R2=0.0;
	private double SSErr=0.0;
	private double SSTot=0.0;
	private double branchCoverage=0;
	


	/**
	 * A simple constructor.
	 * 
	 * @param moduleNames The tested modules.
	 * @param nTests The number of tests.
	 * @param time The time spent testing.
	 * @param nErrors The number of errors.
	 */
	public YetiReport(String moduleNames, long nTests, long time, long nErrors) {
		super();
		this.moduleNames = moduleNames;
		this.nTests = nTests;
		this.time = time;
		this.nErrors = nErrors;
	}

	/**
	 * A simple getter. 
	 * 
	 * @return the value of module names.
	 */
	public String getModuleNames() {
		return moduleNames;
	}

	/**
	 * A simple setter.
	 * 
	 * @param moduleNames the module names to set.
	 */
	public void setModuleNames(String moduleNames) {
		this.moduleNames = moduleNames;
	}

	/**
	 * A simple getter. 
	 * 
	 * @return the value of number of tests.
	 */
	public long getnTests() {
		return nTests;
	}

	/**
	 * A simple setter.
	 * 
	 * @param nTests the number of tests.
	 */
	public void setnTests(long nTests) {
		this.nTests = nTests;
	}
	
	/**
	 * A simple getter. 
	 * 
	 * @return the value of the time spent testing.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * A simple setter
	 * 
	 * @param time the time spent testing
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * A simple getter. 
	 * 
	 * @return the value of number of errors.
	 */
	public long getnErrors() {
		return nErrors;
	}

	/**
	 * A simple setter.
	 * 
	 * @param nErrors the number of errors uncovered.
	 */
	public void setnErrors(long nErrors) {
		this.nErrors = nErrors;
	}

	/**
	 * A simple getter. 
	 * 
	 * @return the value of Max.
	 */
	public double getMax() {
		return Max;
	}

	/**
	 * A simple setter.
	 * 
	 * @param max the value for Max
	 */
	public void setMax(double max) {
		Max = max;
	}

	/**
	 * A simple getter. 
	 * 
	 * @return the value of K.
	 */
	public double getK() {
		return K;
	}

	/**
	 * A simple setter.
	 * 
	 * @param k the value for k.
	 */
	public void setK(double k) {
		K = k;
	}

	/**
	 * A simple getter. 
	 * 
	 * @return the value of R2.
	 */
	public double getR2() {
		return R2;
	}

	/**
	 * A simple setter.
	 * 
	 * @param r2 the value for the residuals.
	 */
	public void setR2(double r2) {
		R2 = r2;
	}

	/**
	 * A simple getter. 
	 * 
	 * @return the value of SSErr.
	 */
	public double getSSErr() {
		return SSErr;
	}

	/**
	 * A simple setter.
	 * 
	 * @param SSErr the value of SSErr.
	 */
	public void setSSErr(double SSErr) {
		this.SSErr = SSErr;
	}

	/**
	 * A simple getter. 
	 * 
	 * @return the value of SSTot.
	 */
	public double getSSTot() {
		return SSTot;
	}

	/**
	 * A simple setter for 
	 * 
	 * @param SSTot the value of SSTot.
	 */
	public void setSSTot(double SSTot) {
		this.SSTot = SSTot;
	}
	
	/**
	 * Simple getter for the branch coverage.
	 * 
	 * @return the coverage.
	 */
	public double getBranchCoverage() {
		return branchCoverage;
	}

	/**
	 * Simple setter for branch coverage.
	 * 
	 * @param branchCoverage set branch coverage.
	 */
	public void setBranchCoverage(double branchCoverage) {
		this.branchCoverage = branchCoverage;
	}

	@Override
	public String toString() {
		return getModuleNames()+","+getMax()+","+getK()+","+getSSErr()+","+getSSTot()+","+getR2()+","+getnTests()+","+getTime()+","+getnErrors();
	}

	
}
