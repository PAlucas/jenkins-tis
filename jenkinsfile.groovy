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
    def result = ("curl -s ${containerFrontEnd}").execute().getText()
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
    environment {
        selected = '[]'
        imageOk = '[]'
        build_ok = '[]'
        build_error = '[]'
    }
    parameters {
        booleanParam(name: 'deploy', defaultValue: true, description: 'Realizar o deploy no ambiente de qualidade')
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

                    } catch (err) {
                        echo err.getMessage()
                    }
                }
            }
        }
        stage('Pull') {
            steps {
                script {
                    try {
                        def frontEndChoice = params['front-end']
                        def backEndChoice = params['back-end']
                        
                        if (params.gerar_front) {
                            sh("docker pull lucaslotti/posto-ipiranga:${backEndChoice}")
                        }
                        
                        if (params.gerar_back) {
                            sh("docker pull lucaslotti/postoapp:${backEndChoice}")
                        }
                          
                    } catch (err) {
                        echo err.getMessage()
                    }
                }
            }
        }
        stage('Erase') {
            steps {
                script {
                    try {
                        sh("docker rm react-app -f")

                        sh("docker rm back-end-posto -f")
                        
                    } catch (err) {
                        echo err.getMessage()
                    }
                }
            }
        }
    }
}
