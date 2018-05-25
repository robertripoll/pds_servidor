// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package org.udg.pds.cheapy.util;

import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import org.udg.pds.cheapy.model.Missatge;
import org.udg.pds.cheapy.model.User;

import javax.inject.Inject;
import java.time.Duration;


public class FirebaseClient {

    @Inject
    Global global;

    public void enviaNotificacioMissatge(User u, Missatge m) throws Exception {

        //FcmClient client = global.getFirebaseClient();

        // Create the Client using system-properties-based settings:
        //try (FcmClient client = global.getFirebaseClient()) {

            // Message Options:
            FcmMessageOptions options = FcmMessageOptions.builder()
                    .setTimeToLive(Duration.ofHours(1))
                    .build();

            // Send a Message:
            //FcmMessageResponse response = client.send(new NotificationUnicastMessage(options, u.getToken(),new NotificationPayload(null,m.getMissatge(),null,null,null,"Nou missatge","white",null,null,null, null, null, null)));
            //TopicMessageResponse response = client.send(new TopicUnicastMessage(options, new Topic("news"), new PersonData("Philipp", "Wagner")));
       // }
    }
}