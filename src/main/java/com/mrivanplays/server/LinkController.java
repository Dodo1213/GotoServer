package com.mrivanplays.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

import at.favre.lib.crypto.bcrypt.BCrypt;

@Controller
public class LinkController {

    @Autowired
    private ApplicationConfiguration appConfiguration;

    @GetMapping("/")
    public String render(Model model) {
        model.addAttribute("gotoInfo", new LinkCreateRequest());
        return "shortenLink";
    }

    @GetMapping("/favicon.ico")
    public void renderFavicon(HttpServletResponse response) {
        response.setContentType("image/vnd.microsoft.icon");
        try (InputStream in = new FileInputStream(appConfiguration.getFavicon())) {
            StreamUtils.copy(in, response.getOutputStream());
        } catch (IOException ignored) {
        }
    }

    @GetMapping("/{id}")
    public void redirect(@PathVariable String id, HttpServletResponse response) throws IOException {
        File file = new File("./links", id + ".json");
        if (!file.exists()) {
            response.sendError(404, "Link not found");
            return;
        }
        try (Reader reader = new FileReader(file)) {
            String link = ServerConstants.JSON_MAPPER.reader().readTree(reader).get("link").asText();
            response.sendRedirect(link);
        }
    }

    @GetMapping("/customCreate")
    public String renderCustom(Model model) {
        model.addAttribute("gotoInfo", new CustomLinkCreateRequest());
        return "customShortenLink";
    }

    @PostMapping("/customCreate")
    public ModelAndView renderCustomPost(@ModelAttribute CustomLinkCreateRequest gotoInfo, HttpServletRequest request) {
        String baseUrl = getBaseUrl(request);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("homePage", baseUrl);
        String url = gotoInfo.getLongUrl();
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            modelAndView.setViewName("error-406");
            return modelAndView;
        }
        String password = gotoInfo.getPassword();
        boolean verify = BCrypt.verifyer().verify(password.toCharArray(), appConfiguration.getEncodedPassword()).verified;
        if (!verify) {
            modelAndView.setViewName("error-403");
            return modelAndView;
        }
        try {
            String linkId = createLinkShorted(gotoInfo.getLongUrl(), gotoInfo.getKeyword());
            modelAndView.setViewName("shortenLinkResult");
            modelAndView.addObject("gotoInfo", new LinkCreateResponse(baseUrl + "/" + linkId));
            return modelAndView;
        } catch (IOException e) {
            e.printStackTrace();
            modelAndView.setViewName("error-500");
            return modelAndView;
        } finally {
            System.gc();
        }
    }

    @PostMapping("/")
    public ModelAndView renderPost(@ModelAttribute LinkCreateRequest gotoInfo, HttpServletRequest request) {
        String baseUrl = getBaseUrl(request);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("homePage", baseUrl);
        String url = gotoInfo.getUrl();
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            modelAndView.setViewName("error-406");
            return modelAndView;
        }
        String password = gotoInfo.getPassword();
        boolean verify = BCrypt.verifyer().verify(password.toCharArray(), appConfiguration.getEncodedPassword()).verified;
        if (!verify) {
            modelAndView.setViewName("error-403");
            return modelAndView;
        }
        try {
            String linkId = createLinkShorted(url, null);
            modelAndView.setViewName("shortenLinkResult");
            modelAndView.addObject("gotoInfo", new LinkCreateResponse(baseUrl + "/" + linkId));
            return modelAndView;
        } catch (IOException e) {
            e.printStackTrace();
            modelAndView.setViewName("error-500");
            return modelAndView;
        } finally {
            System.gc();
        }
    }

    @PostMapping("/api/create")
    public ResponseEntity<LinkCreateResponse> createRequest(@RequestBody LinkCreateRequest linkRequest, HttpServletRequest request) {
        String baseUrl = getBaseUrl(request);
        String url = linkRequest.getUrl();
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new PostError("URL Invalid", 406);
        }
        String password = linkRequest.getPassword();
        boolean verify = BCrypt.verifyer().verify(password.toCharArray(), appConfiguration.getEncodedPassword()).verified;
        if (!verify) {
            throw new PostError("Cannot verify password", 403);
        }
        try {
            String linkId = createLinkShorted(url, null);
            return ResponseEntity.ok().body(new LinkCreateResponse(baseUrl + "/" + linkId));
        } catch (IOException e) {
            e.printStackTrace();
            throw new PostError("Internal error", 500);
        } finally {
            System.gc();
        }
    }

    @PostMapping("/api/createCustom")
    public ResponseEntity<LinkCreateResponse> customCreateRequest(@RequestBody CustomLinkCreateRequest linkRequest, HttpServletRequest request) {
        String baseUrl = getBaseUrl(request);
        String url = linkRequest.getLongUrl();
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new PostError("URL Invalid", 406);
        }
        String password = linkRequest.getPassword();
        boolean verify = BCrypt.verifyer().verify(password.toCharArray(), appConfiguration.getEncodedPassword()).verified;
        if (!verify) {
            throw new PostError("Cannot verify password", 403);
        }
        try {
            String linkId = createLinkShorted(url, linkRequest.getKeyword());
            return ResponseEntity.ok().body(new LinkCreateResponse(baseUrl + "/" + linkId));
        } catch (IOException e) {
            e.printStackTrace();
            throw new PostError("Internal error", 500);
        } finally {
            System.gc();
        }
    }

    private String createLinkShorted(String url, String keyword) throws IOException {
        String existing = checkForExisting(url);
        if (existing != null) {
            return existing;
        }
        Information info;
        if (keyword != null) {
            info = getInfo(keyword);
        } else {
            info = getInfo(StringRandomCreator.generateRandomString(11));
        }
        File file = info.file;
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        String linkId = info.linkId;
        ObjectNode object = new ObjectNode(ServerConstants.JSON_MAPPER.getNodeFactory());
        object.put("link", url);
        try (Writer writer = new FileWriter(file)) {
            ServerConstants.JSON_MAPPER.writeValue(writer, object);
        }
        return linkId;
    }

    private String checkForExisting(String link) {
        File directory = new File(".", "links");
        if (!directory.exists()) {
            directory.mkdirs();
            return null;
        }
        File[] files = directory.listFiles(($, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            return null;
        }
        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                JsonNode node = ServerConstants.JSON_MAPPER.readTree(reader);
                if (node.get("link").asText().equalsIgnoreCase(link)) {
                    return file.getName().replace(".json", "");
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    private Information getInfo(String id) {
        File file = new File("./links", id + ".json");
        if (file.exists()) {
            return getInfo(StringRandomCreator.generateRandomString(11));
        }
        return new Information(file, id);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build().toUriString();
        if (appConfiguration.shouldUseHTTPS()) {
            return baseUrl.replace("http", "https");
        } else {
            return baseUrl;
        }
    }

    @AllArgsConstructor
    private static final class Information {

        private File file;
        private String linkId;
    }
}
