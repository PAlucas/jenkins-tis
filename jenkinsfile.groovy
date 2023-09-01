import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListTagCommand
import org.eclipse.jgit.lib.Ref
import groovy.json.JsonSlurperClassic

class Config {
    def artefato
    def project
    def branch
    def url
}

def repoUrl = 'https://github.com/PAlucas/trab1.git'

def gettags (){
    def result = ("git ls-remote --tags https://github.com/PAlucas/trab1.git").execute().getText().replaceAll('refs/tags/', '')
    def list = result.split()
    def novolist = []
    list.each{value ->
        def primeiraLetra = value.split('');
        if(primeiraLetra[0] == 'v'){
            novolist << value
        }
    }
    return novolist
}


def select(gerar_artefato, artefato, branch, url, selected) {
    if (gerar_artefato) {
        item = new Config()
        item.artefato = artefato
        item.branch = branch
        item.url = url

        selected.add(item)
    }
}
def jsonParseAux(jsonAux) {
    new groovy.json.JsonSlurperClassic().parseText(json)
}


@NonCPS
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)
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
        choice(name: 'front-end', choices: gettags (), description: '')
        booleanParam(name: 'gerar_front', defaultValue: false, description: '')
        choice(name: 'back-end', choices: gettags (), description: '')
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
