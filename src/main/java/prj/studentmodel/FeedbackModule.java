package prj.studentmodel;

import prj.antlr.JavaASTNode;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackModule {
    @Autowired
    private KieContainer kieContainer;

    public String generateFeedback(JavaASTNode node) {
        String feedBack = "";
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("feedback", feedBack);
        kieSession.setGlobal("noNext", Boolean.FALSE);
        kieSession.insert(node);
        kieSession.fireAllRules();
        kieSession.dispose();
        return kieSession.getGlobal("feedback").toString();
    }
}
