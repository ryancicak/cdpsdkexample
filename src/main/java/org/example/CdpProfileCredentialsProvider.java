package org.example;

import static  com.cloudera.cdp.ValidationUtils.checkNotNullAndThrow;

import com.cloudera.cdp.authentication.credentials.CdpCredentials;
import com.cloudera.cdp.authentication.credentials.CdpCredentialsProvider;
import com.cloudera.cdp.authentication.credentials.profile.CdpProfileConfigFile;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

/**
 * Credentials provider based on CDP configuration profiles. This provider
 * reads credentials from the profile configuration file for the default
 * profile, or for a specific, named profile.
 */
public class CdpProfileCredentialsProvider
        implements CdpCredentialsProvider {

    /**
     * Name of system environment variable holding the name of profile to use
     * when reading the credentials file. Overrides CDP_DEFAULT_PROFILE_NAME.
     */
    public static final String CDP_DEFAULT_PROFILE = "CDP_DEFAULT_PROFILE";

    /**
     * Name of the profile in the users credentials file to read.
     */
    public static final String CDP_DEFAULT_PROFILE_NAME = "default";

    private volatile CdpProfileConfigFile cdpProfileConfigFile;
    private final String cdpProfileName;

    /**
     * Uses the default CDP location (~/.cdp/credentials) and default profile
     * name. Loading of the credential file is deferred until getCredentials()
     * method is called
     */
    public CdpProfileCredentialsProvider() {
        this(null);
    }

    /**
     * Uses the default CDP location (~/.cdp/credentials) and input profile
     * name, or the default if the input profile name is null. Loading of the
     * credential file is deferred until getCredentials() method is called.
     * @param profileName profile name to use, "default" is used if null
     */
    public CdpProfileCredentialsProvider(
            @Nullable String profileName) {
        this((CdpProfileConfigFile) null, profileName);
    }

    /**
     * Uses the input profile file path and input profile name, or the default
     * if the input profile name is null. Loading of credential file is deferred
     * until getCredentials() method is called.
     * @param profileFilePath path to the credentials file
     * @param profileName profile name to use, "default" is used if null
     */
    public CdpProfileCredentialsProvider(
            String profileFilePath,
            @Nullable String profileName) {
        this(new CdpProfileConfigFile(checkNotNullAndThrow(profileFilePath)),
                profileName);
    }

    private CdpProfileCredentialsProvider(
            @Nullable CdpProfileConfigFile profilesConfigFile,
            @Nullable String profileName) {
        this.cdpProfileConfigFile = profilesConfigFile;
        if (StringUtils.isBlank(profileName)) {
            this.cdpProfileName = getCdpProfileName();
        } else {
            this.cdpProfileName = profileName;
        }
    }

    private String getCdpProfileName() {
        String envProfileName = System.getenv(CDP_DEFAULT_PROFILE);
        if (StringUtils.isBlank(envProfileName)) {
            return CDP_DEFAULT_PROFILE_NAME;
        } else {
            return envProfileName;
        }
    }

    @Override
    public CdpCredentials getCredentials() {
        if (cdpProfileConfigFile == null) {
            synchronized (this) {
                if (cdpProfileConfigFile == null) {
                    cdpProfileConfigFile = new CdpProfileConfigFile();
                }
            }
        }
        return cdpProfileConfigFile.getCredentials(this.cdpProfileName);
    }
}


