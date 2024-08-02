//
// PrivMX Endpoint Minimal Java.
// Copyright © 2024 Simplito sp. z o.o.
//
// This file is part of demonstration software for the PrivMX Platform (https://privmx.cloud).
// This software is Licensed under the MIT License.
//
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.simplito;

import com.simplito.java.privmx_endpoint.model.*;
import com.simplito.java.privmx_endpoint.model.exceptions.NativeException;
import com.simplito.java.privmx_endpoint.model.exceptions.PrivmxException;
import com.simplito.java.privmx_endpoint.modules.CoreApi;
import com.simplito.java.privmx_endpoint.modules.CryptoApi;
import com.simplito.java.privmx_endpoint.modules.ThreadApi;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;


public class Main {
    public static void main(String[] args) {
        try {
            CryptoApi cryptoApi = new CryptoApi();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //--------------------------------------------------------------------------------------------------------------
            // 1. Configure OpenSSL certs
            //--------------------------------------------------------------------------------------------------------------
            CoreApi.setCertsPath("cacert.pem");
            System.out.println("Certs path set\n");
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 2. Connect to platform
            //--------------------------------------------------------------------------------------------------------------
            String platformUrl; // URL to connect with your Privmx Bridge
            String solutionId; // ID for solution of your project
            String userPrivateKey; // private key to log into Privmx Bridge
            String userPublicKey = cryptoApi.pubKeyNew(userPrivateKey);
            CoreApi coreApi = CoreApi.platformConnect(
                    userPrivateKey,
                    solutionId,
                    platformUrl
            );
            System.out.println("User connected\n");
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 3. List 10 starting Contexts in ascending order (sorted by creation order)
            //--------------------------------------------------------------------------------------------------------------
            PagingList<ContextInfo> contextList = coreApi.contextList(0, 10, "asc");
            if (contextList == null || contextList.items.isEmpty()) {
                System.err.println("No context, go to ERROR section in README.md to read more.\n");
                return;
            }
            System.out.println("------------------ Contexts list ------------------");
            for (int index = 0; index < Objects.requireNonNull(contextList).items.size(); index++) {
                System.out.println("Context " + (index + 1) + " with id: " + contextList.items.get(index).contextId);
            }
            ContextInfo context = contextList.items.get(0);
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 4. List 10 starting Threads
            //--------------------------------------------------------------------------------------------------------------
            ThreadApi threadApi = new ThreadApi(coreApi);
            PagingList<ThreadInfo> threadList = threadApi.threadList(context.contextId, 0, 10, "asc");
            if (threadList == null) {
                System.out.println("No thread list\n");
                return;
            }
            ThreadInfo thread; //current Thread
            //Creates the first Thread if none exists
            if (threadList.items.isEmpty()) {
                UserWithPubKey me = new UserWithPubKey();
                me.userId = context.userId;
                me.pubKey = userPublicKey;
                List<UserWithPubKey> users = List.of(me); //Defines list of users with access to the Thread
                List<UserWithPubKey> managers = List.of(me); //Defines list of users with manage access to the Thread
                String threadTitle = "Demo Thread";
                String threadID = threadApi.threadCreate(
                        context.contextId,
                        users,
                        managers,
                        threadTitle
                );
                thread = threadApi.threadGet(threadID);
                System.out.printf("\nThread named: %s has been created", threadTitle);
            } else {
                System.out.println("\n------------------ Threads list ------------------");
                for (int index = 0; index < Objects.requireNonNull(threadList).items.size(); index++) {
                    System.out.println("Thread " + (index + 1) + ": " + threadList.items.get(index).data.title);
                }
                thread = threadList.items.get(0);
            }
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 5. List 10 newest messages
            //--------------------------------------------------------------------------------------------------------------
            PagingList<Message> messageList = threadApi.threadMessagesGet(thread.threadId, 0, 10, "desc");
            if (messageList == null || messageList.items.isEmpty()) {
                System.out.println("No messages\n");
            } else {
                System.out.println("\n------------------ Messages list ------------------");
                for (int index = 0; index < Objects.requireNonNull(messageList).items.size(); index++) {
                    Message item = messageList.items.get(index);
                    System.out.println("Message " + (index + 1) + ": " + new String(item.data) + " sent at: " + formatter.format(item.info.createDate));
                }
            }
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 6. Send message
            //--------------------------------------------------------------------------------------------------------------
            String messageContent = "Hello World";
            threadApi.threadMessageSend(
                    thread.threadId,
                    new byte[0],
                    new byte[0],
                    messageContent.getBytes(StandardCharsets.UTF_8)
            );
            System.out.printf("\nMessage \"%s\" sent\n", messageContent);
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 7. List 10 newest messages
            //--------------------------------------------------------------------------------------------------------------
            messageList = threadApi.threadMessagesGet(thread.threadId, 0, 10, "desc");
            if (messageList == null || messageList.items.isEmpty()) {
                System.out.println("No messages\n");
            } else {
                System.out.println("\n------------------ Messages list ------------------");
                for (int index = 0; index < Objects.requireNonNull(messageList).items.size(); index++) {
                    Message item = messageList.items.get(index);
                    System.out.println("Message " + (index + 1) + ": " + new String(item.data) + " sent at: " + formatter.format(item.info.createDate));
                }
            }
            //---------------------------------------------------------------------------------------------------------------

            coreApi.disconnect();
            try {
                cryptoApi.close();
                coreApi.close();
                threadApi.close();
            } catch (Exception ignore) {}
        } catch (PrivmxException e){
            System.err.printf(
                    "Error during execute demo: [%s] with message %s\n",
                    e.getClass().getName(),
                    e.description
            );
        } catch (NativeException | IllegalStateException e){
            System.err.printf(
                    "Error during execute demo: [%s] with message %s\n",
                    e.getClass().getName(),
                    e.getMessage()
            );
        }
    }
}