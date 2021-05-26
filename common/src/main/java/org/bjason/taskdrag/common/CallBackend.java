package org.bjason.taskdrag.common;

import org.bjason.taskdrag.model.FreeText;
import org.bjason.taskdrag.model.TaskStates;
import org.bjason.taskdrag.model.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class CallBackend {

    private static final Logger log = LoggerFactory.getLogger(CallBackend.class);

    private static String csrf = "";

    @Value("${backend.url}")
    private String backend;

    @Autowired
    private RestTemplate restTemplate;

    public void setBackend(String backendUrl) {
        this.backend = backendUrl;
    }

    public Work[] getAllWork() throws Exception {
        try {
            ResponseEntity<Work[]> response =
                    restTemplate.getForEntity(
                            backend+"/work/",
                            Work[].class);
            Work[] allWork = response.getBody();
            log.debug("getAllWork " + allWork.length);
            return allWork;
        } catch(RuntimeException e ) {
            throw new Exception("Failed while retrieving all work data from remote backend",e);
        }
    }

    public HashMap<Long, ArrayList<FreeText>> getFreeTextMap() throws Exception {
        try {
            ResponseEntity<FreeText[]> response2 =
                    restTemplate.getForEntity(
                            backend+"/freetext/",
                            FreeText[].class);
            FreeText[] allFreeText = response2.getBody();

            HashMap<Long, ArrayList<FreeText>> freeTextMap = new HashMap<>();
            for (FreeText freeText : allFreeText) {
                Work work = freeText.getWork();
                if (work != null) {
                    ArrayList<FreeText> text = freeTextMap.computeIfAbsent(work.getId(), xx -> new ArrayList<>());
                    text.add(freeText);
                }
            }
            log.debug("getFreeTextMap " + freeTextMap.size());
            return freeTextMap;
        } catch (RuntimeException e) {
            throw new Exception("Failed while retrieving free text map from remote backend",e);
        }
    }
    public void setStatus(String id,String status) throws Exception {
        log.debug("setStatus "+id+" "+status);
        try {
         restTemplate.put(
                        backend+"/work/"+id+"/"+status,null
         );} catch(RuntimeException e) {
            throw new Exception("Failed while setting status on remote backend",e);
         }
    }

    public void createNew(Work work) throws Exception {
        log.debug("createNew "+work);
        try {
        restTemplate.postForLocation( backend+"/work",work );
        } catch(RuntimeException e) {
            throw new Exception("Failed while creating new work on backend",e);
        }
    }

    public void addNote(FreeText freeText) {
        log.debug("addNote "+freeText);
        restTemplate.postForLocation(
                backend+"/freetext",freeText
        );
    }

    public void putWork(Work work) {
        log.debug("putWork "+work);
        restTemplate.put(
                backend+"/work/"+work.getId(),work
        );
    }

    public TaskStates[] getTaskStats() {

        ResponseEntity<TaskStates[]> response2 =
                restTemplate.getForEntity(
                        backend+"/taskstates",
                        TaskStates[].class);
        TaskStates[] taskStates = response2.getBody();

        log.debug("getTaskStats "+taskStates);
        return taskStates;
    }



    public String login(String username, String password) throws Exception {
        ResponseEntity<String> response = getValidButTempJsessionIdByGetLogin();

        ResponseEntity<String> responsePost = nowPostLoginWithUserPassword(username, password);

        getLoggedInJSessionIdCookiIntoRestTemplateAndCsrfToken(responsePost);

        return response.toString();
    }

    private void getLoggedInJSessionIdCookiIntoRestTemplateAndCsrfToken(ResponseEntity<String> responsePost) {
        String secondToken = responsePost.getHeaders().getFirst("Set-Cookie").replaceAll(";.*$","").replaceAll("JSESSIONID=","");
        AuthHelper secondAuth = new AuthHelper(secondToken);
        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(secondAuth);

        ResponseEntity<String> pingResponse = restTemplate.getForEntity( backend+"/login.html",String.class);
        String secondCsrf = pingResponse.getBody().replaceAll("\n","").replaceFirst("<!.* value=\"","").replaceAll("\"/>.*","");

        AuthHelper thirdAuth = new AuthHelper(secondToken,secondCsrf);
        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(thirdAuth);
    }

    private ResponseEntity<String> nowPostLoginWithUserPassword(String username, String password) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> responsePost = restTemplate.postForEntity( backend+"/login.html", request , String.class );

        log.debug("Status "+responsePost.getStatusCode()+" Response ["+responsePost);

        if ( responsePost.getStatusCode() != HttpStatus.FOUND &&  responsePost.getHeaders().getLocation().compareTo(new URI(backend+"/")) != 0) {
            throw new Exception("Login failed "+responsePost);
        }
        return responsePost;
    }

    private ResponseEntity<String> getValidButTempJsessionIdByGetLogin() {
        ResponseEntity<String> response = restTemplate.getForEntity( backend+"/login.html",String.class);

        String token = response.getHeaders().getFirst("Set-Cookie").replaceAll(";.*$","").replaceAll("JSESSIONID=","");
        String csrf = response.getBody().replaceAll("\n","").replaceFirst("<!.* value=\"","").replaceAll("\"/>.*","");
        AuthHelper ah = new AuthHelper(token,csrf);
        restTemplate.getInterceptors().add(ah);
        return response;
    }

    public void addAuthentication(AuthHelper ah) {
        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(ah);
    }

}
