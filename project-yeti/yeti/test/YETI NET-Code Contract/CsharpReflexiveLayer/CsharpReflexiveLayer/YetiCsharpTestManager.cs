using System;
using System.Collections.Generic;
using System.Reflection;

namespace CsharpReflexiveLayer
{
    /**
 * Class that is responsible for executing the
     * test-cases (Constructors or Methods) received from the Java part
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Jul 21, 2009
 *
 */
    class YetiCsharpTestManager
    {
        //This Dictionary collection holds the mapping between variable identifiers = actual values
        public static Dictionary<String, Object> createdValues = new Dictionary<String, Object>();

        bool callHappened = false;
        //The method that makes the Constructor calls
        //param: message is the test-case message that the Java part has sent
        public string makeConstructorCall(String message)
        {            
            String[] s = message.Split(new Char[] { ':' });
            //id is a variable identifier of the Java part (e.g. v100)
            String id = s[1].Trim();
            //The name of the Consrtuctor
            String name = s[2].Trim();  
            //The parameters of the test-case (e.g. v100;v111;v112)
            String pars = s[3];
            //ArrayList that holds seperately the parameters (e.g. values[0]=v100...)         
            String[] values = null;
            //ArrayList that holds the values of the parameters ( v100=10.5 --> ar[0]=10.5)
            Object[] ar = null;
            
            //Search the creation routines that we have
            foreach (YetiCsharpConstructor index in YetiCsharpInitializer.creationRoutines)
            {
                //If the name is the same with the one we are looking for
                if (name.Equals(index.type.Name))
                {
                    Object o = null; 
                    //Split the parameters string
                    values = pars.Split(new Char[] { ';' });
                    //If there are parameters in the Constructor test-case
                    if (pars.Length != 0 && values.Length==index.parameters.Length)
                    {
                        //We create the ar as big as the number of parameters 
                        //the test-case has
                        ar = new Object[index.parameters.Length];
                        //A help Object that temporarily holds the actual values of the parameters
                        Object helpobject = new Object();
                        int i = 0;
                        //Loop traverses the parameters and ar ArrayList
                        //at exit has the actual values of the parameters
                        foreach (String vs in values)
                        {                         
                            if (!("null".Equals(vs.Trim())))
                            {
                                createdValues.TryGetValue(vs.Trim(), out helpobject);                                
                                ar[i] = helpobject;
                            }
                            else ar[i] = null;
                            i++;
                        }                      
                        //invokation of the Constructor with all the bindings that limit the
                        //search space where the Constructor exists inside the class
                        o = index.type.InvokeMember(null,
                        BindingFlags.DeclaredOnly |
                        BindingFlags.Public |
                        BindingFlags.Instance | BindingFlags.CreateInstance, null, null, ar);
                        //We add the created object as id=o
                        createdValues.Add(id, Convert.ChangeType(o, index.type));
                        callHappened = true;
                        return (id + ":" + name);
                    }
                    //If the Constructor test-case has no parameters
                    if(pars.Length==0)
                    {                      
                        o = index.type.InvokeMember(null,
                        BindingFlags.DeclaredOnly |
                        BindingFlags.Public |
                        BindingFlags.Instance | BindingFlags.CreateInstance, null, null, ar);
                        //We add the created object as id=o
                        createdValues.Add(id, Convert.ChangeType(o, index.type));
                        callHappened = true;                   
                        return (id + ":" + name);
                    }
                }
            }
            return "NO CALL";
        }

