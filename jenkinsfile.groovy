import groovy.json.JsonSlurperClassic
import groovy.json.JsonSlurper

def repoUrl = 'https://github.com/ICEI-PUC-Minas-PPLES-TI/plf-es-2023-2-ti3-6654100-posto-ipiranga.git'
def containerFrontEnd = "https://registry.hub.docker.com/v2/repositories/lucaslotti/postoapp/tags"
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
        stage('Erase Container') {
            steps {
                script {
                    def frontEndChoice = params['front-end']
                    def backEndChoice = params['back-end']
                    try {
                        if (params.gerar_front) {
                            sh("docker rm postoapp -f")
                        }
                        
                        if (params.gerar_back) {
                            sh("docker rm posto-ipiranga -f")
                        }
                    } catch (err) {
                        echo err.getMessage()
                    }
                }
            }
        }
        stage('Run Container') {
            steps {
                script {
                    try {
                        def frontEndChoice = params['front-end']
                        def backEndChoice = params['back-end']
                        if (params.gerar_front) {
                            sh("docker run -d -p 3000:3000 --name postoapp --network=tis-3 lucaslotti/postoapp:${backEndChoice}")
                        }
                        
                        if (params.gerar_back) {
                            sh("docker run -d -p 7000:7000 --name posto-ipiranga --network=tis-3 -e DB_URL=jdbc:postgresql://some-postgres:5432/ -e PORT=7000 lucaslotti/posto-ipiranga:${backEndChoice}")
                        }
                    } catch (err) {
                        echo err.getMessage()
                    }
                }
            }
        }
        stage('Erase Image') {
            steps {
                script {
                    try {

                        sh("docker system prune -a -f")

                    } catch (err) {
                        echo err.getMessage()
                    }
                }
            }
        }
    }
}
