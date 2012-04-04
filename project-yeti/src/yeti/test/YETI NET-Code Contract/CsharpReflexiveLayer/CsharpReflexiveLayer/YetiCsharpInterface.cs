using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;

namespace CsharpReflexiveLayer
{
    /**
 * Class that represents the Interface that exists 
 * in a NET assembly.
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Jul 21, 2009
 *
 */
    class YetiCsharpInterface : YetiCsharpSpecificType
    {
        String superclass;

        public YetiCsharpInterface(Type c, String sc, Module mod) : base(c,mod) { this.superclass = sc; }

        //The ToString method is used to send to the Java application
        //the metadata information in the appropriate 
        //format of a string-messsage ( <xxx>:<xxx>:...)
        public override string ToString()
        {
            return (typeName+":"+superclass);
        }
    }
}
