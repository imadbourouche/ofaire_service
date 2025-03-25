package fr.lirmm.fairness.assessment.models.results;

import com.google.gson.annotations.Expose;
import fr.lirmm.fairness.assessment.principles.criterion.question.AbstractCriterionQuestion;

import java.io.Serializable;

public class Result implements Serializable {
    private static final long serialVersionUID = 2746658890779865382L;

    @Expose
    protected double score;

    protected AbstractCriterionQuestion question;

    public Result() {
        this.score = 0;
        this.question = null;
    }

    public Result(double score, AbstractCriterionQuestion question) {
        this.score = score;
        this.question = question;
    }

    public double getScore() {
        return score;
    }



    public AbstractCriterionQuestion getQuestion() {
        return question;
    }

    public void setResult(double score , AbstractCriterionQuestion question){
        this.score = score;
        this.question = question;
   }

}
