# Privmx Endpoint Minimal Java

Console app written in Java demonstrating the usage
of [Privmx Endpoint Java library](https://github.com/simplito/privmx-endpoint-java).

To use this application, install Chatee Server and PrivMX Bridge on your local infrastructure. For detailed
installation instructions, go to the [Privmx Bridge](https://github.com/simplito/privmx-bridge).
To use this example you need a Solution, a Context within it and a user that has been added to that Context in
the PrivMX Bridge.
See the [Documentation](https://docs.privmx.dev) for relevant instructions.

## How to start

1. Run `Sync`

2. If native libraries are not installed automatically (there is no `src/main/jniLibs` directory in the project) then
   run:

```shell
./gradlew app:privmxEndpointInstallJni
```

3. Set required variables in Main.java file:
    - platformUrl - URL to connect with your Privmx Bridge
    - solutionId - ID for solution of your project
    - userPrivateKey - private key to log in to Privmx Bridge

4. Create Run Configuration and add the line below into its VM options:

```text
-Djava.library.path=src/main/jniLibs/Android/arm64_v8a:src/main/jniLibs/Android/armeabi_v7a:src/main/jniLibs/Android/x86:src/main/jniLibs/Android/x86_64:src/main/jniLibs/Darwin/arm64:src/main/jniLibs/arm64:src/main/jniLibs/arm64_v8a:src/main/jniLibs/armeabi_v7a:src/main/jniLibs/x86:src/main/jniLibs/x86_64
```

5. Start the app using the created configuration.

## Errors

`No context` message means that there are no existing Contexts in your PrivMX Bridge. Add a Context within your
Solution [as described in PrivMX Bridge documentation](https://github.com/simplito/privmx-bridge)

## About PrivMX

[PrivMX](http://privmx.com)  allows developers to build end-to-end encrypted apps used for communication. The Platform
works according to privacy-by-design mindset, so all of our solutions are based on Zero-Knowledge architecture. This
project extends PrivMX’s commitment to security by making its encryption features accessible to developers using Java.

## License

PrivMX Endpoint Minimal Java. \
Copyright © 2024 Simplito sp. z o.o.

This file is part of demonstration software for the PrivMX Platform (https://privmx.dev). \
This software is Licensed under the MIT License.

See the License for the specific language governing permissions and \
limitations under the License.

