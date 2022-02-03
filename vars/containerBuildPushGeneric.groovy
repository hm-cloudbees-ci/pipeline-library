// vars/containerBuildPushGeneric.groovy
def call(String imageName, String imageTag = env.BUILD_NUMBER, String containerRegistry = "us-east1-docker.pkg.dev/core-workshop/workshop-registry", Closure body) {
  def label = "kaniko-${UUID.randomUUID().toString()}"
  def podYaml = libraryResource 'podtemplates/kaniko.yml'
  def customBuildArg = ""
  def buildModeArg = ""
  podTemplate(name: 'kaniko', inheritFrom: 'default-jnlp', label: label, yaml: podYaml, podRetention: never(), activeDeadlineSeconds:1) {
    node(label) {
      body()
      try {
        env.VERSION = readFile 'version.txt'
        env.VERSION = env.VERSION.trim()
        env.VERSION = "${env.VERSION}-${BUILD_NUMBER}"
        imageTag = env.VERSION
      } catch(e) {}
      if(env.EVENT_PUSH_IMAGE_TAG) {
        customBuildArg = "--build-arg BASE_IMAGE=${env.EVENT_PUSH_IMAGE_NAME}:${env.EVENT_PUSH_IMAGE_TAG}"
      }
      if(env.BRANCH_NAME != "main") {
        buildModeArg = "--build-arg BUILD_MODE=build:dev" 
      }
      imageName = imageName.toLowerCase()
      container(name: 'kaniko', shell: '/busybox/sh') {
        withEnv(['PATH+EXTRA=/busybox:/kaniko']) {
          sh label: "container build and push", script: """#!/busybox/sh
            /kaniko/docker-credential-gcr configure-docker --registries=${containerRegistry}
            /kaniko/executor -f ${pwd()}/Dockerfile -c ${pwd()} ${buildModeArg} ${customBuildArg} --build-arg buildNumber=${BUILD_NUMBER} --build-arg commitAuthor='${COMMIT_AUTHOR}' --build-arg shortCommit=${env.SHORT_COMMIT} --cache=true -d ${containerRegistry}/${imageName}:${env.SHORT_COMMIT}
          """
        }
      }
    }
  }
}
