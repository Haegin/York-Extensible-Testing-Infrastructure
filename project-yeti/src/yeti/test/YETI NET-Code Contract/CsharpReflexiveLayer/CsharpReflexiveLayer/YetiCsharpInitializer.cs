using System;
using System.Reflection;
using System.Collections;

namespace CsharpReflexiveLayer
{
    /**
* Class that is responsible for extracting the metadata information
     * YETI requires
* 
* @author Sotirios Tassis (st552@cs.york.ac.uk)
* @date Jul 21, 2009
*
*/
    class YetiCsharpInitializer
    {
        //allTypes holds all the Types that exist in an assembly (i.e. class,interface etc.)
        public static ArrayList allTypes; 
        //creationRoutines has all the Constructors
        //methodRoutines has all the methods of the classes that are in an assembly
        //allInterfaces has all the intercases the classes of the assemblies implement
        public static ArrayList creationRoutines, methodRoutines, allInterfaces;
        

        public YetiCsharpInitializer()
        {
            allTypes = new ArrayList();
            allInterfaces = new ArrayList();
            creationRoutines = new ArrayList();
            methodRoutines = new ArrayList();
        }

        //The following are the properties that can acces the
        //variables of the class (* in case in the future the visibility of the variables changes)
        public ArrayList AllTypes
        {
            get { return allTypes; }
        }
        public ArrayList CreationRoutines
        {
            get { return creationRoutines; }
        }
        public ArrayList MethodRoutines
        {
            get { return methodRoutines; }
        }
        public ArrayList AllInterfaces
        {
            get { return allInterfaces; }
        }

        //This method starts the process of extracting the metadata info
        //using the System.Reflection API
        public void loadAssemblies(Assembly asm)
        {            
            //here we get the Types that exist inside an assembly
            Type[] types = asm.GetTypes();            
            //Loop traverses the types
            foreach (Type t in types)
            {
                //the if statements filter out only the classes of the asssemly, constructors of classes
                //methods of classes, interfaces of classes
                if ((!t.FullName.StartsWith("System.Diagnostics.Contracts")))
                {
                    //Module indicates the assembly related with each class
                    Module mod = t.Module;
                    if (t.IsInterface)
                    {
                                                
                        YetiCsharpSpecificType itype = new YetiCsharpSpecificType(t,mod);
                        //We add the Interface                   
                        allTypes.Add(itype);
                    }
                    //We only want non abstract classes
                    if ((!t.IsAbstract) && (t.IsClass))
                    {
                        //get the superclass
                        Type bas = t.BaseType;
                        if (bas == null)                        
                            bas = typeof(Object);                        
                        
                        ConstructorInfo[] ci = t.GetConstructors();
                        if (ci.Length != 0)
                        {
                            foreach (ConstructorInfo c in ci)
                            {
                                ParameterInfo[] cpars = c.GetParameters();

                                YetiCsharpConstructor constr = new YetiCsharpConstructor(t, c, cpars, mod);
                                //we add the constructors of the class
                                creationRoutines.Add(constr);
                            }
                        }

                        MethodInfo[] mi = t.GetMethods(BindingFlags.Public | BindingFlags.Static | BindingFlags.Instance);
                        if (mi.Length != 0)
                        {
                            foreach (MethodInfo m in mi)
                            {
                                ParameterInfo[] pi = m.GetParameters();
                                Type ret = m.ReturnType;
                                YetiCsharpMethod method = new YetiCsharpMethod(t, m, pi, ret, m.IsStatic, mod);
                                //we add the methods of the class
                                methodRoutines.Add(method);
                            }
                        }

                        YetiCsharpSpecificType stype = new YetiCsharpSpecificType(t, bas, t.Name,mod);
                        //we add the class itself
                        allTypes.Add(stype);                                              

                    }
                                   
                }
                
            }

            //Here we add to the types implemented Interfaces by the assemblies
            //which are interfaces of the System e.g. IEnumerable and are
            //not defined by the developers
            foreach (Type t in types)
            {
                if ((!t.FullName.StartsWith("System.Diagnostics.Contracts")))
                {
                    if ((!t.IsAbstract) && (t.IsClass))
                    {
                        Type[] it = t.GetInterfaces();
                        Module mod = t.Module;
                        foreach (Type i in it)
                        {
                            //We add the interfaces each method realizes
                            YetiCsharpInterface intype = new YetiCsharpInterface(i, t.Name, mod);
                            allInterfaces.Add(intype);
                            //The flag indicates if we have to add the interface as a usable type to allTypes
                            bool flag = true;
                            //We check if the implemented interface is one of the System or not
                            foreach (YetiCsharpSpecificType indexInterface in allTypes)
                            {
                                if (indexInterface.typeName.Equals(i.Name))
                                    flag = false;
                            }
                            //If it is an Interfaced of the System we add it to
                            //the Types this assemblies under test might want
                            //to use as arguments with their Superclass=Object;
                            if (flag)
                            {
                                YetiCsharpSpecificType tmp = new YetiCsharpSpecificType(i, mod);
                                allTypes.Add(tmp);
                            }
                        }
                    }
                }

            }
                                                      
        }

