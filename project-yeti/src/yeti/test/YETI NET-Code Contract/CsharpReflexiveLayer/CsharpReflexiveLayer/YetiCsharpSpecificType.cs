using System;
using System.Collections;
using System.Reflection;


namespace CsharpReflexiveLayer
{
    /**
 * Class that represents the Types that exist 
 * in a NET assembly (i.e. class, interface etc.).
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Jul 21, 2009
 *
 */
    class YetiCsharpSpecificType
    {
        //The class metadata
        public Type c;
        //Superclass metadata
        public Type bas;
        //The name of the type
        public String typeName;
        //The assembly of the class
        public Module module;
        //Flag indicating if the type is interface
        public Boolean intrface;

        public YetiCsharpSpecificType()
        {
        }
        //This constructor is used when the initialization process finds
        //an Interface type
        public YetiCsharpSpecificType(Type cl, Module mod)
        {
            this.typeName = cl.Name;
            this.module = mod;
            this.intrface = true;
        }
        //Consrtucotr that is used when the initialization process finds
        //a Class type 
        public YetiCsharpSpecificType(Type cl, Type basecl, string nm, Module mod)
        {
            this.c = cl;
            this.bas = basecl;
            this.typeName = nm;           
            this.intrface = false;
            this.module = mod;
        }
        //The ToString method is used to send to the Java application
        //the metadata information in the appropriate 
        //format of a string-messsage ( <xxx>:<xxx>:...)
        public override string ToString()
        {
            if (!intrface)
            {                
                return (typeName + ":" + bas.Name + ":" + module.Name);
            }
            else
            {               
                return (typeName + ":" + "Object" + ":" + module.Name);
            }
        }

    }
}
