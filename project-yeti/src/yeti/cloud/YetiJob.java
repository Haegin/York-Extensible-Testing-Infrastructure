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

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;

/**
 * Class that represents the main launching class of Yeti in distributed mode
 * 
 * @author Faheem Ullah (fu500@cs.york.ac.uk)
 * @date August 20, 2009
 */

public class YetiJob {
	/**
	 * The main method of yeti in distributed mode.
	 *  It receives two arguments. These are:
	 *  -The Input Path for YetiMap
	 *  -The output path for YetiReducer.
	 *  @param args the arguments of the program
	 */
	public static void main(String[] args) {
		
		if (args.length<2){
			System.err.println("Error! Arguments must be provided for input and output path");
			System.err.println("Usage: bin/hadoop jar yeti.jar input_path output_path output_path");
			return;
		}
		
		//The job client to interact with the JobTracker for this job
		JobClient client = new JobClient();
		//setting up the job configuration for YetiJob
		JobConf conf= new JobConf(yeti.cloud.YetiJob.class);
		conf.setJobName("Yeti");
			
		//The input data would be read as a line of text at at time, separated by newlines
		conf.setInputFormat(TextInputFormat.class);
		//The output data would be written in the form of text - new line for each unique key, value pair
		conf.setOutputFormat(TextOutputFormat.class);
		
		//we set the Reducer class for Yeti
		conf.setMapperClass(YetiMap.class);
		conf.setReducerClass(YetiReducer.class);
		
		//the output of Yeti would be Text for the Key
		conf.setOutputKeyClass(Text.class);
		//the output of Yeti would be a Number for the Value
		conf.setOutputValueClass(Text.class);
		//The Input path for YetiMap to read file(s) containingthe input parameters
		FileInputFormat.setInputPaths(conf, new Path (args[0]));
			
		//The output path for YetiReducer to write the final output
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		
		
		conf.setNumReduceTasks(0);
		
		try{
			
			JobClient jc= new JobClient(conf);
			RunningJob job = jc.runJob(conf);
				
		}catch (IOException e){
			e.printStackTrace();
			
		}

	}
}