        //this method starts the initialization process
        //using the string-message (asms) the Java part sent
        public void initialize(string asms)
        {
            //***** External Assembly Loader *****"

            string testDirectory = "";           
            Assembly asm = null;                         
                // Try to load the assemblies.
           
                String[] help = asms.Split(new Char[] { '=' });
                //assies has all the assemblies that muset be tested
                String[] assies = help[1].Split(new Char[] { ':' });
                //the path directory where the assemblies under test exist
                testDirectory = help[0].Trim();               
                YetiPrimInitMethods();
                foreach (String indexassies in assies)
                {                    
                    String tmp="";                    
                    tmp += indexassies.Trim();
                    tmp = testDirectory + tmp;
                    asm = Assembly.LoadFrom(tmp);
                    //for each assembly get the metadata
                    loadAssemblies(asm);
                }               

        }

        //This method gets the metadata information of
        //heloer methods that randomly create new primitive values
        //and YETI uses them for its Random Strategy
        public static void YetiPrimInitMethods()
        {
            Type[] t = new Type[8];
            t[0] = typeof(Boolean); t[1] = typeof(Byte); t[2] = typeof(Int16);
            t[3] = typeof(Int32); t[4] = typeof(Int64); t[5] = typeof(Char);
            t[6] = typeof(Single); t[7] = typeof(Double);         
            MethodInfo[] mi;
            int i=0;
            //we create an objec that has the helper methods
            YetiPrimMethods yms = new YetiPrimMethods();
            //we get the class
            Type tr = yms.GetType();
            //the assembly of the class
            Module mod = tr.Module;
            YetiCsharpSpecificType stype = new YetiCsharpSpecificType(tr, tr.BaseType, tr.Name, mod);
            //add the class
            allTypes.Add(stype);         
            mi = tr.GetMethods(BindingFlags.Public | BindingFlags.Static | BindingFlags.Instance | BindingFlags.DeclaredOnly);
            i = 0;
            //traverse through all the methods and we add each method to the methodRoutines ArrayList
            foreach (MethodInfo m in mi)
            {
                ParameterInfo[] pi = m.GetParameters();
                Type ret = m.ReturnType;
                if (m.Name.Contains("__yetiValue_createRandomBoolean"))
                {
                    YetiCsharpMethod method = new YetiCsharpMethod(tr, m, pi, t[0], true, mod);
                    methodRoutines.Add(method);
                }

                if (m.Name.Contains("__yetiValue_createRandomByte"))
                {
                    YetiCsharpMethod method = new YetiCsharpMethod(tr, m, pi, t[1], true, mod);
                    methodRoutines.Add(method);
                }

                if (m.Name.Contains("__yetiValue_createRandomShort"))
                {
                    YetiCsharpMethod method = new YetiCsharpMethod(tr, m, pi, t[2], true, mod);
                    methodRoutines.Add(method);
                }

                if (m.Name.Contains("__yetiValue_createRandomInt"))
                {
                    YetiCsharpMethod method = new YetiCsharpMethod(tr, m, pi, t[3], true, mod);
                    methodRoutines.Add(method);
                }

                if (m.Name.Contains("__yetiValue_createRandomLong"))
                {
                    YetiCsharpMethod method = new YetiCsharpMethod(tr, m, pi, t[4], true, mod);
                    methodRoutines.Add(method);
                }

                if (m.Name.Contains("__yetiValue_createRandomChar"))
                {
                    YetiCsharpMethod method = new YetiCsharpMethod(tr, m, pi, t[5], true, mod);
                    methodRoutines.Add(method);
                }

                if (m.Name.Contains("__yetiValue_createRandomFloat"))
                {
                    YetiCsharpMethod method = new YetiCsharpMethod(tr, m, pi, t[6], true, mod);
                    methodRoutines.Add(method);
                }

                if (m.Name.Contains("__yetiValue_createRandomDouble"))
                {
                    YetiCsharpMethod method = new YetiCsharpMethod(tr, m, pi, t[7], true, mod);
                    methodRoutines.Add(method);
                }
                i++;
            }            
            
        }

    }

}