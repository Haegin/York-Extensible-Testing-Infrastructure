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

import javax.swing.JPanel;

import yeti.environments.YetiTestManager;


/**
 * Class that represents a testing strategy. 
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public abstract class YetiStrategy {

	/**
	 * Probability to generate a new instance rather than use an old one.
	 */
	public static double NEW_INSTANCES_INJECTION_PROBABILITY=.10;

	/**
	 * The test manager used.
	 */
	@SuppressWarnings("unused")
	private YetiTestManager ytm;
	
	/**
	 * Constructor setting the manager up.
	 * 
	 * @param ytm
	 */
	public YetiStrategy (YetiTestManager ytm){
		this.ytm=ytm;
	}

	
	/**
	 * Method to get all parameters for a routine.
	 * 
	 * @param routine the routine to treat.
	 * @return the array of cards.
	 * @throws ImpossibleToMakeConstructorException exception returned when it is impossible to get all arguments.
	 */
	public abstract YetiCard[] getAllCards(YetiRoutine routine) throws ImpossibleToMakeConstructorException; 
	
	/**
	 * Gets the next routine to test in a given module.
	 * 
	 * @param module the module in which look for the routine.
	 * @return the next routine to test.
	 */
	public abstract YetiRoutine getNextRoutine(YetiModule module);
	
	/**
	 * Gets the card at argumentNumber in the routine.
	 * 
	 * @param routine the routine to test.
	 * @param argumentNumber the argument number.
	 * @return the card found.
	 * @throws ImpossibleToMakeConstructorException in case it is not possible to find it.
	 */
	public abstract YetiCard getNextCard(YetiRoutine routine, int argumentNumber) throws ImpossibleToMakeConstructorException;

	/**
	 * This is to provide the GUI with a preference panel.
	 * 
	 * @return the associated panel
	 */
	public JPanel getPreferencePane() {
		return null;
	}
	
	/**
	 * Returns a nice representation of the strategy name
	 * 
	 * @return the nice name
	 */
	public String getName() {
		return "Generic Strategy";
	}
	
	/**
	 * Resets this strategy.
	 */
	public void reset() {
		
	}
}
