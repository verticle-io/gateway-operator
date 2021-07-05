package io.verticle.gateway.operator.gatewayoperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GitBuildInfo {

    private static final Logger log = LoggerFactory.getLogger(GitBuildInfo.class);

    @Autowired
    GitProperties gitProperties;

    @Autowired
    BuildProperties buildProperties;

    @PostConstruct
    public void info(){

        if (gitProperties != null){
            log.info("git commit     " + gitProperties.getShortCommitId());
            log.info("git time       " + gitProperties.getCommitTime());
            log.info("git branch     " + gitProperties.getBranch());
        }

        if (buildProperties != null){
            log.info("build name     " + buildProperties.getName());
            log.info("build artifact " + buildProperties.getArtifact());
            log.info("build group    " + buildProperties.getGroup());
            log.info("build version  " + buildProperties.getVersion());
            log.info("build time     " + buildProperties.getTime());
        }
    }
}
