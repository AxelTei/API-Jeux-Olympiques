package com.jo.api.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AuthenticationRateLimitFilter extends OncePerRequestFilter {

    // Map pour stocker les compteurs de tentatives d'authentification par IP
    private final Map<String, RequestCounter> loginAttempts = new ConcurrentHashMap<>();

    // Limite de tentatives d'authentification par période de 15 minutes
    private final int MAX_LOGIN_ATTEMPTS = 5;

    // Durée de la période en millisecondes (15 minutes)
    private final long PERIOD_DURATION_MS = 15 * 60 * 1000;

    // URL de l'endpoint d'authentification à protéger
    private final String LOGIN_ENDPOINT = "/auth/signin";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtenir l'adresse IP réelle du client
        String clientIP = getClientIP(request);

        // Récupérer ou créer un compteur pour cette IP
        RequestCounter counter = loginAttempts.computeIfAbsent(clientIP,
                ip -> new RequestCounter(System.currentTimeMillis()));

        // Vérifier si nous avons dépassé la limite
        if (counter.incrementAndGet(System.currentTimeMillis()) <= MAX_LOGIN_ATTEMPTS) {
            // La tentative d'authentification est autorisée
            filterChain.doFilter(request, response);
        } else {
            // La tentative est rejetée - trop de tentatives
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Trop de tentatives de connexion. Veuillez réessayer dans quelques minutes.\"}");
        }
    }

    /**
     * Obtient l'adresse IP réelle du client, même derrière un proxy
     */
    private String getClientIP(HttpServletRequest request) {
        String[] HEADERS_TO_TRY = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                // Si plusieurs IP, prendre la première
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Ce filtre ne s'applique qu'aux requêtes d'authentification
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // N'appliquer le filtre que pour les requêtes POST sur l'endpoint de login
        return !path.equals(LOGIN_ENDPOINT) || !"POST".equals(method);
    }

    /**
     * Classe interne pour suivre les tentatives d'authentification
     */
    private static class RequestCounter {
        private AtomicInteger count = new AtomicInteger(0);
        private long windowStartTime;

        public RequestCounter(long startTime) {
            this.windowStartTime = startTime;
        }

        public int incrementAndGet(long currentTime) {
            // Réinitialiser le compteur si 15 minutes se sont écoulées
            if (currentTime - windowStartTime > 15 * 60 * 1000) {
                count.set(0);
                windowStartTime = currentTime;
            }

            // Incrémenter et retourner la nouvelle valeur
            return count.incrementAndGet();
        }
    }
}
