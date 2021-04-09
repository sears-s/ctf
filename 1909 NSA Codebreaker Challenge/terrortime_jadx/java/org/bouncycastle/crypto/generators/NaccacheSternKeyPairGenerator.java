package org.bouncycastle.crypto.generators;

import android.support.v4.view.InputDeviceCompat;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Vector;
import okhttp3.internal.http.StatusLine;
import org.bouncycastle.asn1.eac.CertificateBody;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.NaccacheSternKeyGenerationParameters;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.math.Primes;
import org.bouncycastle.util.BigIntegers;

public class NaccacheSternKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static int[] smallPrimes = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, CertificateBody.profileType, 131, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA, CipherSuite.TLS_PSK_WITH_3DES_EDE_CBC_SHA, CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA, CipherSuite.TLS_DH_DSS_WITH_SEED_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_DHE_DSS_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_DH_anon_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384, CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA384, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256, CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256, 199, Primes.SMALL_FACTOR_LIMIT, 223, 227, 229, 233, 239, 241, 251, InputDeviceCompat.SOURCE_KEYBOARD, 263, 269, 271, 277, 281, 283, 293, StatusLine.HTTP_TEMP_REDIRECT, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557};
    private NaccacheSternKeyGenerationParameters param;

    private static Vector findFirstPrimes(int i) {
        Vector vector = new Vector(i);
        for (int i2 = 0; i2 != i; i2++) {
            vector.addElement(BigInteger.valueOf((long) smallPrimes[i2]));
        }
        return vector;
    }

    private static BigInteger generatePrime(int i, int i2, SecureRandom secureRandom) {
        BigInteger createRandomPrime;
        do {
            createRandomPrime = BigIntegers.createRandomPrime(i, i2, secureRandom);
        } while (createRandomPrime.bitLength() != i);
        return createRandomPrime;
    }

    private static int getInt(SecureRandom secureRandom, int i) {
        int nextInt;
        int i2;
        if (((-i) & i) == i) {
            return (int) ((((long) i) * ((long) (secureRandom.nextInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED))) >> 31);
        }
        do {
            nextInt = secureRandom.nextInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            i2 = nextInt % i;
        } while ((nextInt - i2) + (i - 1) < 0);
        return i2;
    }

    private static Vector permuteList(Vector vector, SecureRandom secureRandom) {
        Vector vector2 = new Vector();
        Vector vector3 = new Vector();
        for (int i = 0; i < vector.size(); i++) {
            vector3.addElement(vector.elementAt(i));
        }
        vector2.addElement(vector3.elementAt(0));
        while (true) {
            vector3.removeElementAt(0);
            if (vector3.size() == 0) {
                return vector2;
            }
            vector2.insertElementAt(vector3.elementAt(0), getInt(secureRandom, vector2.size() + 1));
        }
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        long j;
        BigInteger generatePrime;
        BigInteger add;
        BigInteger bigInteger;
        BigInteger bigInteger2;
        BigInteger generatePrime2;
        BigInteger add2;
        BigInteger bigInteger3;
        BigInteger bigInteger4;
        BigInteger bigInteger5;
        BigInteger bigInteger6;
        boolean z;
        BigInteger bigInteger7;
        PrintStream printStream;
        StringBuilder sb;
        String str;
        BigInteger createRandomPrime;
        int i;
        SecureRandom secureRandom;
        int strength = this.param.getStrength();
        SecureRandom random = this.param.getRandom();
        int certainty = this.param.getCertainty();
        boolean isDebug = this.param.isDebug();
        if (isDebug) {
            PrintStream printStream2 = System.out;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Fetching first ");
            sb2.append(this.param.getCntSmallPrimes());
            sb2.append(" primes.");
            printStream2.println(sb2.toString());
        }
        Vector permuteList = permuteList(findFirstPrimes(this.param.getCntSmallPrimes()), random);
        BigInteger bigInteger8 = ONE;
        BigInteger bigInteger9 = bigInteger8;
        for (int i2 = 0; i2 < permuteList.size() / 2; i2++) {
            bigInteger9 = bigInteger9.multiply((BigInteger) permuteList.elementAt(i2));
        }
        for (int size = permuteList.size() / 2; size < permuteList.size(); size++) {
            bigInteger8 = bigInteger8.multiply((BigInteger) permuteList.elementAt(size));
        }
        BigInteger multiply = bigInteger9.multiply(bigInteger8);
        int bitLength = (((strength - multiply.bitLength()) - 48) / 2) + 1;
        BigInteger generatePrime3 = generatePrime(bitLength, certainty, random);
        BigInteger generatePrime4 = generatePrime(bitLength, certainty, random);
        if (isDebug) {
            System.out.println("generating p and q");
        }
        BigInteger shiftLeft = generatePrime3.multiply(bigInteger9).shiftLeft(1);
        BigInteger shiftLeft2 = generatePrime4.multiply(bigInteger8).shiftLeft(1);
        long j2 = 0;
        while (true) {
            j = j2 + 1;
            generatePrime = generatePrime(24, certainty, random);
            add = generatePrime.multiply(shiftLeft).add(ONE);
            if (!add.isProbablePrime(certainty)) {
                bigInteger2 = shiftLeft2;
                bigInteger = shiftLeft;
            } else {
                while (true) {
                    do {
                        generatePrime2 = generatePrime(24, certainty, random);
                    } while (generatePrime.equals(generatePrime2));
                    bigInteger2 = shiftLeft2;
                    add2 = generatePrime2.multiply(shiftLeft2).add(ONE);
                    if (add2.isProbablePrime(certainty)) {
                        break;
                    }
                    BigInteger bigInteger10 = add;
                    shiftLeft2 = bigInteger2;
                }
                bigInteger = shiftLeft;
                if (multiply.gcd(generatePrime.multiply(generatePrime2)).equals(ONE)) {
                    if (add.multiply(add2).bitLength() >= strength) {
                        break;
                    } else if (isDebug) {
                        PrintStream printStream3 = System.out;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("key size too small. Should be ");
                        sb3.append(strength);
                        sb3.append(" but is actually ");
                        sb3.append(add.multiply(add2).bitLength());
                        printStream3.println(sb3.toString());
                    }
                } else {
                    continue;
                }
            }
            j2 = j;
            shiftLeft2 = bigInteger2;
            shiftLeft = bigInteger;
        }
        String str2 = "needed ";
        if (isDebug) {
            PrintStream printStream4 = System.out;
            bigInteger3 = generatePrime4;
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str2);
            sb4.append(j);
            sb4.append(" tries to generate p and q.");
            printStream4.println(sb4.toString());
        } else {
            bigInteger3 = generatePrime4;
        }
        BigInteger multiply2 = add.multiply(add2);
        BigInteger multiply3 = add.subtract(ONE).multiply(add2.subtract(ONE));
        if (isDebug) {
            System.out.println("generating g");
        }
        long j3 = 0;
        while (true) {
            Vector vector = new Vector();
            bigInteger4 = add2;
            bigInteger5 = add;
            int i3 = 0;
            while (i3 != permuteList.size()) {
                BigInteger divide = multiply3.divide((BigInteger) permuteList.elementAt(i3));
                while (true) {
                    j3++;
                    createRandomPrime = BigIntegers.createRandomPrime(strength, certainty, random);
                    i = strength;
                    secureRandom = random;
                    if (!createRandomPrime.modPow(divide, multiply2).equals(ONE)) {
                        break;
                    }
                    strength = i;
                    random = secureRandom;
                }
                vector.addElement(createRandomPrime);
                i3++;
                strength = i;
                random = secureRandom;
            }
            int i4 = strength;
            SecureRandom secureRandom2 = random;
            bigInteger6 = ONE;
            for (int i5 = 0; i5 < permuteList.size(); i5++) {
                bigInteger6 = bigInteger6.multiply(((BigInteger) vector.elementAt(i5)).modPow(multiply.divide((BigInteger) permuteList.elementAt(i5)), multiply2)).mod(multiply2);
            }
            int i6 = 0;
            while (true) {
                if (i6 >= permuteList.size()) {
                    z = false;
                    break;
                } else if (bigInteger6.modPow(multiply3.divide((BigInteger) permuteList.elementAt(i6)), multiply2).equals(ONE)) {
                    if (isDebug) {
                        PrintStream printStream5 = System.out;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("g has order phi(n)/");
                        sb5.append(permuteList.elementAt(i6));
                        sb5.append("\n g: ");
                        sb5.append(bigInteger6);
                        printStream5.println(sb5.toString());
                    }
                    z = true;
                } else {
                    i6++;
                }
            }
            if (!z) {
                if (!bigInteger6.modPow(multiply3.divide(BigInteger.valueOf(4)), multiply2).equals(ONE)) {
                    if (!bigInteger6.modPow(multiply3.divide(generatePrime), multiply2).equals(ONE)) {
                        if (!bigInteger6.modPow(multiply3.divide(generatePrime2), multiply2).equals(ONE)) {
                            if (!bigInteger6.modPow(multiply3.divide(generatePrime3), multiply2).equals(ONE)) {
                                bigInteger7 = bigInteger3;
                                if (!bigInteger6.modPow(multiply3.divide(bigInteger7), multiply2).equals(ONE)) {
                                    break;
                                }
                                if (isDebug) {
                                    PrintStream printStream6 = System.out;
                                    StringBuilder sb6 = new StringBuilder();
                                    sb6.append("g has order phi(n)/b\n g: ");
                                    sb6.append(bigInteger6);
                                    printStream6.println(sb6.toString());
                                }
                                bigInteger3 = bigInteger7;
                                add = bigInteger5;
                                add2 = bigInteger4;
                                strength = i4;
                                random = secureRandom2;
                            } else if (isDebug) {
                                printStream = System.out;
                                sb = new StringBuilder();
                                str = "g has order phi(n)/a\n g: ";
                            }
                        } else if (isDebug) {
                            printStream = System.out;
                            sb = new StringBuilder();
                            str = "g has order phi(n)/q'\n g: ";
                        }
                    } else if (isDebug) {
                        printStream = System.out;
                        sb = new StringBuilder();
                        str = "g has order phi(n)/p'\n g: ";
                    }
                } else if (isDebug) {
                    printStream = System.out;
                    sb = new StringBuilder();
                    str = "g has order phi(n)/4\n g:";
                }
                sb.append(str);
                sb.append(bigInteger6);
                printStream.println(sb.toString());
            }
            bigInteger7 = bigInteger3;
            bigInteger3 = bigInteger7;
            add = bigInteger5;
            add2 = bigInteger4;
            strength = i4;
            random = secureRandom2;
        }
        if (isDebug) {
            PrintStream printStream7 = System.out;
            StringBuilder sb7 = new StringBuilder();
            sb7.append(str2);
            sb7.append(j3);
            sb7.append(" tries to generate g");
            printStream7.println(sb7.toString());
            System.out.println();
            System.out.println("found new NaccacheStern cipher variables:");
            PrintStream printStream8 = System.out;
            StringBuilder sb8 = new StringBuilder();
            sb8.append("smallPrimes: ");
            sb8.append(permuteList);
            printStream8.println(sb8.toString());
            PrintStream printStream9 = System.out;
            StringBuilder sb9 = new StringBuilder();
            sb9.append("sigma:...... ");
            sb9.append(multiply);
            sb9.append(" (");
            sb9.append(multiply.bitLength());
            sb9.append(" bits)");
            printStream9.println(sb9.toString());
            PrintStream printStream10 = System.out;
            StringBuilder sb10 = new StringBuilder();
            sb10.append("a:.......... ");
            sb10.append(generatePrime3);
            printStream10.println(sb10.toString());
            PrintStream printStream11 = System.out;
            StringBuilder sb11 = new StringBuilder();
            sb11.append("b:.......... ");
            sb11.append(bigInteger7);
            printStream11.println(sb11.toString());
            PrintStream printStream12 = System.out;
            StringBuilder sb12 = new StringBuilder();
            sb12.append("p':......... ");
            sb12.append(generatePrime);
            printStream12.println(sb12.toString());
            PrintStream printStream13 = System.out;
            StringBuilder sb13 = new StringBuilder();
            sb13.append("q':......... ");
            sb13.append(generatePrime2);
            printStream13.println(sb13.toString());
            PrintStream printStream14 = System.out;
            StringBuilder sb14 = new StringBuilder();
            sb14.append("p:.......... ");
            sb14.append(bigInteger5);
            printStream14.println(sb14.toString());
            PrintStream printStream15 = System.out;
            StringBuilder sb15 = new StringBuilder();
            sb15.append("q:.......... ");
            sb15.append(bigInteger4);
            printStream15.println(sb15.toString());
            PrintStream printStream16 = System.out;
            StringBuilder sb16 = new StringBuilder();
            sb16.append("n:.......... ");
            sb16.append(multiply2);
            printStream16.println(sb16.toString());
            PrintStream printStream17 = System.out;
            StringBuilder sb17 = new StringBuilder();
            sb17.append("phi(n):..... ");
            sb17.append(multiply3);
            printStream17.println(sb17.toString());
            PrintStream printStream18 = System.out;
            StringBuilder sb18 = new StringBuilder();
            sb18.append("g:.......... ");
            sb18.append(bigInteger6);
            printStream18.println(sb18.toString());
            System.out.println();
        }
        NaccacheSternKeyParameters naccacheSternKeyParameters = new NaccacheSternKeyParameters(false, bigInteger6, multiply2, multiply.bitLength());
        NaccacheSternPrivateKeyParameters naccacheSternPrivateKeyParameters = new NaccacheSternPrivateKeyParameters(bigInteger6, multiply2, multiply.bitLength(), permuteList, multiply3);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) naccacheSternKeyParameters, (AsymmetricKeyParameter) naccacheSternPrivateKeyParameters);
    }

    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.param = (NaccacheSternKeyGenerationParameters) keyGenerationParameters;
    }
}
