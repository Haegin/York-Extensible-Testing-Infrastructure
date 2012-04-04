using System;
using System.Collections;
using System.Reflection;

namespace CsharpReflexiveLayer
{
    /**
* Class that represents the Method that exists 
* in a class Type.
* 
* @author Sotirios Tassis (st552@cs.york.ac.uk)
* @date Jul 21, 2009
*
*/
    class YetiCsharpMethod
    {
        //The metadata of a Method
        public MethodInfo mi;
        //The parameters metadata information
        public ParameterInfo[] parameters;
        //The class that holds the Method
        public Type type;
        //The metadata of the retrun type of the Method
        public Type returntype;
        //The assembly that has the class of the Method
        public Module module;
        //Flag that shows if the Method is a static one
        bool stat;

        public YetiCsharpMethod(Type t, MethodInfo method, ParameterInfo[] par, Type ret, bool st, Module mod)
        {
            this.mi = method;
            this.parameters = par;
            this.type = t;
            this.returntype = ret;
            this.module = mod;
            this.stat = st; 
        }

        //The ToString method is used to send to the Java application
        //the metadata information in the appropriate 
        //format of a string-messsage ( <xxx>:<xxx>:...)
        public override string ToString()
        {
            string help = "";
            foreach (ParameterInfo p in parameters)
            {
                if (!help.Equals(""))
                    help += ";" + p.ParameterType.Name;
                else
                    help += p.ParameterType.Name;
            }
            return (type.Name+":"+mi.Name+":"+help+":"+returntype.Name+":"+stat+ ":" +module.Name);            
        }

    }
}
