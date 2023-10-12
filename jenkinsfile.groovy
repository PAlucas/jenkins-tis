import groovy.json.JsonSlurperClassic
import groovy.json.JsonSlurper

def repoUrl = 'https://github.com/ICEI-PUC-Minas-PPLES-TI/plf-es-2023-2-ti3-6654100-posto-ipiranga.git'
def containerBackEnd = "https://registry.hub.docker.com/v2/repositories/lucaslotti/posto-ipiranga/tags"


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
    agent {
        label 'ubuntu-2004'
    }
    environment {
        selected = '[]'
        imageOk = '[]'
        build_ok = '[]'
        build_error = '[]'
    }
    parameters {
        choice(name: 'back-end', choices: getTags (containerBackEnd), description: '')
    }
    options {
        timestamps()
    }
    stages {
        stage('Info') {
            steps {
                script {
                    try {
                        def backEndChoice = params['back-end']
                    
                            def fileJson = readFile();
                            println fileJson
                            echo "Back-end escolhido: ${backEndChoice}"
                            sh "ls"

                    } catch (err) {
                        echo err.getMessage()
                    }
                }
            }
        }
    }
}
