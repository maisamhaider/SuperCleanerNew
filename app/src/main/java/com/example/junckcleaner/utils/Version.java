package com.example.junckcleaner.utils;

public class Version implements Comparable<Version> {

    private final String versionCode;

    public final String get() {
        return this.versionCode;
    }

    public Version(String versionCode) {
        if (versionCode == null)
            throw new IllegalArgumentException("Version can not be null");
        if (!versionCode.matches("[0-9]+(\\.[0-9]+)*"))
            throw new IllegalArgumentException("Invalid version format");
        this.versionCode = versionCode;
    }

    @Override
    public int compareTo(Version version) {
        if (version == null)
            return 1;
        String[] thisParts = this.get().split("\\.");
        String[] thatParts = version.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart)
                return -1;
            if (thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (this.getClass() != object.getClass())
            return false;
        return this.compareTo((Version) object) == 0;
    }

}