        //The method that makes the Method calls (Static or not)
        //param: message is the test-case message that the Java part has sent
        public string makeMethodCall(string message)
        {           
            String[] s = message.Split(new Char[] { ':' });
            //id is a variable identifier of the Java part (e.g. v100)
            String id = s[1].Trim();
            //The name of the Method
            String name = s[4].Trim();
            //The class in which the method exists
            String type = s[2].Trim();
            //The return type of the Method
            String rettype = s[3].Trim();
            //The parameters of the test-case (e.g. v100;v111;v112)
            String pars = s[5];
            //The variable indentifier that maps to the class which we have
            //created and will use to make the invokation
            String target = s[7].Trim();
            //ArrayList that holds seperately the parameters (e.g. values[0]=v100...)
            String[] values = null;
            //ArrayList that holds the values of the parameters ( v100=10.5 --> ar[0]=10.5)
            Object[] ar = null;
            //A help Object that temporarily holds the actual values of the parameters                    
            Object helpobject = new Object();
            
            //Loop that traverses all the method routines to find the correct one 
            //that the test-case indicates
            foreach (YetiCsharpMethod index in YetiCsharpInitializer.methodRoutines)
            {
                if (type.Equals(index.type.Name) && name.Equals(index.mi.Name))
                {
                    if (index.returntype.Name.Equals(rettype))
                    {
                        //if there are parameters in the Method test-case
                        //we hold at the ar ArrayList the actual values of the parameters
                        //which exist in the createdValues Dictionary
                        if (pars.Length != 0)
                        {                            
                            values = pars.Split(new Char[] { ';' });
                            ar = new Object[values.Length];
                            
                            int i = 0;
                            foreach (String vs in values)
                            {
                                if (!("null".Equals(vs.Trim())))
                                {
                                    createdValues.TryGetValue(vs.Trim(), out helpobject);
                                    ar[i] = helpobject;
                                }
                                else ar[i] = null;
                                i++;
                            }

                        }
                        
                        //if the parameters match with the parameters of th test case the invokation can
                        //happen
                        if ((index.parameters.Length == pars.Length && pars.Length == 0) ||
                            (values!=null && values.Length == index.parameters.Length && values.Length != 0))
                        {
                            if (pars.Length == 0) ar = new Object[0];
                            //obj holds the actual metadata of the target
                            Object obj = null;
                            if (!("null".Equals(target)))
                            {
                                createdValues.TryGetValue(target, out helpobject);
                                obj = helpobject;
                            }

                            //invokation of the Method with all the bindings that limit the
                            //search space where the Constructor exists inside the class
                            Object o = index.type.InvokeMember(name,
                            BindingFlags.Public | BindingFlags.Static |
                            BindingFlags.Instance | BindingFlags.InvokeMethod, null, obj, ar);

                            if (!index.returntype.Name.Equals("Void"))
                            {
                                //We add the created object as id=o
                                try
                                {
                                    createdValues.Add(id, Convert.ChangeType(o, index.returntype));
                                }
                                catch (InvalidCastException e)
                                {                                   
                                    createdValues.Add(id, o);
                                }
                                
                            }
                            callHappened = true;                           
                            
                            String value = "null";
                            if (o != null)
                                value = o.ToString();

                            if (YetiCsharpInitializer.allTypes.Contains(index.returntype))
                                return (id + ":" + index.returntype.Name);
                            else
                            {
                                return (id + ":" + value);
                            }
                        }
                    }
                }

            }
            return "NO CALL";

        }
        //The method that makes the call by deciding if it is a Constructor or a Method
        //param: message is the test-case message that the Java part has sent
        public string makeCall(String s)
        { 
            //help will have the string of the result of the test-case invokation
            String help="OK";
            //flag showing what test-case s indicates
            bool constr = false;    
            //flag that shows if the test-case happened or not
            callHappened = false;
            //The temp ArrayList is a help variable to split the initila message
            //which he Java part sent to the CsharpReflexiveLayer
            String[] temp=null;
            try
            {
                if (s.StartsWith("Constructor"))
                {
                    constr = true;
                    temp = s.Split(new Char[] { ':' });
                    help = makeConstructorCall(s);
                }
                else
                {
                    temp = s.Split(new Char[] { ':' });
                    help = makeMethodCall(s);
                }
                if (!callHappened)
                {
                    help = "FAIL!The routine does not exist--> @"+s;                   
                }
            }
            catch(Exception e)
            {
                help = "FAIL! POSSIBLE BUG --> @ ";
                               
                if (e.InnerException != null)
                {                                            
                        if ((e.InnerException.Message.Contains("Precondition")))
                            help = "FAIL! PRECONDITION";
                        else
                            help += e.InnerException.Message + "\n" + e.InnerException.StackTrace;
                }
                else help += e.Message + "\n" + e.StackTrace;
            }         
            if (constr) 
                help += "@" + temp[4].Trim();
            else
                help += "@" + temp[6].Trim();
            return help;
                    
        }

        
    }
}
