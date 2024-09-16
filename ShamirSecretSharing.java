import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

private static final SecureRandom random = new SecureRandom();

// Generate n shares with a threshold of k
public static List<Share> generateShares(int secret, int n, int k, int prime) {
List<BigInteger> coefficients = new ArrayList<>();
coefficients.add(BigInteger.valueOf(secret));
for (int i = 1; i < k; i++) {
coefficients.add(BigInteger.valueOf(random.nextInt(prime)));
}

List<Share> shares = new ArrayList<>();
for (int i = 1; i <= n; i++) {
BigInteger x = BigInteger.valueOf(i);
BigInteger y = evaluatePolynomial(x, coefficients, BigInteger.valueOf(prime));
shares.add(new Share(x, y));
}
return shares;
}

// Evaluate polynomial at a given x
private static BigInteger evaluatePolynomial(BigInteger x, List<BigInteger> coefficients, BigInteger prime) {
BigInteger result = BigInteger.ZERO;
for (int i = coefficients.size() - 1; i >= 0; i--) {
result = result.multiply(x).add(coefficients.get(i)).mod(prime);
}
return result;
}

// Reconstruct the secret using k shares
public static BigInteger reconstructSecret(List<Share> shares, int prime) {
BigInteger secret = BigInteger.ZERO;
BigInteger primeBigInt = BigInteger.valueOf(prime);

for (int i = 0; i < shares.size(); i++) {
BigInteger xi = shares.get(i).x;
BigInteger yi = shares.get(i).y;
BigInteger numerator = BigInteger.ONE;
BigInteger denominator = BigInteger.ONE;

for (int j = 0; j < shares.size(); j++) {
if (i != j) {
BigInteger xj = shares.get(j).x;
numerator = numerator.multiply(xj.negate()).mod(primeBigInt);
denominator = denominator.multiply(xi.subtract(xj)).mod(primeBigInt);
}
}

BigInteger lagrangeTerm = yi.multiply(numerator).multiply(denominator.modInverse(primeBigInt)).mod(primeBigInt);
secret = secret.add(lagrangeTerm).mod(primeBigInt);
}

return secret;
}

public static void main(String[] args) {
int secret = 1234; // The secret to be shared
int n = 5; // Total number of shares
int k = 3; // Minimum number of shares required to reconstruct the secret
int prime = 2089; // A prime number larger than the secret and the number of shares

// Generate shares
List<Share> shares = generateShares(secret, n, k, prime);
System.out.println("Generated Shares:");
for (Share share : shares) {
System.out.println("x: " + share.x + ", y: " + share.y);
}

// Reconstruct the secret using the first k shares
List<Share> selectedShares = shares.subList(0, k);
BigInteger reconstructedSecret = reconstructSecret(selectedShares, prime);
System.out.println("Reconstructed Secret: " + reconstructedSecret);
}

// Inner class to represent a share (x, y)
public static class Share {
BigInteger x;
BigInteger y;

Share(BigInteger x, BigInteger y) {
this.x = x;
this.y = y;
}
}
} 