package yeti.environments.java;

import org.apache.commons.lang3.exception.ExceptionUtils;

import yeti.YetiCard;

public class YetiBug {
		private String className = ""; // The class the bug was found in
		private String methodName = ""; // The method the bug was found in
		private String usefulMethodName = ""; // The method the bug was found in with the _number bit removed
		private String unitTest = ""; // A unit test that demonstrates the bug
		private YetiCard[] args; // The arguments used when calling the method
		private Throwable throwable; // The exception that was thrown when we found the bug
		private Integer lineNumber; // The line number reported by the exception
	
		// Constructors
        public YetiBug(String className, String methodName, String unitTest, YetiCard[] args, Throwable throwable, Integer lineNumber) {
            this.className = className;
            this.methodName = methodName;
            this.unitTest = unitTest;
            this.args = args;
            this.throwable = throwable;
            this.lineNumber = lineNumber;
        }
        
		public YetiBug(String className, String methodName) {
			this.className = className;
			this.methodName = methodName;
		}
		
		@Override
		public String toString() {
		    StringBuilder sbargs = new StringBuilder();
		    if (args.length > 0) {
    		    sbargs.append(args[0].getValue());
    		    for (int i = 1; i < args.length; i++) {
    	            sbargs.append(", ");
    		        sbargs.append(args[i].getValue());
    		    }
		    }
		    StringBuilder sb = new StringBuilder();
		    sb.append(className);
		    sb.append('.');
		    sb.append(methodName);
		    sb.append('(');
		    sb.append(sbargs);
		    sb.append(')');
		    sb.append(" -> ");
		    sb.append(throwable.toString());
		    sb.append("\n");
		    sb.append(unitTest);
		    return sb.toString();
		}
	
		// Getters and setters
		
		public String getStackTrace() {
		    return ExceptionUtils.getStackTrace(throwable);
		}
		
		// TODO: write a unit test for this
		public String getStackTraceWithoutYeti() {
		    String[] stacktrace = ExceptionUtils.getStackTrace(throwable).split("[\r\n]+"); // Split on linebreaks and remove empty lines
		    StringBuilder sb = new StringBuilder(stacktrace[0]);
		    for (int i = 1; i < stacktrace.length; i++) { // start at index 1 as the first line is already in the stringbuilder
		        if (stacktrace[i].replaceAll("^\\s+", "").startsWith("at sun.reflect")) {
		            break;
		        }
		        sb.append("\n");
		        sb.append(stacktrace[i]);
		    }
		    return sb.toString();
		}
		
		public String getClassName() {
			return className;
		}
	
		public void setClassName(String className) {
			this.className = className;
		}
	
		public String getMethodName() {
			return methodName;
		}
	
		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}
	
		public String getUnitTest() {
			return unitTest;
		}
	
		public void setUnitTest(String unitTest) {
			this.unitTest = unitTest;
		}
	
		public YetiCard[] getArgs() {
			return args;
		}
	
		public void setArgs(YetiCard[] args) {
			this.args = args;
		}
	
		public Throwable getThrowable() {
			return throwable;
		}
	
		public void setThrowable(Throwable throwable) {
			this.throwable = throwable;
		}
	
		public Integer getLineNumber() {
			return lineNumber;
		}
	
		public void setLineNumber(Integer lineNumber) {
			this.lineNumber = lineNumber;
		}
		
		public void setLineNumberFromException() {
		    if (this.throwable != null) {
		        String[] stacktrace = ExceptionUtils.getStackTrace(this.throwable).split("[\r\n]+");
		        String interestingLine = "";
		        String matcher;
		        
		        // Work out what we're looking for
		        if (this.getUsefulMethodName().equals(this.className)) {
		            matcher = this.className + ".<init>";
		        } else {
		            matcher = this.className + "." + this.getUsefulMethodName();
		        }
		        
		        // Find the line we're interested in
		        for (String line : stacktrace) {
		            if (line.contains(matcher)) {
		                interestingLine = line;
		                break;
		            }
		        }
		        
		        // get the line number
		        // NOTE: if the class is in.haeg.stablemarriage.Coordinator,
		        // and the useful method name is main
		        // then the line we are interested in is at in.haeg.stablemarriage.Coordinator.main(Coordinator.java:18)
		        // so we need to:
		        // match the Coordinator.java:\d\d bit and get the number.
		        String filename = this.className.substring(this.className.lastIndexOf('.') + 1) + ".java";
		        String linenumber = interestingLine.substring(interestingLine.lastIndexOf(filename) + 1 + filename.length(), interestingLine.lastIndexOf(')'));
		        this.lineNumber =  Integer.parseInt(linenumber);
		    }
		}
		
		public String getUsefulMethodName() {
		    if (this.usefulMethodName != null) {
		        if (this.methodName.contains("_")) {
    		        this.usefulMethodName = this.methodName.substring(this.methodName.lastIndexOf('.') + 1, this.methodName.lastIndexOf('_'));
		        } else {
		            this.usefulMethodName = this.methodName;
		        }
		    }
		    return this.usefulMethodName;
		}
		
}