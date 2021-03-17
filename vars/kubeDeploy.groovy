// vars/kubeDeploy.groovy
def call(String imageName, String imageTag, String githubCredentialId, String repoOwner, String gcpProject = "core-workshop") {
  def label = "kubectl"
  def podYaml = libraryResource 'podtemplates/kubectl.yml'
  def deployYaml = libraryResource 'k8s/basicDeploy.yml'

  podTemplate(name: 'kubectl', label: label, yaml: podYaml) {
    node(label) {
      imageNameTag()
      def repoName = env.IMAGE_REPO.toLowerCase()
      //create environment repo for prod if it doesn't already exist
      writeFile file: "deploy.yml", text: deployYaml
      sh """
        sed -i.bak 's#REPLACE_IMAGE_TAG#gcr.io/${gcpProject}/helloworld-nodejs:${repoName}-${BUILD_NUMBER}#' deploy.yml
        sed -i.bak 's#REPLACE_SERVICE_NAME#${repoName}#' deploy.yml
      """
      
      container("kubectl") {
        sh "kubectl apply -f deploy.yml"
        sh "echo 'deployed to http://staging.cbci.workshop.cb-sa.io/${repoName}/'"
      }
    }
  }
}
