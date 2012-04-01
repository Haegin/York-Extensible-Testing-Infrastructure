package yeti.environments.java;

import java.util.List;

public interface YetiBugClassifier {
    
    public boolean areTheSame(YetiBug bug1, YetiBug bug2);
    
    public List<YetiBugList> classifyThis(YetiBugList buglist);
    
}
