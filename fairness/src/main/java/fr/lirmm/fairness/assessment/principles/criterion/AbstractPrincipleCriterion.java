package fr.lirmm.fairness.assessment.principles.criterion;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.lirmm.fairness.assessment.models.Configuration;
import fr.lirmm.fairness.assessment.models.results.QuestionResult;
import fr.lirmm.fairness.assessment.models.results.Result;
import fr.lirmm.fairness.assessment.principles.AbstractScoredEntity;
import fr.lirmm.fairness.assessment.principles.criterion.question.AbstractCriterionQuestion;
import org.json.JSONException;

import fr.lirmm.fairness.assessment.models.Ontology;
import fr.lirmm.fairness.assessment.principles.Evaluable;

public abstract class AbstractPrincipleCriterion extends AbstractScoredEntity implements Evaluable, Serializable {
	
	private static final long serialVersionUID = -5519124612489307590L;
	protected List<AbstractCriterionQuestion> questions = null;
	private Double maxCredits = 0.0;
	private Double portalMaxCredits = 0.0;
	protected List<Result> results = new ArrayList<>();
	protected String label;
	
	public AbstractPrincipleCriterion() {
		super();
		this.fillProperties();
	}

	@Override
	public final void evaluate(Ontology ontology) throws JSONException, IOException {
		this.results = new ArrayList<>();
		System.out.println("> Evaluating '" + this.getClass().getSimpleName() + "' of ontology '" + ontology.getAcronym() + "' on repository '" + ontology.getPortalInstance().getName() + "' (" + ontology.getPortalInstance().getUrl() + "?apikey=" + ontology.getPortalInstance().getApikey() + ").");
		this.doEvaluation(ontology);
		this.scores = this.results.stream().map(x -> x.getScore()).collect(Collectors.toList());
		this.weights = this.questions.stream().map(x -> x.getMaxPoint().getScore()).collect(Collectors.toList());;
	}

	@Override
	public Double getTotalScoreWeight() {
		return this.maxCredits;
	}

	public Double getPortalMaxCredits() {
		return portalMaxCredits;
	}

	protected abstract void doEvaluation(Ontology ontology) throws JSONException, IOException, MalformedURLException, SocketTimeoutException;


	protected void addResult(int index, double score, String explanation , Map<String ,String> values) {
		this.results.add(index,new QuestionResult(score , explanation ,values, questions.get(index)));
	}


	protected void setDefaultSuccess(int index){
		this.results.add(QuestionResult.success(questions.get(index).getMaxPoint().getExplanation() ,questions.get(index)));
	}
	protected void setNotResolvable(int index){
		this.results.add(QuestionResult.notResolvable(questions.get(index)));
	}


	protected void addResult(Result result){
		this.results.add(result);
	}

	public List<Result> getResults() {
		return results;
	}


	private void fillProperties() {
		try {

			Map<String,?> criterionList = getCriteriaBy(this.getClassConfigName());
			this.maxCredits = Double.parseDouble(criterionList.get("maxCredits").toString());
			this.portalMaxCredits = Double.parseDouble(criterionList.get("portalMaxCredits").toString());
			this.label = criterionList.get("label").toString();

			fillQuestions(criterionList);

		} catch(Exception ioe) {
			ioe.printStackTrace();
		}
	}

	private void fillQuestions(Map<String,?> criterionList) throws JSONException {
		Gson gson = new GsonBuilder().create();
		this.questions = new ArrayList<>();
		for (Map.Entry<String,Map<?,?>> q: ((Map<String, Map<?,?>>)criterionList.get("questions")).entrySet()) {
			this.questions.add(new AbstractCriterionQuestion(
					q.getKey(),
					q.getValue().get("question").toString() ,
					AbstractCriterionQuestion.getQuestionResultsArray(gson.toJsonTree(q.getValue().get("points"), ArrayList.class).getAsJsonArray()),
					(List<String>) q.getValue().get("properties")));
		}
	}

	private Map<String, ?> getCriteriaBy(String principalName) throws IOException {
		Map<?, ?> fairConfigs = Configuration.getInstance().getFairConfigs();
		Map<? , ?> criteria = (Map<?, ?>) fairConfigs.values().stream()
				.filter(principal -> ((Map<?,?>)principal).containsKey(principalName))
				.findFirst().get();
		return  (Map<String,?>) criteria.get(principalName);
	}
	private String getClassConfigName() {
		String className = this.getClass().getSimpleName();
		if(className.length() > 2)
			className = className.substring(0,2) +"."+className.substring(2);

		return className;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return this.getClassConfigName();
	}

}
