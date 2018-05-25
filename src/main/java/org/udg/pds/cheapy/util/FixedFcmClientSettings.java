// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package org.udg.pds.cheapy.util;

import de.bytefish.fcmjava.constants.Constants;
import de.bytefish.fcmjava.http.options.IFcmClientSettings;

public class FixedFcmClientSettings implements IFcmClientSettings {

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

    public IFcmClientSettings createClient(String apiKey) {

        // Construct the FCM Client Settings with your API Key:
        IFcmClientSettings clientSettings = new FixedFcmClientSettings(apiKey);

        // Instantiate the FcmClient with the API Key:
        return clientSettings;
    }

}