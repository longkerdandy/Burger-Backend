## 1. Overview

Burger-Backend is an application project which provide RESTful API. It contains following information:

- Configuration at `com.github.longkerdandy.burger.backend.config`
- REST Controller at `com.github.longkerdandy.burger.backend.controller`
- Model at `com.github.longkerdandy.burger.backend.model`
- Model DTO at `com.github.longkerdandy.burger.backend.dto`
- Model Mapper at `com.github.longkerdandy.burger.backend.mapper`
- Repository at `com.github.longkerdandy.burger.backend.repository`
- HTTP Filter at `com.github.longkerdandy.burger.backend.filter`
- HTTP Security at `com.github.longkerdandy.burger.backend.security`
- Util at `com.github.longkerdandy.burger.backend.util`


## 2. Prerequisite

### 2.1 Install Azul Zulu for Azure - Enterprise Edition JDK builds

To make sure our JDK environment is compatible with Azure, we need to download and install
the [Azul Zulu for Azure - Enterprise Edition JDK](https://www.azul.com/downloads/azure-only/zulu/),
and configured it as the default JDK in your system and IntelliJ IDEA IDE.

### 2.2 Apply the [Google Java Style](https://google.github.io/styleguide/javaguide.html)

Download the `intellij-java-google-style.xml` file from the official google/styleguide
repository [here](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)
.

- Launch IntelliJ and go to the **Settings > Editor > Code Style > Java**.
- Select the gear icon then Import Scheme > IntelliJ IDEA code style XML then select
  the`intellij-java-google-style.xml` file you downloaded from GitHub.
  
![Import Schema](https://miro.medium.com/max/2400/1*V0Ej8dLSdDYW8raQgMf5aw.png)


## 3 Development & Deployment

### 3.1 IntelliJ IDEA Run & Debug
Run/Debug the project through SpringBoot application configuration, make sure you specific the
Spring active profile by add the VM variables like `-Dspring.profiles.active=dev`.

### 3.2 Build with maven
Using following command to build with maven:
```bash
mvn clean install
```


## 4. Work Flow

### 4.1 Git branches naming and usage

| Branch     | Usage and workflow                                                                                                        | Example                            |
|------------|---------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| master     | Used as the develop branch and will auto deploy to the staging env. Commit to the master should go through pull request.  |                                    |
| staging    | Used as the staging branch and will auto deploy to the staging env. Commit to the staging should go through pull request. |                                    |
| production | Used as the production branch and will auto deploy to the staging env. Production should only be merged from staging.     |                                    |
| feature    | Temporal branch from master for developing new features.                                                                  | feature/integrate-swagger.         |
| bugfix     | Temporal branch from master for bug fixing.                                                                               | bugfix/user-mapper-null-pointer.   |
| hotfix     | Temporal branch from master for bug fixing but need release immediately.                                                  | hotfix/increase-scaling-threshold. |