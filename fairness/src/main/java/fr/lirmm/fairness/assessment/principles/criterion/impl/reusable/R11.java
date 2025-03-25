package fr.lirmm.fairness.assessment.principles.criterion.impl.reusable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import fr.lirmm.fairness.assessment.principles.criterion.question.AbstractCriterionQuestion;
import fr.lirmm.fairness.assessment.principles.criterion.question.Testable;
import fr.lirmm.fairness.assessment.principles.criterion.question.Tester;
import fr.lirmm.fairness.assessment.principles.criterion.question.tests.ContentNegotiationTest;
import fr.lirmm.fairness.assessment.principles.criterion.question.tests.MetaDataExistTest;
import fr.lirmm.fairness.assessment.principles.criterion.question.tests.ResolvableURLTest;
import fr.lirmm.fairness.assessment.principles.criterion.question.tests.URLValidTest;
import org.json.JSONException;

import fr.lirmm.fairness.assessment.models.Ontology;
import fr.lirmm.fairness.assessment.principles.criterion.AbstractPrincipleCriterion;

public class R11 extends AbstractPrincipleCriterion {

    private static final long serialVersionUID = 1326075077978659656L;

    @Override
    protected void doEvaluation(Ontology ontology) throws JSONException, IOException, MalformedURLException, SocketTimeoutException {







        // Q1: Is the ontology license clearly specified (i.e., with a persistent, unique identifier)?
        this.addResult(Tester.doEvaluation(ontology, questions.get(0), new Testable() {
            @Override
            public void doTest(Ontology ontology, AbstractCriterionQuestion question) {
                String license = ontology.getHasLicense();
                if (MetaDataExistTest.isValid(license)) {
                    if (URLValidTest.isValid(license)) {
                        if (ResolvableURLTest.isValid(license)) {
                            if (ContentNegotiationTest.isValid(license , "")) {
                                this.setSuccess(question);
                            } else {
                                this.setScoreLevel(3, question);
                            }
                        } else {
                            this.setScoreLevel(2, question);
                        }
                    } else {
                        this.setScoreLevel(1, question);
                    }
                } else {
                    this.setFailure(question);
                }
            }
        }));



			/*
			// Q2: If yes, is the license description accessible and resolvable by a machine?
			if (licenseIsSpecified && ((license.contains("http")) || (license.contains("https")))) {
				URL urluri = new URL(license);
				HttpURLConnection urluriConnection = (HttpURLConnection) urluri.openConnection();
				HttpURLConnection.setFollowRedirects(false);
				urluriConnection.setRequestMethod("HEAD");
				int httpstatusCode = 0;
				httpstatusCode = urluriConnection.getResponseCode();
				if ((httpstatusCode == 200) || (httpstatusCode == 302)) {
					this.addResult(1, this.questions.get(1).getMaxPoint(), "License is accessible and resolvable");
				} else {
					this.addResult(1, 0.0, String.format("License is not resolvable HTTP error=%s",
							urluriConnection.getResponseMessage()));
				}
			} else {
				this.addResult(1, 0.0, "License is not resolvable");
			}
            */


        //Q2: Are the ontology access rights clearly specified?
        this.addResult(Tester.doEvaluation(ontology, questions.get(1), new Testable() {
            @Override
            public void doTest(Ontology ontology, AbstractCriterionQuestion question) {
                String morePermissions = ontology.getMorePermissions();
                String accessRights = ontology.getAccessRights();

                if (MetaDataExistTest.isValid(accessRights)) {
                    if(MetaDataExistTest.isValid(morePermissions)){
                        this.setSuccess(question);
                    }else {
                        this.setScoreLevel(1,question);
                    }
                } else {
                    this.setFailure(question);
                }
            }
        }));


        // Q3: Are the permissions, usage guidelines and copyright holder clearly documented?
        this.addResult(Tester.doEvaluation(ontology, questions.get(2), new Testable() {
            @Override
            public void doTest(Ontology ontology, AbstractCriterionQuestion question) {
                String useGuidelines = ontology.getUseGuidelines();
                String rightsHolder = ontology.getContact();

                this.setScoreLevel(countExistentMetaData(new String[]{useGuidelines, rightsHolder},question.getPoints().size()), question);

            }
        }));


    }

}
