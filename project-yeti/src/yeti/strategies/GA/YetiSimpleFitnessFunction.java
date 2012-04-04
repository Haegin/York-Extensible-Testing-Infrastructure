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

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.jgap.impl.IntegerGene;
import yeti.*;
import yeti.cloud.YetiMap;
import yeti.environments.YetiInitializer;
import yeti.environments.YetiLoader;
import yeti.environments.YetiProgrammingLanguageProperties;
import yeti.environments.YetiTestManager;
import yeti.environments.commandline.YetiCLInitializer;
import yeti.environments.commandline.YetiCLLogProcessor;
import yeti.environments.commandline.YetiCLProperties;
import yeti.environments.commandline.YetiCLTestManager;
import yeti.environments.csharp.*;
import yeti.environments.java.*;
import yeti.environments.pharo.YetiPharoCommunicator;
import yeti.environments.pharo.YetiPharoTestManager;
import yeti.monitoring.YetiGUI;
import yeti.stats.YetiDataSet;
import yeti.stats.YetiMichaelisMentenEquation;
import yeti.strategies.*;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Lucas Serpa Silva
 * Date: Apr 23, 2011
 * Time: 12:15:29 PM
 */
public class YetiSimpleFitnessFunction extends FitnessFunction {
    private YetiGAParameters parameters;


    public YetiSimpleFitnessFunction(YetiGAParameters parameters) {
        this.parameters = parameters;
    }

    public double evaluate(IChromosome a_subject) {
        String[] arguments={"-java","-time="+this.parameters.gaEvaluationTime,"-testModules="+this.parameters.gaEvaluationModules,"-nologs","-chromosome=test"};
        Yeti.chromosome = a_subject;
        Yeti.YetiRun(arguments);
        return Yeti.report.getnErrors();
    }
}
