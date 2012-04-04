using System;
using System.Collections;
using System.Reflection;

namespace CsharpReflexiveLayer
{
    /**
* Class that represents the Constructor that exists 
* in a class Type.
* 
* @author Sotirios Tassis (st552@cs.york.ac.uk)
* @date Jul 21, 2009
*
*/
    class YetiCsharpConstructor
    {
        //The Constructor metadata information
        public ConstructorInfo ci;
        //The parameters metadata information
        public ParameterInfo[] parameters;
        //The class that holds the Constructor
        public Type type;
        //The assembly that has the class of the Constructor
        public Module module;

        public YetiCsharpConstructor(Type t, ConstructorInfo cons, ParameterInfo[] par, Module mod)
        {
            this.ci = cons;
            this.parameters = par;
            this.type = t;
            this.module = mod;
        }

        //The ToString method is used to send to the Java application
        //the metadata information in the appropriate 
        //format of a string-messsage ( <xxx>:<xxx>:...)
        public override string ToString()
        {
            string help="";
            foreach (ParameterInfo p in parameters)
            {
                if (!help.Equals(""))
                    help += ";" + p.ParameterType.Name;
                else
                    help += p.ParameterType.Name;
            }
            return (type.Name+":"+help + ":" +module.Name);
        }

    }
}
