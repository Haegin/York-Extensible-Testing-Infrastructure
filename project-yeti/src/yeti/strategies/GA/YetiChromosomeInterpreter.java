package yeti.strategies.GA;

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

import org.jgap.IChromosome;
import org.jgap.impl.IntegerGene;
import yeti.YetiLog;

/**
 * Created by IntelliJ IDEA.
 * User: slucas
 * Date: 4/28/11
 * Time: 9:31 PM
 */
public class YetiChromosomeInterpreter {
    int currentIndex;
    IChromosome chromosome;

    public YetiChromosomeInterpreter(IChromosome aChromosome) {
        this.chromosome = aChromosome;
        currentIndex = 0;
        printSolution(aChromosome);

    }

    public int getNextMethodCall() {
        int result = 0;
        if (currentIndex < chromosome.size()) {
            IntegerGene gene = (IntegerGene)chromosome.getGene(currentIndex);
            result = gene.intValue();
            currentIndex++;
        }

        return result;
    }

    //TODO make the sections in the chromosome
    public int getNextMethodCallParameter() {
        int result = 0;
        if (currentIndex < chromosome.size()) {
            IntegerGene gene = (IntegerGene)chromosome.getGene(currentIndex);
            result = gene.intValue();
            currentIndex++;
        }

        return result;

    }


     public void printSolution(IChromosome a_solution) {
        YetiLog.printDebugLog("Printing the chromosome of the Solution with size" + a_solution.size(), this);
        String chromosome = "";
        for (int i = 0; i < a_solution.size(); i++) {
            IntegerGene gene = (IntegerGene)a_solution.getGene(i);
            chromosome = chromosome.concat(gene.intValue() +",");
        }

        System.out.println("Chromosome = "+chromosome);
    }

}
