package yeti.environments.java;

import java.util.ArrayList;
import java.util.List;

public class YetiDecisionTreeClassifier implements YetiBugClassifier {

    @Override
    public boolean areTheSame(YetiBug bug1, YetiBug bug2) {
        if (bug1.getClassName().equals(bug2.getClassName())) {
            if (bug1.getMethodName().equals(bug2.getMethodName())) {
                if (bug1.getLineNumber().equals(bug2.getLineNumber())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public List<YetiBugList> classifyThis(YetiBugList buglist) {
        List<YetiBugList> classified = new ArrayList<YetiBugList>(100); // Initialise to 100 results as a rough estimate of how many different bugs
                                                                        // that are likely to be found. This can probably be based of the input list length
        boolean bugMatched;
        YetiBugList templist;
        for (YetiBug bug : buglist.getBugs()) {
            bugMatched = false;
            for (YetiBugList foundbug : classified) {
                if (foundbug.contains(bug)) {
                    foundbug.addBug(bug);
                    bugMatched = Boolean.TRUE;
                    break;
                }
            }
            if (!bugMatched) {
                templist = new YetiBugList();
                templist.addBug(bug);
                classified.add(templist);
            }
        }
        return classified;
    }

}
