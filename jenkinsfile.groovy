import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListTagCommand
import org.eclipse.jgit.lib.Ref
import groovy.json.JsonSlurperClassic

import groovy.json.JsonSlurperClassic
import groovy.json.JsonSlurper

def repoUrl = 'https://github.com/ICEI-PUC-Minas-PPLES-TI/plf-es-2023-2-ti3-6654100-posto-ipiranga.git'
def containerFrontEnd = "https://registry.hub.docker.com/v2/repositories/library/nginx/tags"
def containerBackEnd = "https://registry.hub.docker.com/v2/repositories/library/nginx/tags"


def jsonParseAux(jsonAux) {
    def jsonSlurper = new JsonSlurper()
    def result = jsonSlurper.parseText(jsonAux)
}
def getTags (container){
    def result = ("curl -s ${container}").execute().getText()
    def object = jsonParseAux(result)
    def results = object.results
    def list = []
    results.each{value ->
        list << value.name
    }
    return list
}

pipeline {
    agent any
    parameters {
        choice(name: 'front-end', choices: getTags (containerFrontEnd), description: '')
        booleanParam(name: 'gerar_front', defaultValue: false, description: '')
        choice(name: 'back-end', choices: getTags (containerBackEnd), description: '')
        booleanParam(name: 'gerar_back', defaultValue: false, description: '')
    }
    options {
        timestamps()
    }
    stages {
        stage('Info') {
            steps {
                script {
                    try {
                        def frontEndChoice = params['front-end']
                        def backEndChoice = params['back-end']
                        
                        if (params.gerar_front) {
                            echo "Front-end escolhido: ${frontEndChoice}"
                        }
                        
                        if (params.gerar_back) {
                            echo "Back-end escolhido: ${backEndChoice}"
                        }
                        // Parse tags from the output
                        println params.gerar_front
                    } catch (err) {
                        echo err.getMessage()
                    }
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    try {
                        sh("docker ps")
                        
                    } catch (err) {
                        echo err.getMessage()
                    }
                }
            }
        }
    }
}
