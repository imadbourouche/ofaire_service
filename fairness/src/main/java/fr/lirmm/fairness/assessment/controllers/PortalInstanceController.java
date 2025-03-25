package fr.lirmm.fairness.assessment.controllers;

import fr.lirmm.fairness.assessment.models.Configuration;
import fr.lirmm.fairness.assessment.models.PortalInstance;
import fr.lirmm.fairness.assessment.models.requestparams.exceptions.ApikeyException;
import fr.lirmm.fairness.assessment.models.requestparams.exceptions.PortalNameException;
import fr.lirmm.fairness.assessment.models.requestparams.params.ApiParam;
import fr.lirmm.fairness.assessment.models.requestparams.params.PortalParam;
import fr.lirmm.fairness.assessment.models.requestparams.params.PortalUrlParam;

import java.io.IOException;

public class PortalInstanceController {

    private final RequestParamController paramController;
    private PortalInstance portalInstance;

    public PortalInstanceController(RequestParamController requestParamController) {
        this.paramController = requestParamController;
    }

    public PortalInstance getPortalInstance() throws Exception {
        if (portalInstance==null){
            portalInstance = this.initPortalInstance();
        }
        return portalInstance;
    }

    public PortalInstance getPortalInstanceValue(){
        return  this.portalInstance;
    }

    private PortalInstance initPortalInstance() throws Exception {

        try {
            return this.getPortalInstanceByUrl();
        }catch (ApikeyException e){
            throw e;
        } catch (Exception e) {
            try {
                return this.getPortalInstanceByNameParam();
            }catch( PortalNameException pnException){
                throw pnException;
            }catch (Exception e1){
                try{
                    return this.getPortalInstanceByNameEnv();
                }catch (Exception e11){
                    throw new Exception("Portal configuration file not found (set a valid portal name parameter)");
                }
            }

        }
    }

    private PortalInstance getPortalInstanceByUrl() throws Exception {
        PortalUrlParam urlParam = paramController.getPortalUrlParam();
        try{
            ApiParam apiParam =  paramController.getApiKeyParam();
            return  new PortalInstance(urlParam.getValue(), apiParam.getValue(), false , false);
        }catch (Exception e){
            throw new ApikeyException(e.getMessage());
        }
    }

    private PortalInstance getPortalInstanceByConfiguration(String portalName) throws IOException {
        String apikey = "";

        try{
            apikey = paramController.getApiKeyParam().getValue();
        }catch (Exception ignored){}

        return PortalInstance.getFromConfiguration(Configuration.getInstance(), portalName ,apikey, false);
    }

    private PortalInstance getPortalInstanceByNameParam() throws Exception {
        try{
            PortalParam portalParam = paramController.getPortalNameParam();
            return  getPortalInstanceByConfiguration(portalParam.getValue());
        }catch (Exception e){
            if(this.paramController.portalName.getValue().isEmpty()){
                throw e;
            }else
                throw  new PortalNameException(e.getMessage());
        }

    }
    private PortalInstance getPortalInstanceByNameEnv() throws Exception {
        return getPortalInstanceByConfiguration(System.getenv(PortalInstance.SERVER_DEFAULT_PORTAL));
    }
}
