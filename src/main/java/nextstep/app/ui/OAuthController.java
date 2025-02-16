package nextstep.app.ui;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;


@Controller
public class OAuthController {
    final RestTemplate rest = new RestTemplate();

    @GetMapping("/oauth2/authorization/github")
    public String oauthAuthorization(
            HttpServletResponse response
    ) {
        return "redirect:" + generateRedirectUri();
    }

    private String generateRedirectUri() {
        final String clientId = "Ov23liTBhugSIcf8VX1v";
        final String RESPONSE_TYPE = "code";
        final String SCOPE = "read:user";
        final String REDIRECT_URI = "http://localhost:8080/login/oauth2/code/github";
        return "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&response_type=" + RESPONSE_TYPE +
                "&scope=" + SCOPE +
                "&redirect_uri=" + REDIRECT_URI;
    }


    @GetMapping("/login/oauth2/code/github")
    public String oauthLogin(
            HttpServletRequest request,
            @RequestParam(name = "code") String code
    ) {
        SecurityContext context = (SecurityContext) request.getSession().getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        );
        if (context == null) {
            context = new SecurityContext();
        }
        modifyContext(context);
        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );
        return "redirect:/";
    }

    public void modifyContext(SecurityContext context) {
        context.setAuthentication(new Authentication() {
            @Override
            public Set<String> getAuthorities() {
                return Set.of();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return email();
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }
        });
    }

    private String email() {
        Map<String, String> authBody = rest.postForObject("http://localhost:8089/login/oauth/access_token", null, Map.class);
        final String accessToken = authBody.get("access_token");
        final String tokenType = authBody.get("token_type");
        HttpHeaders headers = new HttpHeaders();
        headers.add(
                "Authorization",
                tokenType + " " + accessToken
        );

        Map<String, String> resourceBody = rest.exchange(
                "http://localhost:8089/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        ).getBody();
        return resourceBody.get("email");
    }
}
