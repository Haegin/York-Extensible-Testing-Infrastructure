using System;
using System.Collections;
using System.IO;
using System.Reflection;
using System.Net.Sockets;
using System.Net;
using System.Collections.Generic;
using System.Threading;

namespace CsharpReflexiveLayer
{
    /**
 * Class that holds the main of the Csharp application
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Jul 21, 2009
 *
 */
    class YetiCsharp
    {
        //writer of socket
        public static StreamWriter sw = null;
        //reader if socket
        public static StreamReader sr = null;
        //client that receives info from yeti.environments.csharp
        public static TcpClient tc = null;
        static void Main(string[] args)
        {            
            //The socket is in the local host of the machine
            IPAddress ipAddress = System.Net.IPAddress.Parse("127.0.0.1");
            
            YetiCsharpInitializer init = new YetiCsharpInitializer();
            YetiCsharpTestManager tmangr = new YetiCsharpTestManager();   

            tc = new TcpClient("localhost", 2300);
            while (tc == null)
            {
                // sleep (100) to secure syncronization with the Java part
                Thread.Sleep(100);
                tc = new TcpClient("localhost", 2300);
            }          

            //we get the streams of the 2300 socket
            NetworkStream ns = tc.GetStream();
            sw = new StreamWriter(ns);
            sr = new StreamReader(ns);
            //we wait to get the string-message about assemblies to be tested
            String s = YetiSocketConnection.readData();            
            //we clone the received string-message to use it
            //in writing the YetiValuesReport.txt
            String cloneS = s;
            //we get the necessary metadata of the assemblies under test
            init.initialize(s);           
            //unblock the Java application which is waiting, for synchronization
            YetiSocketConnection.sendData("stop");
            //receive data
            String tmps = YetiSocketConnection.readData(); 
            //unblock the Java application
            YetiSocketConnection.sendData("stop");

            //Loop that sends info of the Classes of the assemblies under test
            foreach (YetiCsharpSpecificType spt in init.AllTypes)
            {
                YetiSocketConnection.sendData(spt.ToString());
                                
            }
            YetiSocketConnection.sendData("stop");
            
            //Loop that sends info of the Constructors of the assemblies under test
            foreach (YetiCsharpConstructor cs in init.CreationRoutines)
            {
                YetiSocketConnection.sendData(cs.ToString());

            }
            YetiSocketConnection.sendData("stop");

            //Loop that sends info of the Methods of the assemblies under test
            foreach (YetiCsharpMethod ms in init.MethodRoutines)
            {
                YetiSocketConnection.sendData(ms.ToString());
            }
            YetiSocketConnection.sendData("stop");

            //Loop that sends info of the implemented Interfaces of the assemblies under test
            foreach (YetiCsharpInterface ins in init.AllInterfaces)
            {
                YetiSocketConnection.sendData(ins.ToString());
            }
            YetiSocketConnection.sendData("stop");
 
            //loop that always waits to receive test-cases to execute them            
            while(true)
            {                
                String str = YetiSocketConnection.readData();
                if (str != null)
                {
                    //it stops processign test cases only when "! STOP TESTING !" string
                    //is send by the Java application
                    if (str.Contains("! STOP TESTING !"))
                    {

                        YetiSocketConnection.sendData("stop");                        
                        break;
                    }   
                    //the makeCall executes the test case of the str String-message
                    String str2 = tmangr.makeCall(str);
                    
                    YetiSocketConnection.sendData(str2);                    
                    YetiSocketConnection.sendData("stop");
                }            
                
            }                        
            // writer fir the YetiValuesReport.txt
            StreamWriter outStream = null;
            String testDirectory = "";    
            //we plit the string-message toget the info
            String[] help = cloneS.Split(new Char[] { '=' });
            String[] assies = help[1].Split(new Char[] { ':' });
            //help[0] has the path we need
            testDirectory = help[0].Trim();            
            string outFileName = testDirectory + "YetiValuesReport.txt";
            try
            {
                //if a YetiValuesReport.txt file exists we delete it
                //and create a new one
                if (File.Exists(outFileName))
                {
                    File.Delete(outFileName);                    
                }

                outStream = new StreamWriter(outFileName);       
                // loop traverses the Dictionary collection to write
                // the pairs variable = value --> v111 = 3.22345
                foreach (KeyValuePair<String, Object> kvp in YetiCsharpTestManager.createdValues)
                {
                    try
                    {
                        Object o = kvp.Value;
                        //check if null
                        if (o != null)
                        {
                            //if not null print the pair
                            outStream.WriteLine("{0} = {1}", kvp.Key, kvp.Value);                          
                        }
                        else
                            //else variable = null
                            outStream.WriteLine("{0} = null", kvp.Key);  
                    }
                    catch (Exception e1)
                    {
                        outStream.WriteLine("{0} = {1}", kvp.Key, e1.Message);
                    }
                }              
                outStream.Close();
            }
            catch (Exception e)
            {                
                Console.WriteLine("Reason: {0}", e.ToString());            
            }
            //finally we close the resources relevant to the socket communication
            YetiSocketConnection.closeSocket();
                    
        }

        
    }
}
