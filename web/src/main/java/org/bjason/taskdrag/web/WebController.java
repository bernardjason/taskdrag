package org.bjason.taskdrag.web;

import org.bjason.taskdrag.common.AuthHelper;
import org.bjason.taskdrag.common.CallBackend;
import org.bjason.taskdrag.model.FreeText;
import org.bjason.taskdrag.model.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;


@Controller
public class WebController {

    @Autowired
    CallBackend callBackend;

    // Login form
    @RequestMapping("/login.html")
    public String login() {
        return "login.html";
    }

    // Login form with error
    @RequestMapping("/login-error.html")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login.html";
    }

    @RequestMapping("/ping.html")
    public String ping(Model model) {
        return "ping.html";
    }


    @GetMapping("/")
    public String singlePage(Model model, HttpSession session) throws Exception {

        AuthHelper ah = new AuthHelper(session.getId());
        callBackend.addAuthentication(ah);

        SecurityContext sc = SecurityContextHolder.getContext();
        org.springframework.security.core.userdetails.User principle = (User) ((UsernamePasswordAuthenticationToken) sc.getAuthentication()).getPrincipal();
        model.addAttribute("username",principle.getUsername());

        allData(model);
        return "index";
    }

    @GetMapping("/welcome")
    public String welcome(Model Map, HttpSession session) {
        SecurityContext sc = SecurityContextHolder.getContext();
        Enumeration<String> names = session.getAttributeNames() ;
        while( names.hasMoreElements()) {
            String n = names.nextElement();
        }

        return "welcome";
    }


    @GetMapping("/all_entries")
    public String getAllEntries(Model map,HttpSession session) throws Exception {
        AuthHelper ah = new AuthHelper(session.getId());
        callBackend.addAuthentication(ah);
        allData(map);

        return "all_entries";
    }
    @GetMapping("/kanban")
    public String getKanban(Model map,HttpSession session) throws Exception {
        AuthHelper ah = new AuthHelper( session.getId());
        callBackend.addAuthentication(ah);

        allData(map);
        return "kanban";
    }

    private void allData(Model model) throws Exception {
        Work[] allWork = callBackend.getAllWork();
        HashMap<Long, ArrayList<FreeText>> freeTextMap = callBackend.getFreeTextMap();
        model.addAttribute("allWork", allWork);
        model.addAttribute("allFreeText", freeTextMap);
        model.addAttribute("work", new Work());
    }

}
