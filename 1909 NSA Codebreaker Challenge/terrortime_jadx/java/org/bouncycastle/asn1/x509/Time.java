package org.bouncycastle.asn1.x509;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERUTCTime;

public class Time extends ASN1Object implements ASN1Choice {
    ASN1Primitive time;

    public Time(Date date) {
        String str = "Z";
        SimpleTimeZone simpleTimeZone = new SimpleTimeZone(0, str);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat.setTimeZone(simpleTimeZone);
        StringBuilder sb = new StringBuilder();
        sb.append(simpleDateFormat.format(date));
        sb.append(str);
        String sb2 = sb.toString();
        int parseInt = Integer.parseInt(sb2.substring(0, 4));
        this.time = (parseInt < 1950 || parseInt > 2049) ? new DERGeneralizedTime(sb2) : new DERUTCTime(sb2.substring(2));
    }

    public Time(Date date, Locale locale) {
        String str = "Z";
        SimpleTimeZone simpleTimeZone = new SimpleTimeZone(0, str);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", locale);
        simpleDateFormat.setTimeZone(simpleTimeZone);
        StringBuilder sb = new StringBuilder();
        sb.append(simpleDateFormat.format(date));
        sb.append(str);
        String sb2 = sb.toString();
        int parseInt = Integer.parseInt(sb2.substring(0, 4));
        this.time = (parseInt < 1950 || parseInt > 2049) ? new DERGeneralizedTime(sb2) : new DERUTCTime(sb2.substring(2));
    }

    public Time(ASN1Primitive aSN1Primitive) {
        if ((aSN1Primitive instanceof ASN1UTCTime) || (aSN1Primitive instanceof ASN1GeneralizedTime)) {
            this.time = aSN1Primitive;
            return;
        }
        throw new IllegalArgumentException("unknown object passed to Time");
    }

    public static Time getInstance(Object obj) {
        if (obj == null || (obj instanceof Time)) {
            return (Time) obj;
        }
        if (obj instanceof ASN1UTCTime) {
            return new Time((ASN1Primitive) (ASN1UTCTime) obj);
        }
        if (obj instanceof ASN1GeneralizedTime) {
            return new Time((ASN1Primitive) (ASN1GeneralizedTime) obj);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown object in factory: ");
        sb.append(obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    public static Time getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(aSN1TaggedObject.getObject());
    }

    public Date getDate() {
        try {
            return this.time instanceof ASN1UTCTime ? ((ASN1UTCTime) this.time).getAdjustedDate() : ((ASN1GeneralizedTime) this.time).getDate();
        } catch (ParseException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("invalid date string: ");
            sb.append(e.getMessage());
            throw new IllegalStateException(sb.toString());
        }
    }

    public String getTime() {
        ASN1Primitive aSN1Primitive = this.time;
        return aSN1Primitive instanceof ASN1UTCTime ? ((ASN1UTCTime) aSN1Primitive).getAdjustedTime() : ((ASN1GeneralizedTime) aSN1Primitive).getTime();
    }

    public ASN1Primitive toASN1Primitive() {
        return this.time;
    }

    public String toString() {
        return getTime();
    }
}
