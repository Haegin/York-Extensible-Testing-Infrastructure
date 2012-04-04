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

import org.jgap.Configuration;
import org.jgap.IChromosome;
import org.jgap.xml.XMLManager;
import org.w3c.dom.Document;
import java.io.File;


/**
 * Created by IntelliJ IDEA.
 * User: slucas
 * Date: 4/30/11
 * Time: 10:01 AM
 */
public class YetiStrategyPersistenceManager {

    public static void saveChromosome( IChromosome aChromosome, String filePath ) throws Exception {
        // Convert the Genotype to a DOM object.
        // -------------------------------------
        Document xmlRepresentation = XMLManager.representChromosomeAsDocument(aChromosome);
        XMLManager.writeFile(xmlRepresentation, new File(filePath));
    }


    public static IChromosome loadChromosome(Configuration aConfiguration, String filePath ) throws Exception {
        Document chromosomeDoc = XMLManager.readFile(new File(filePath));
        return XMLManager.getChromosomeFromDocument(aConfiguration,chromosomeDoc);
    }
}
