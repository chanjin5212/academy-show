package project.academyshow.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import project.academyshow.security.token.AuthToken;
import project.academyshow.security.token.AuthTokenProvider;
import project.academyshow.util.HeaderUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final AuthTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = HeaderUtil.resolveToken(request);
        if (!StringUtils.hasText(token)) {
            log.debug("토큰 정보 없음");
        }
        else {
            /* token string -> AuthToken object */
            AuthToken authToken = tokenProvider.convertToAuthToken(token);

            if (authToken.isValid()) {
                Authentication authentication = authToken.getAuthentication();
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context => 인증 정보 저장 완료: {}", authentication.getName());
            }
        }

        filterChain.doFilter(request, response);
    }
}
