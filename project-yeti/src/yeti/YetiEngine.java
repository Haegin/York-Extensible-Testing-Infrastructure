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
import java.util.Date;

import yeti.environments.YetiTestManager;

/**
 * This class is here to manage how the tests are run. It contains several methods that have have several ways of stopping testing. 
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public class YetiEngine {
	
		/**
		 * A progress value between 1 and 100.
		 */
		int progress = 0;
	
		/**
		 * Simple getter for the progress.
		 * 
		 * @return the value of the progress variable (between 0 and 100).
		 */
		public int getProgress() {
			return progress;
		}


		/**
		 * A simple setter for the progress variable.
		 * 
		 * @param progress the value of the progress variable.
		 */
		public void setProgress(int progress) {
			this.progress = progress;
			YetiLog.printDebugLog("YETI Testing session: "+progress+"%", this);
		}


		/**
		 * The strategy that will be used
		 */
		protected YetiStrategy strategy;
		
		/**
		 * The manager to use for making calls
		 */
		protected YetiTestManager manager;
		
		
		/**
		 * A constructor for the engine.
		 * Simply assigns the two parameters.
		 * 
		 * @param strategy the strategy to use for this testing session
		 * @param manager the manager for making calls
		 */
		public YetiEngine(YetiStrategy strategy, YetiTestManager manager){
			this.strategy=strategy;
			this.manager=manager;
		}
		
	
		/**
		 * Method allowing to test a module for a number of minutes.
		 * 
		 * Performance: on the Java implementation, 1 mn generate hundreds of thousands of method calls.
		 * 
		 * @param mod the module to test
		 * @param minutes the number of minutes to test it
		 */
		public void testModuleForNMinutes(YetiModule mod, int minutes){
			this.testModuleForNSeconds(mod, 60*minutes);
		}

		/**
		 * Method allowing to test a module for a number of seconds.
		 * 
		 * Performance: on the Java implementation, 10 seconds generate more than a hundred thousand method calls.
		 * 
		 * @param mod the module to test
		 * @param seconds the number of seconds to test it
		 */
		public void testModuleForNSeconds(YetiModule mod, int seconds){
			// we take the start time
			long startTime = new Date().getTime();
			// we generate the end time using a long representation
			long endTime = startTime+seconds*1000;
			
			// steps
			long step = (endTime-startTime)/100L;
			
			// the last step
			long lastStep = startTime;
			
			// the current time
			long currentTime = startTime;
			
			// at each iteration we test the current time
			while (currentTime<endTime&&!shouldStopTesting()){
				if (currentTime>lastStep+step) {
					this.setProgress((int)((100L*(currentTime-startTime))/(endTime-startTime)));
					lastStep = lastStep+step;
				}
				manager.makeNextCall(mod, strategy);
				currentTime=new Date().getTime();
			}
			this.setProgress(100);
			manager.stopTesting();
		}

		
		/**
		 * Method allowing to test a module for a number of tests.
		 * 
		 * Performance: on the Java implementation, 500 tests is almost immediate.

		 * @param mod the module to test
		 * @param number the number of tests to make
		 */
		public void testModuleForNumberOfTests(YetiModule mod, int number){
			int nTests=number;
			
			// steps
			long step = number/100;
			
			// the last step
			long lastStep = 0;
	
			// we simplty iterate through the calls
			while (nTests-->0&&!shouldStopTesting()){
				if ((number-nTests)>(lastStep+step)) {
					this.setProgress(((number-nTests)*100)/number);
					lastStep = lastStep+step;
				}
				manager.makeNextCall(mod, strategy);
			}
			this.setProgress(100);
			manager.stopTesting();
		}


		/**
		 * Getter for the manager.
		 * @return  the manager of this engine
		 */
		public YetiTestManager getManager() {
			return manager;
		}


		/**
		 * Setter for the manager.
		 * @param manager  the manager to set.
		 */
		public void setManager(YetiTestManager manager) {
			this.manager = manager;
		}


		/**
		 * Getter for the strategy.
		 * @return  the strategy.
		 */
		public YetiStrategy getStrategy() {
			return strategy;
		}


		/**
		 * Setter for the strategy.
		 * @param strategy  the strategy to set.
		 */
		public void setStrategy(YetiStrategy strategy) {
			this.strategy = strategy;
		}
		
		private boolean stopTesting = false;
		/**
		 * Checks if we should stop testing.
		 * 
		 * @return true if needs to stop testing.
		 */
		public boolean shouldStopTesting() {
			return stopTesting;
		}
		
		/**
		 * Call this method to stop testing.
		 */
		public void stopTesting() {
			stopTesting = true;
		}

}
