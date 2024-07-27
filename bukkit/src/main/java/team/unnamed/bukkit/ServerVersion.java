package team.unnamed.bukkit;

import org.bukkit.Bukkit;

import java.util.regex.Pattern;

public class ServerVersion {

    public static final String VERSION_STRING = Bukkit.getBukkitVersion()
            .split(Pattern.quote("-"))[0];

    public static final String VERSION_STRING_OLD = Bukkit.getServer()
            .getClass()
            .getName()
            .split(Pattern.quote("."))[3];

    public static final ServerVersion CURRENT = getVersionOfString(VERSION_STRING, VERSION_STRING_OLD);

    private final byte major;
    private final byte minor;
    private final byte patch;
    private final boolean isOld;

    public ServerVersion(byte major, byte minor, byte patch, boolean isOld) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.isOld = isOld;
    }
    public ServerVersion(byte major, byte minor) {
        this.major = major;
        this.minor = minor;
        this.patch = 0;
        this.isOld = false;
    }

    public byte getMajor() {
        return major;
    }

    public byte getMinor() {
        return minor;
    }

    public byte getPatch() {
        return patch;
    }

    @Override
    public String toString() {
        if (patch == 0) {
            return "v" + major + '_' + minor;
        }
        if (isOld)
            return "v" + major + '_' + minor + "_R" + patch;
        return "v" + major + '_' + minor + "_" + patch;
    }

    /**
     * Resolves a {@link ServerVersion} from a given {@code versionString}
     * with the format v(major)_(minor)_R(patch)
     *
     * @throws NumberFormatException If major, minor or patch versions
     *                               are not bytes
     */
    public static ServerVersion getVersionOfString(String versionString,String versionStringOld) {
        String[] args = versionString.split(Pattern.quote("."));
        byte major = Byte.parseByte(args[0]);
        byte minor = Byte.parseByte(args[1]);
        if (args.length == 2) {
            return new ServerVersion(major, minor);
        }
        byte patch = Byte.parseByte(args[2]);
        if(minor <= 20 && patch < 5)
            return getVersionOfStringOld(versionStringOld);
        return new ServerVersion(major, minor, patch,false);
    }
    public static ServerVersion getVersionOfStringOld(String versionString) {
        String[] args = versionString.split(Pattern.quote("_"));
        byte major = Byte.parseByte(args[0].substring(1));
        byte minor = Byte.parseByte(args[1]);
        byte patch = Byte.parseByte(args[2].substring(1));
        return new ServerVersion(major, minor, patch,true);
    }

}
