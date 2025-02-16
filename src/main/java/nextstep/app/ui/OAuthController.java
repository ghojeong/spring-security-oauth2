package nextstep.app.ui;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class OAuthController {

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
}
