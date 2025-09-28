package ar.edu.unq.ttip.sportbook.service.auth

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
class JwtService {
    @Value("\${security.jwt.secret-key}")
    private val secretKey: String? = null

    @Value("\${security.jwt.expiration-time}")
    val expirationTime: Long = 0

    fun extractUsername(token: String?): String {
        val claims = extractAllClaims(token)
        return claims.subject
    }

    fun generateToken(userDetails: UserDetails): String {
        return generateToken(HashMap<String?, Any?>(), userDetails)
    }

    fun generateToken(extraClaims: MutableMap<String?, Any?>?, userDetails: UserDetails): String {
        return buildToken(extraClaims, userDetails, this.expirationTime)
    }

    private fun buildToken(
        extraClaims: MutableMap<String?, Any?>?,
        userDetails: UserDetails,
        expiration: Long
    ): String {
        return Jwts
            .builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(this.signInKey)
            .compact()
    }

    fun isTokenValid(token: String?, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.getUsername()) && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String?): Boolean {
        val claims = extractAllClaims(token)
        return claims.expiration.before(Date())
    }

    private fun extractAllClaims(token: String?): Claims {
        return Jwts
            .parser()
            .setSigningKey(this.signInKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
    }

    private val signInKey: Key
        get() {
            val keyBytes: ByteArray? = Decoders.BASE64.decode(secretKey)
            return Keys.hmacShaKeyFor(keyBytes)
        }
}