package com.mrivanplays.server;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class NormalErrorHandler implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(String.valueOf(status));
            if (statusCode == 404) {
                String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                        .replacePath(null)
                        .build().toUriString();
                modelAndView.setViewName("error-404");
                modelAndView.addObject("homePage", baseUrl);
            }
        }
        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
