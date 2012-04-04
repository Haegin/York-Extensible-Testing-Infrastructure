using System;
using System.Collections;
using System.IO;
using System.Net.Sockets;
using System.Net;
using System.Threading;

namespace CsharpReflexiveLayer
{
    /**
 * Class responsible for having the conection through
     * sockets with the Java part
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Jul 21, 2009
 *
 */
    class YetiSocketConnection
    {
        //const int echoPort = 2000;
        //The method that sends string-messages to the Java part (port 2300)
        public static void sendData(String message)
        {
            //TcpClient tc = null;
            
            //using (tc=new TcpClient("localhost", soc))
            //{
            //Console.WriteLine("CSHARP SW 1");
                try
                {                    
//                    NetworkStream ns = tc.GetStream();
                    StreamWriter sw = YetiCsharp.sw;
                    //Console.WriteLine("CSHARP SW 2 -> "+message);
                    sw.WriteLine(message);
                    sw.Flush();
//                    sw.Close();
                    
                }
                catch (Exception e)
                {
                    Console.WriteLine("Exception caught.");
                    Console.WriteLine("Source: " + e.Source);
                    Console.WriteLine("Message: " + e.Message);
                    Console.WriteLine(e.StackTrace);
                    string tmp = e.StackTrace;

                }
           // }
        }

        //The method that receives the string-messages of the Java part
        public static String readData()
        {
            /*TcpListener server=null;
            IPAddress ipAddress = System.Net.IPAddress.Parse("127.0.0.1");
            //IPAddress ipAddress = Dns.Resolve("localhost").AddressList[0];
            server = new TcpListener(ipAddress, soc);            
            // Start listening for client requests.
            server.Start();*/
            String tmp = "";
                try
                {
                    //Console.WriteLine("%%%%%%% CSHARP SR 1");
                    //TcpClient client = server.AcceptTcpClient();
                    //NetworkStream ns = client.GetStream();                    
                    StreamReader sr = YetiCsharp.sr;                    
                    tmp = (String) sr.ReadLine();
                    //Console.WriteLine("%%%%%%% CSHARP SR 2");
                    //ns.Flush();
                    //ns.Close();
                    //System.Console.WriteLine(sr.ReadLine());
                }
                catch (Exception e)
                {
                    Console.WriteLine("Exception caught.");
                    Console.WriteLine("Source: " + e.Source);
                    Console.WriteLine("Message: " + e.Message);
                    Console.WriteLine(e.StackTrace);
                    string tmp2 = e.StackTrace;

                }
            //server.Stop();
            return tmp;
        }
        //The method that closes the socket resources at the end of the YetiCsharp main
        public static void closeSocket()
        {
            YetiCsharp.tc.Close();
        }
        

    }
}
