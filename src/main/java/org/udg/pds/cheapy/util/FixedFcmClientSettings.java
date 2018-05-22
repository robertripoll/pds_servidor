// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package org.udg.pds.cheapy.util;

import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.constants.Constants;
import de.bytefish.fcmjava.http.client.IFcmClient;
import de.bytefish.fcmjava.http.options.IFcmClientSettings;

class FixedFcmClientSettings implements IFcmClientSettings {

    private final String apiKey;

    public FixedFcmClientSettings(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String getFcmUrl() {
        return Constants.FCM_URL;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }
}

class FcmClientSettingsTest {

    public void testFixedClientSettings() {

        // Construct the FCM Client Settings with your API Key:
        IFcmClientSettings clientSettings = new FixedFcmClientSettings("AAAACmcXDWE:APA91bG-T1k-Pd5I7ahE6rdYqCVNikbFqSGFI8fbLG9L-vutz0t9ultjE8eieyrTJNEiNRW7jc2fsttxOV2S4ROSrbYNzDSRWaNvPDywTE5AlYQFdESktORB4I2tu9YGSWkNACaBfYKE");

        // Instantiate the FcmClient with the API Key:
        IFcmClient client = new FcmClient(clientSettings);
    }

}