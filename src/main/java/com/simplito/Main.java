//
// PrivMX Endpoint Minimal Java.
// Copyright © 2024 Simplito sp. z o.o.
//
// This file is part of demonstration software for the PrivMX Platform (https://privmx.dev).
// This software is Licensed under the MIT License.
//
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.simplito;

import com.simplito.java.privmx_endpoint.model.*;
import com.simplito.java.privmx_endpoint.model.Thread;
import com.simplito.java.privmx_endpoint.model.exceptions.NativeException;
import com.simplito.java.privmx_endpoint.model.exceptions.PrivmxException;
import com.simplito.java.privmx_endpoint.modules.core.Connection;
import com.simplito.java.privmx_endpoint.modules.crypto.CryptoApi;
import com.simplito.java.privmx_endpoint.modules.thread.ThreadApi;

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
            // 1. Connect to platform
            //--------------------------------------------------------------------------------------------------------------
            String platformUrl = "<PLATFORM_URL>"; // URL to connect with your Privmx Bridge
            String solutionId = "<SOLUTION_ID>"; // ID for solution of your project
            String userPrivateKey = "<PRIVATE_KEY>"; // private key to log into Privmx Bridge
            String userPublicKey = cryptoApi.derivePublicKey(userPrivateKey);
            Connection connection = Connection.connect(
                    userPrivateKey,
                    solutionId,
                    platformUrl
            );
            System.out.println("User connected\n");

            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 2. List 10 starting Contexts in ascending order (sorted by creation order)
            //--------------------------------------------------------------------------------------------------------------
            PagingList<Context> contextList = connection.listContexts(0, 10, "asc");
            if (contextList == null || contextList.readItems.isEmpty()) {
                System.err.println("No context, go to ERROR section in README.md to read more.\n");
                return;
            }
            System.out.println("------------------ Contexts list ------------------");
            for (int index = 0; index < Objects.requireNonNull(contextList).readItems.size(); index++) {
                System.out.println("Context " + (index + 1) + " with id: " + contextList.readItems.get(index).contextId);
            }
            Context context = contextList.readItems.getFirst();
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 3. List 10 starting Threads
            //--------------------------------------------------------------------------------------------------------------
            ThreadApi threadApi = new ThreadApi(connection);
            PagingList<Thread> threadList = threadApi.listThreads(context.contextId, 0, 10, "asc");
            if (threadList == null) {
                System.out.println("No thread list\n");
                return;
            }
            Thread thread; //current Thread
            //Creates the first Thread if none exists
            if (threadList.readItems.isEmpty()) {
                UserWithPubKey me = new UserWithPubKey();
                me.userId = context.userId;
                me.pubKey = userPublicKey;
                List<UserWithPubKey> users = List.of(me); //Defines list of users with access to the Thread
                List<UserWithPubKey> managers = List.of(me); //Defines list of users with manage access to the Thread
                String threadTitle = "Demo Thread";
                String threadID = threadApi.createThread(
                        context.contextId,
                        users,
                        managers,
                        new byte[0],
                        threadTitle.getBytes(StandardCharsets.UTF_8)
                );
                thread = threadApi.getThread(threadID);
                System.out.printf("\nThread named: %s has been created", threadTitle);
            } else {
                System.out.println("\n------------------ Threads list ------------------");
                for (int index = 0; index < Objects.requireNonNull(threadList).readItems.size(); index++) {
                    System.out.println("Thread " + (index + 1) + ": " + new String(threadList.readItems.get(index).privateMeta));
                }
                thread = threadList.readItems.getFirst();
            }
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 4. List 10 newest messages
            //--------------------------------------------------------------------------------------------------------------
            PagingList<Message> messageList = threadApi.listMessages(thread.threadId, 0, 10, "desc");
            if (messageList == null || messageList.readItems.isEmpty()) {
                System.out.println("No messages\n");
            } else {
                System.out.println("\n------------------ Messages list ------------------");
                for (int index = 0; index < Objects.requireNonNull(messageList).readItems.size(); index++) {
                    Message item = messageList.readItems.get(index);
                    System.out.println("Message " + (index + 1) + ": " + new String(item.data) + " sent at: " + formatter.format(item.info.createDate));
                }
            }
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 5. Send message
            //--------------------------------------------------------------------------------------------------------------
            String messageContent = "Hello World";
            threadApi.sendMessage(
                    thread.threadId,
                    new byte[0],
                    new byte[0],
                    messageContent.getBytes(StandardCharsets.UTF_8)
            );
            System.out.printf("\nMessage \"%s\" sent\n", messageContent);
            //--------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------------------------------------------------------------------
            // 6. List 10 newest messages
            //--------------------------------------------------------------------------------------------------------------
            messageList = threadApi.listMessages(thread.threadId, 0, 10, "desc");
            if (messageList == null || messageList.readItems.isEmpty()) {
                System.out.println("No messages\n");
            } else {
                System.out.println("\n------------------ Messages list ------------------");
                for (int index = 0; index < Objects.requireNonNull(messageList).readItems.size(); index++) {
                    Message item = messageList.readItems.get(index);
                    System.out.println("Message " + (index + 1) + ": " + new String(item.data) + " sent at: " + formatter.format(item.info.createDate));
                }
            }
            //---------------------------------------------------------------------------------------------------------------

            connection.disconnect();
            try {
                cryptoApi.close();
                connection.close();
                threadApi.close();
            } catch (Exception ignore) {
            }
        } catch (PrivmxException e) {
            System.err.printf(
                    "Error during execute demo: [%s] with message %s\n",
                    e.getClass().getName(),
                    e.description
            );
        } catch (NativeException | IllegalStateException e) {
            System.err.printf(
                    "Error during execute demo: [%s] with message %s\n",
                    e.getClass().getName(),
                    e.getMessage()
            );
        }
    }
}