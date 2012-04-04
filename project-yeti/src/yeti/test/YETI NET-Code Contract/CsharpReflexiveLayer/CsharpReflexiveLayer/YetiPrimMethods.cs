using System;
using System.Collections;


namespace CsharpReflexiveLayer
{
    /**
* Class that has all the helper methods
     * that create random primitive values which YETI uses
     * to be sure it can generate test-cases for primitive parameters
* 
* @author Sotirios Tassis (st552@cs.york.ac.uk)
* @date Jul 21, 2009
*
*/
    class YetiPrimMethods
    {      

        /**
        * A boolean random generator.
        * 
        * @return a random boolean.
        */
        public static Boolean __yetiValue_createRandomBoolean()
        {
            Random rand = new Random();
            return (rand.NextDouble() < 0.5);
        }
        /**
	    * A byte random generator.
	    * 
	    * @return a random byte.
	    */

        public static Byte __yetiValue_createRandomByte()
        {
            Random rand = new Random();
            Double d = rand.NextDouble();
            int tmp = Convert.ToInt32((257 * d));
            d = tmp;
            Byte b;
            b = (Byte)d;
            return b;
        }
        /**
	    * A short random generator.
	    * 
	    * @return a random short.
	    */

        public static Int16 __yetiValue_createRandomShort()
        {
            Random rand = new Random();
            Double d = rand.NextDouble();
            int tmp = Convert.ToInt32((65535 * d));
            tmp=tmp-32767;
            short s = Convert.ToInt16(tmp);
            return s;
        }
        /**
	    * A int random generator.
	    * 
	    *@return a random int.
	    */
        public static Int32 __yetiValue_createRandomInt()
        {
            Random rand = new Random();
            double d = rand.NextDouble();
            double d2 = rand.NextDouble()*2 - 1.0d;
            d = 2147483647 * d * d2;
            int i = Convert.ToInt32(d);
            return i;
        }

        /**
	    * A long random generator.
	    * 
	    * @return a random long.
	    */
        public static Int64 __yetiValue_createRandomLong()
        {            
            Double d = YetiPrimMethods.__yetiValue_createRandomDouble();
            long l = Convert.ToInt64(d);
            return l;
        }

        /**
	    * A char random generator.
	    * 
	    * @return a random char.
	    */
        public static Char __yetiValue_createRandomChar()
        {              
            Random rand = new Random();
            char c = Convert.ToChar(Convert.ToInt32(rand.Next(0,255)));
            return c;
        }

        /**
         * A float random generator.
         * 
         * @return a random float.
         */
        public static Single __yetiValue_createRandomFloat()
        {            
            Random rand = new Random();
            float f = Convert.ToSingle(11 * rand.NextDouble());
            int i = Convert.ToInt32(f);
            f = Convert.ToSingle((rand.NextDouble() * (10 ^ i)));
            return f;
        }

        /**
         * A double random generator.
         * 
         * @return a random double.
         */
        public static Double __yetiValue_createRandomDouble()
        {
            Random rand = new Random();
            double f = 15 * rand.NextDouble();
            int i = Convert.ToInt32(f);
            f = Convert.ToDouble((rand.NextDouble() * (10 ^ i)));
            return f;
        }
    }
}
