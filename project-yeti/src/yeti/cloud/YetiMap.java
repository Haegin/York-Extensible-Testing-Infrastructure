package yeti.cloud;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import yeti.Yeti;
import yeti.YetiLogProcessor;

/**
 * Class that represents the Maper for Yeti to run on cloud. 
 * It reads the contents of file(s) containing all the parameters for YETI one file at a time and passes them on to YETI 
 * 
 * @author Faheem Ullah (fu500@cs.york.ac.uk)
 * @date August 20, 2009
 */

public class YetiMap extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>  {
	
	public static HashMap<String, Object> listOfExceptions= new HashMap<String,Object>();
	public static String moduleName;
	/**
	 * The map method, this will serve as a single map job and will be run on a single machine within the cloud
	 * Each machine will get a map job to run YETI with the specified line of parameters
	 * @param 
	 * key = the line number read<br>
	 * value = the actual line contents<br>
	 * OutputCollector = Output will be collected in the form of <Text, Int><br>
	 * Reporter = to report the status of current process<br>
	 */
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		
		//splitting the lines into parameters based on white spaces
		String params [] = value.toString().split("\\x20");
		
		//set up the distributed mode
		Yeti.isDistributed=true;
		
		//passing parameters (Command line arguments) to Yeti
		Yeti.YetiRun(params);
		
		String outputKey="";

		YetiLogProcessor lp = (YetiLogProcessor)Yeti.pl.getLogProcessor();
		int uniqueBugs= lp.getNumberOfUniqueFaults();

		Iterator<String> it =listOfExceptions.keySet().iterator(); 
		while(it.hasNext())
			outputKey+=it.next().toString()+"@\n";
		
		output.collect(new Text (moduleName+"\n"+outputKey), new IntWritable(uniqueBugs));
	}
}
