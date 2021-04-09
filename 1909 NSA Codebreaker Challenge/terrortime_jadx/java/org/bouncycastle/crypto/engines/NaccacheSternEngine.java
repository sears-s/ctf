package org.bouncycastle.crypto.engines;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class NaccacheSternEngine implements AsymmetricBlockCipher {
    private static BigInteger ONE = BigInteger.valueOf(1);
    private static BigInteger ZERO = BigInteger.valueOf(0);
    private boolean debug = false;
    private boolean forEncryption;
    private NaccacheSternKeyParameters key;
    private Vector[] lookup = null;

    private static BigInteger chineseRemainder(Vector vector, Vector vector2) {
        BigInteger bigInteger = ZERO;
        BigInteger bigInteger2 = ONE;
        for (int i = 0; i < vector2.size(); i++) {
            bigInteger2 = bigInteger2.multiply((BigInteger) vector2.elementAt(i));
        }
        for (int i2 = 0; i2 < vector2.size(); i2++) {
            BigInteger bigInteger3 = (BigInteger) vector2.elementAt(i2);
            BigInteger divide = bigInteger2.divide(bigInteger3);
            bigInteger = bigInteger.add(divide.multiply(divide.modInverse(bigInteger3)).multiply((BigInteger) vector.elementAt(i2)));
        }
        return bigInteger.mod(bigInteger2);
    }

    public byte[] addCryptedBlocks(byte[] bArr, byte[] bArr2) throws InvalidCipherTextException {
        String str = "BlockLength too large for simple addition.\n";
        if (this.forEncryption) {
            if (bArr.length > getOutputBlockSize() || bArr2.length > getOutputBlockSize()) {
                throw new InvalidCipherTextException(str);
            }
        } else if (bArr.length > getInputBlockSize() || bArr2.length > getInputBlockSize()) {
            throw new InvalidCipherTextException(str);
        }
        BigInteger bigInteger = new BigInteger(1, bArr);
        BigInteger bigInteger2 = new BigInteger(1, bArr2);
        BigInteger mod = bigInteger.multiply(bigInteger2).mod(this.key.getModulus());
        if (this.debug) {
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("c(m1) as BigInteger:....... ");
            sb.append(bigInteger);
            printStream.println(sb.toString());
            PrintStream printStream2 = System.out;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("c(m2) as BigInteger:....... ");
            sb2.append(bigInteger2);
            printStream2.println(sb2.toString());
            PrintStream printStream3 = System.out;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("c(m1)*c(m2)%n = c(m1+m2)%n: ");
            sb3.append(mod);
            printStream3.println(sb3.toString());
        }
        byte[] byteArray = this.key.getModulus().toByteArray();
        Arrays.fill(byteArray, 0);
        System.arraycopy(mod.toByteArray(), 0, byteArray, byteArray.length - mod.toByteArray().length, mod.toByteArray().length);
        return byteArray;
    }

    public byte[] encrypt(BigInteger bigInteger) {
        byte[] byteArray = this.key.getModulus().toByteArray();
        Arrays.fill(byteArray, 0);
        byte[] byteArray2 = this.key.getG().modPow(bigInteger, this.key.getModulus()).toByteArray();
        System.arraycopy(byteArray2, 0, byteArray, byteArray.length - byteArray2.length, byteArray2.length);
        if (this.debug) {
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("Encrypted value is:  ");
            sb.append(new BigInteger(byteArray));
            printStream.println(sb.toString());
        }
        return byteArray;
    }

    public int getInputBlockSize() {
        return this.forEncryption ? ((this.key.getLowerSigmaBound() + 7) / 8) - 1 : this.key.getModulus().toByteArray().length;
    }

    public int getOutputBlockSize() {
        return this.forEncryption ? this.key.getModulus().toByteArray().length : ((this.key.getLowerSigmaBound() + 7) / 8) - 1;
    }

    public void init(boolean z, CipherParameters cipherParameters) {
        this.forEncryption = z;
        if (cipherParameters instanceof ParametersWithRandom) {
            cipherParameters = ((ParametersWithRandom) cipherParameters).getParameters();
        }
        this.key = (NaccacheSternKeyParameters) cipherParameters;
        if (!this.forEncryption) {
            if (this.debug) {
                System.out.println("Constructing lookup Array");
            }
            NaccacheSternPrivateKeyParameters naccacheSternPrivateKeyParameters = (NaccacheSternPrivateKeyParameters) this.key;
            Vector smallPrimes = naccacheSternPrivateKeyParameters.getSmallPrimes();
            this.lookup = new Vector[smallPrimes.size()];
            for (int i = 0; i < smallPrimes.size(); i++) {
                BigInteger bigInteger = (BigInteger) smallPrimes.elementAt(i);
                int intValue = bigInteger.intValue();
                this.lookup[i] = new Vector();
                this.lookup[i].addElement(ONE);
                if (this.debug) {
                    PrintStream printStream = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Constructing lookup ArrayList for ");
                    sb.append(intValue);
                    printStream.println(sb.toString());
                }
                BigInteger bigInteger2 = ZERO;
                for (int i2 = 1; i2 < intValue; i2++) {
                    bigInteger2 = bigInteger2.add(naccacheSternPrivateKeyParameters.getPhi_n());
                    this.lookup[i].addElement(naccacheSternPrivateKeyParameters.getG().modPow(bigInteger2.divide(bigInteger), naccacheSternPrivateKeyParameters.getModulus()));
                }
            }
        }
    }

    public byte[] processBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (this.key == null) {
            throw new IllegalStateException("NaccacheStern engine not initialised");
        } else if (i2 > getInputBlockSize() + 1) {
            throw new DataLengthException("input too large for Naccache-Stern cipher.\n");
        } else if (this.forEncryption || i2 >= getInputBlockSize()) {
            if (!(i == 0 && i2 == bArr.length)) {
                byte[] bArr2 = new byte[i2];
                System.arraycopy(bArr, i, bArr2, 0, i2);
                bArr = bArr2;
            }
            BigInteger bigInteger = new BigInteger(1, bArr);
            if (this.debug) {
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("input as BigInteger: ");
                sb.append(bigInteger);
                printStream.println(sb.toString());
            }
            if (this.forEncryption) {
                return encrypt(bigInteger);
            }
            Vector vector = new Vector();
            NaccacheSternPrivateKeyParameters naccacheSternPrivateKeyParameters = (NaccacheSternPrivateKeyParameters) this.key;
            Vector smallPrimes = naccacheSternPrivateKeyParameters.getSmallPrimes();
            for (int i3 = 0; i3 < smallPrimes.size(); i3++) {
                BigInteger modPow = bigInteger.modPow(naccacheSternPrivateKeyParameters.getPhi_n().divide((BigInteger) smallPrimes.elementAt(i3)), naccacheSternPrivateKeyParameters.getModulus());
                Vector[] vectorArr = this.lookup;
                Vector vector2 = vectorArr[i3];
                if (vectorArr[i3].size() != ((BigInteger) smallPrimes.elementAt(i3)).intValue()) {
                    if (this.debug) {
                        PrintStream printStream2 = System.out;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Prime is ");
                        sb2.append(smallPrimes.elementAt(i3));
                        sb2.append(", lookup table has size ");
                        sb2.append(vector2.size());
                        printStream2.println(sb2.toString());
                    }
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Error in lookup Array for ");
                    sb3.append(((BigInteger) smallPrimes.elementAt(i3)).intValue());
                    sb3.append(": Size mismatch. Expected ArrayList with length ");
                    sb3.append(((BigInteger) smallPrimes.elementAt(i3)).intValue());
                    sb3.append(" but found ArrayList of length ");
                    sb3.append(this.lookup[i3].size());
                    throw new InvalidCipherTextException(sb3.toString());
                }
                int indexOf = vector2.indexOf(modPow);
                if (indexOf == -1) {
                    if (this.debug) {
                        PrintStream printStream3 = System.out;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("Actual prime is ");
                        sb4.append(smallPrimes.elementAt(i3));
                        printStream3.println(sb4.toString());
                        PrintStream printStream4 = System.out;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("Decrypted value is ");
                        sb5.append(modPow);
                        printStream4.println(sb5.toString());
                        PrintStream printStream5 = System.out;
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append("LookupList for ");
                        sb6.append(smallPrimes.elementAt(i3));
                        sb6.append(" with size ");
                        sb6.append(this.lookup[i3].size());
                        sb6.append(" is: ");
                        printStream5.println(sb6.toString());
                        for (int i4 = 0; i4 < this.lookup[i3].size(); i4++) {
                            System.out.println(this.lookup[i3].elementAt(i4));
                        }
                    }
                    throw new InvalidCipherTextException("Lookup failed");
                }
                vector.addElement(BigInteger.valueOf((long) indexOf));
            }
            return chineseRemainder(vector, smallPrimes).toByteArray();
        } else {
            throw new InvalidCipherTextException("BlockLength does not match modulus for Naccache-Stern cipher.\n");
        }
    }

    public byte[] processData(byte[] bArr) throws InvalidCipherTextException {
        byte[] bArr2;
        if (this.debug) {
            System.out.println();
        }
        if (bArr.length > getInputBlockSize()) {
            int inputBlockSize = getInputBlockSize();
            int outputBlockSize = getOutputBlockSize();
            String str = " bytes";
            if (this.debug) {
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("Input blocksize is:  ");
                sb.append(inputBlockSize);
                sb.append(str);
                printStream.println(sb.toString());
                PrintStream printStream2 = System.out;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Output blocksize is: ");
                sb2.append(outputBlockSize);
                sb2.append(str);
                printStream2.println(sb2.toString());
                PrintStream printStream3 = System.out;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Data has length:.... ");
                sb3.append(bArr.length);
                sb3.append(str);
                printStream3.println(sb3.toString());
            }
            byte[] bArr3 = new byte[(((bArr.length / inputBlockSize) + 1) * outputBlockSize)];
            int i = 0;
            int i2 = 0;
            while (i < bArr.length) {
                int i3 = i + inputBlockSize;
                if (i3 < bArr.length) {
                    int i4 = i3;
                    bArr2 = processBlock(bArr, i, inputBlockSize);
                    i = i4;
                } else {
                    bArr2 = processBlock(bArr, i, bArr.length - i);
                    i += bArr.length - i;
                }
                if (this.debug) {
                    PrintStream printStream4 = System.out;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("new datapos is ");
                    sb4.append(i);
                    printStream4.println(sb4.toString());
                }
                if (bArr2 != null) {
                    System.arraycopy(bArr2, 0, bArr3, i2, bArr2.length);
                    i2 += bArr2.length;
                } else {
                    String str2 = "cipher returned null";
                    if (this.debug) {
                        System.out.println(str2);
                    }
                    throw new InvalidCipherTextException(str2);
                }
            }
            byte[] bArr4 = new byte[i2];
            System.arraycopy(bArr3, 0, bArr4, 0, i2);
            if (this.debug) {
                PrintStream printStream5 = System.out;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("returning ");
                sb5.append(bArr4.length);
                sb5.append(str);
                printStream5.println(sb5.toString());
            }
            return bArr4;
        }
        if (this.debug) {
            System.out.println("data size is less then input block size, processing directly");
        }
        return processBlock(bArr, 0, bArr.length);
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }
}
