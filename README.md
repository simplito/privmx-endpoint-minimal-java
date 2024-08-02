# Privmx Endpoint Minimal Java
Console app written in Java demonstrating the use of Privmx Endpoint Java library to work with Privmx Bridge.

## How to start
1. If you do not have a Github Personal Access Token (PAT), create it as described on [Github Docs](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens).

2. Add created Github PAT to [`local.properties`](local.properties)
```text
privmxGithubMavenUsername=<your_github_username>
privmxGithubMavenPassword=<your_github_pat>
```
3. Run `Sync`

4. If native libraries are not installed automatically (no `src/main/jniLibs` directory in project) then run
```shell
./gradlew app:privmxEndpointInstallJni
```
5. Set required variables in Main.java file:
   - platformUrl - URL to connect with your Privmx Bridge
   - solutionId - ID for solution of your project
   - userPrivateKey - private key to log in Privmx Bridge

6. Create Run Configuration and add the line below into its VM options:
```text
-Djava.library.path=src/main/jniLibs/Android/arm64_v8a:src/main/jniLibs/Android/armeabi_v7a:src/main/jniLibs/Android/x86:src/main/jniLibs/Android/x86_64:src/main/jniLibs/Darwin/arm64:src/main/jniLibs/arm64:src/main/jniLibs/arm64_v8a:src/main/jniLibs/armeabi_v7a:src/main/jniLibs/x86:src/main/jniLibs/x86_64
```
7. Start the app using created configuration

## Errors

`No context` message means that there are no existing Context on your PrivMX Bridge. Add a Context using [privmx.cloud](https>//privmx.cloud) Panel. Follow our [guide](https://docs.privmx.cloud/cloud) to see exactly how to do it.
