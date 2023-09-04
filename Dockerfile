FROM registry.access.redhat.com/ubi8/openjdk-17:1.16

ENV LANGUAGE='en_US:en'

USER root
RUN microdnf --setopt=tsflags=nodocs install -y gzip which

USER jboss
SHELL ["/bin/bash", "--login", "-c"]
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.35.3/install.sh | bash
RUN nvm install 16.20.2
RUN npm --version
RUN npm install -g @squoosh/cli
RUN squoosh-cli --help
RUN cat /home/jboss/.bashrc
RUN which squoosh-cli

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --chown=185 target/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/app/ /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus/ /deployments/quarkus/


EXPOSE 8080
USER 185
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

# Add PATH to squoosh-cli
ENV PATH="$PATH:/home/jboss/.nvm/versions/node/v16.20.2/bin"


ENTRYPOINT ["/opt/jboss/container/java/run/run-java.sh" ]

