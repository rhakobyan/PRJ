package prj.studentmodel;

import prj.ast.JavaASTNode;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * The FeedbackModule class communicates with the Drools rules and obtains the relevant hint
 * based on the JavaASTNode passed.
 */
@Service
public class FeedbackModule {

    @Autowired
    private KieContainer kieContainer;

    /*
     * This method obtains the relevant hint from the rules engine based on the @param Node.
     * It sets up a feedback String, build a kieSession and executes all the rules in the
     * rules file with the @param node to get the correct feedback.
     * @param node The model solution node for obtaining a hint.
     * @return The hint message obtained from the rules engine.
     */
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
