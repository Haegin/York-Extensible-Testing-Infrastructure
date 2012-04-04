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

import org.jgap.*;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.SwappingMutationOperator;
import yeti.YetiLog;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Lucas Serpa Silva
 * Date: Apr 20, 2011
 * Time: 10:44:40 PM
 */
public class YetiStrategyOptimizer {


    /**
     * Genetic Algorithm configuration with the mutation
     * fitness function
     */
    public Configuration gaConfiguration;

    /**
     * Genotype is a set of chromosomes
     */
    public Genotype gaGenotype;


    /**
     * The parameters that controls how the execution should be executed
     */
    public YetiGAParameters parameters;


    public YetiStrategyOptimizer(YetiGAParameters parameters) {
        this.parameters = parameters;

    }

    public void evolveStrategy() {
        YetiLog.printDebugLog("---===>Evolution started",this);

        try {
            createGAConfiguration();
            createGAGenotype();
            doEvolution(this.gaGenotype);

            YetiLog.printDebugLog("---===>Evolution finished", this);
            YetiLog.printDebugLog("---===>Saving Chromosome to " + parameters.getGaFittestChromosomeOutPutPath(), this);
            YetiStrategyPersistenceManager.saveChromosome(this.gaGenotype.getFittestChromosome(),parameters.getGaFittestChromosomeOutPutPath());
            printSolution(this.gaGenotype.getFittestChromosome());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Here we have to set the GA operators and the evolution parameters
     */
    public void createGAConfiguration () {
        if (this.gaConfiguration == null) {
            try {
                this.gaConfiguration = new DefaultConfiguration();
                Configuration.resetProperty(Configuration.PROPERTY_FITEVAL_INST);

                gaConfiguration.setFitnessEvaluator(new DeltaFitnessEvaluator());
                // Just use a swapping operator instead of mutation and others.
                // ------------------------------------------------------------
                gaConfiguration.getGeneticOperators().clear();
                SwappingMutationOperator swapper = new SwappingMutationOperator(gaConfiguration);
                gaConfiguration.addGeneticOperator(swapper);
                // Setup some other parameters.
                // ----------------------------
                gaConfiguration.setPreservFittestIndividual(true);
                gaConfiguration.setKeepPopulationSizeConstant(false);
                // Set number of individuals (=tries) per generation.
                // --------------------------------------------------
                gaConfiguration.setPopulationSize(YetiGAParameters.GA_POPULATION_SIZE);
                gaConfiguration.setSampleChromosome(createChromosome(gaConfiguration));
                //At the moment the fitness function is only using the number of faults
                gaConfiguration.setFitnessFunction(new YetiSimpleFitnessFunction(this.parameters));

            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
                System.exit( -2);
            }
        }
    }


    /**
     * The goal of this method is to set the alleles of the population.
     * Since the population is randomly initialize we have to set alleles here.
     * TODO: Improve the initialization (Interesting values, etc..)
     */
    public void createGAGenotype() {
        assert(this.gaConfiguration != null): "The GAConfiguration must not be null";

        if (this.gaGenotype == null) {
            try {
                this.gaGenotype = Genotype.randomInitialGenotype(gaConfiguration);

                //FIXME: lssilva this alleles have to be specified according to
                //the chromosome.
                List chromosomes = this.gaGenotype.getPopulation().getChromosomes();
                for (int i = 0; i < chromosomes.size(); i++) {
                    IChromosome chrom = (IChromosome) chromosomes.get(i);
                    for (int j = 0; j < chrom.size(); j++) {
                        Gene gene = (Gene) chrom.getGene(j);
                        gene.setAllele(j);
                    }
                }
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
                System.exit( -2);
            }
        }
    }


    /**
     * Does the evolution until finished.
     */
    public void doEvolution(Genotype a_genotype) {
        int percentEvolution = YetiGAParameters.GA_NUMBER_GENERATION / 100;
        for (int i = 0; i < YetiGAParameters.GA_NUMBER_GENERATION; i++) {
            a_genotype.evolve();
        }

        YetiLog.printDebugLog("Evolution finished ",this);
    }




    /**
     * @param a_solution of the best chromosome.
     */
    public void printSolution(IChromosome a_solution) {
        YetiLog.printDebugLog("Printing the chromosome of the Solution with size" + a_solution.size(),this);
        String chromosome = "";
        for (int i = 0; i < a_solution.size(); i++) {
            IntegerGene gene = (IntegerGene)a_solution.getGene(i);
            chromosome = chromosome.concat(gene.intValue() +",");
        }

        YetiLog.printDebugLog("Chromosome = "+chromosome,this);
    }



    /**
     * At the moment this is just a sample chromosome but it ought to encode the parameters for the
     * testing session
     * @param gaConf to create the chromosome
     * @return a chromosome
     */
    public IChromosome createChromosome(Configuration gaConf) {

        IChromosome sampleChromosome = null;
        try {
            sampleChromosome = new Chromosome(gaConf,YetiGAParameters.GA_CHROMOSOME_SIZE);
            Gene[] gene  = new IntegerGene[YetiGAParameters.GA_CHROMOSOME_SIZE];
            for (int i = 0; i < gene.length; i++) {
                gene[i] = new IntegerGene(gaConf, 0, 1000); //Alleles max num methods
            }
            sampleChromosome.setGenes(gene);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return sampleChromosome;
    }
}
