package yeti.strategies;

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

import yeti.ImpossibleToMakeConstructorException;
import yeti.NoCreationRoutineInType;
import yeti.YetiCard;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiRoutine;
import yeti.YetiStrategy;
import yeti.YetiType;
import yeti.YetiVariable;
import yeti.environments.YetiTestManager;

/**
 * Class that represents a purely random strategy.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 *
 */
public class YetiRandomStrategy extends YetiStrategy{
	

	/**
	 * The maximum number of recursions allowed for creating arguments to a method.
	 */
	public static int MAX_RECURSIVE_RANK=20;

	/**
	 * Creates this strategy.
	 * 
	 * @param ytm the test manager used.
	 */
	public YetiRandomStrategy(YetiTestManager ytm) {
		super(ytm);
	}

	/* (non-Javadoc)
	 * 
	 * Standard strategy, get the arguments.
	 * 
	 * @see yeti.YetiStrategy#getAllCards(yeti.YetiRoutine)
	 */
	public YetiCard[] getAllCards(YetiRoutine routine) throws ImpossibleToMakeConstructorException{
		int length = routine.getOpenSlots().length;
		YetiCard[] yt=new YetiCard[length];
		
		// for all open slots we try to get a corresponding instance
		for (int i=0; i<length; i++){
			YetiLog.printDebugLog("trying to get argument "+ i, this);
			yt[i]=getNextCard(routine, i, 0);
		}
		return yt;
	}

	/**
	 * Get all the arguments with the level of recursion.
	 * 
	 * @param routine the routine to test.
	 * @param recursiveRank the rank of recursion
	 * @return an array with all arguments.
	 * @throws ImpossibleToMakeConstructorException when it is impossible to construct all argument givent hte level of recursion.
	 */
	public YetiCard[] getAllCards(YetiRoutine routine, int recursiveRank) throws ImpossibleToMakeConstructorException{
		
		// we test for the level of recursion
		if (recursiveRank>=MAX_RECURSIVE_RANK)
			throw new ImpossibleToMakeConstructorException(routine.getClass().getName());
		int length = routine.getOpenSlots().length;
		YetiCard[] yt=new YetiCard[length];
		
		// we try to get all arguments
		for (int i=0; i<length; i++){
			YetiLog.printDebugLog("trying to get argument "+ i, this);
			yt[i]=getNextCard(routine, i, recursiveRank+1);
		}
		return yt;
	}
	
	
	
	/**
	 * Tries to find or generate one argument.
	 * 
	 * @param routine the routine to test.
	 * @param argumentNumber the slot number.
	 * @param recursiveRank the rank of recursion at which we are.
	 * @return the argument to use.
	 * @throws ImpossibleToMakeConstructorException when there is no possibility to generate the argument.
	 */
	public YetiCard getNextCard(YetiRoutine routine, int argumentNumber, int recursiveRank) throws ImpossibleToMakeConstructorException {

		// we test first
		if (recursiveRank>=MAX_RECURSIVE_RANK)
			throw new ImpossibleToMakeConstructorException(routine.getClass().getName());

		YetiType type=routine.getOpenSlots()[argumentNumber];
		YetiLog.printDebugLog(routine.getName().getValue(),this);
		
		// we try to get a random instance from the pool
		if ((type.instances.size()>0)&&(Math.random()>NEW_INSTANCES_INJECTION_PROBABILITY)){
			return type.getRandomInstance();
		}		
		YetiRoutine creator;
		
		// we try to create that random instance
		try {
			creator = type.getRandomCreationRoutine();
		} catch (NoCreationRoutineInType e) {
			throw new ImpossibleToMakeConstructorException(e.getMessage());
		}
		YetiCard[] creatorCards=getAllCards(creator,recursiveRank);
		if (creatorCards.length>0)
			YetiLog.printDebugLog("Getting creation routine of type "+type.getName()+" with arguments "+creatorCards[0]+"...", this);
		else
			YetiLog.printDebugLog("Getting creation routine of type "+type.getName()+" with no arguments ", this);
			
		// we make a call to a constructor
		YetiVariable result = (YetiVariable)creator.makeCall(creatorCards);
		if (result!=null)
			return result;
		else 
			return getNextCard( routine, argumentNumber, recursiveRank+1);
	}

	/* (non-Javadoc)
	 * Standard get next routine method.
	 * 
	 * @see yeti.YetiStrategy#getNextRoutine(yeti.YetiModule)
	 */
	@Override
	public YetiRoutine getNextRoutine(YetiModule module) {
		YetiRoutine r= module.getRoutineAtRandom();
		YetiLog.printDebugLog("Testing routine "+r+" from module "+module.getModuleName(), this);
		return r;
	}

	/* (non-Javadoc)
	 * standard get next argument method.
	 * 
	 * @see yeti.YetiStrategy#getNextCard(yeti.YetiRoutine, int)
	 */
	@Override
	public YetiCard getNextCard(YetiRoutine routine, int argumentNumber) throws ImpossibleToMakeConstructorException {

		YetiType type=routine.getOpenSlots()[argumentNumber];
		YetiLog.printDebugLog(routine.getName().getValue(),this);
		
		// we try to get a random argument.
		if (type.instances.size()>0){
			return type.getRandomInstance();
		}		
		YetiRoutine creator;
		try {
			// we get a creation routine at random.
			creator = type.getRandomCreationRoutine();
		} catch (NoCreationRoutineInType e) {
			throw new ImpossibleToMakeConstructorException(e.getMessage());
		}
		// we try to get all the arguments to the creation routine
		YetiCard[] creatorCards=getAllCards(creator);
		if (creatorCards.length>0)
			YetiLog.printDebugLog("Getting creation routine of type "+type.getName()+" with arguments "+creatorCards[0]+"...", this);
		else
			YetiLog.printDebugLog("Getting creation routine of type "+type.getName()+" with no arguments ", this);
			
		// we make the call
		YetiVariable result = (YetiVariable)creator.makeCall(creatorCards);
		if (result!=null)
			return result;
		else 
			// we try to call again in case we could not generate the routine.
			return getNextCard( routine, argumentNumber);
		
	}
	
	@Override
	public String getName() {
		return "Random Strategy";
	}


}
