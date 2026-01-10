package com.game.find.word.auth.service;


import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class AppleIdTokenVerifier {

    private static String jwksUri;
    private static String issuer;

    @Value("${apple.auth.jwks-uri}")
    public void setJwksUri(String uri) {
        AppleIdTokenVerifier.jwksUri = uri;
    }

    @Value("${apple.auth.issuer}")
    public void setIssuer(String iss) {
        AppleIdTokenVerifier.issuer = iss;
    }

    public DecodedJWT verify(String idToken, String clientId) throws Exception {
        DecodedJWT jwt = JWT.decode(idToken);

        JwkProvider provider = new UrlJwkProvider(new URL(jwksUri));
        Jwk jwk = provider.get(jwt.getKeyId());

        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(clientId)
                .build();

        return verifier.verify(idToken);
    }
}