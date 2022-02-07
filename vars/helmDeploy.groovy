def call(String name, 
         String imageTag, 
         String namespace = "staging",
         Closure body) {
  def label = "helm-${UUID.randomUUID().toString()}"
  def podYaml = libraryResource 'podtemplates/helm.yml'
  podTemplate(name: 'helm', inheritFrom: 'default-jnlp', label: label, yaml: podYaml, podRetention: never(), activeDeadlineSeconds:1) {
    node(label) {
      body()
      stagingUrl = "https://${name}.${env.DEPLOYMENT_ENV}.workshop.cb-sa.io"
      gitHubDeploy(REPO_OWNER, REPO_NAME, "", "staging", GITHUB_CREDENTIAL_ID, "true", "false")
      env.NAME=name
      env.IMAGE_TAG=imageTag
      env.NAMESPACE=namespace
      container('helm') {
        withCredentials([string(credentialsId: 'fm-key', variable: 'FM_KEY')]) {
          sh '''
            helm upgrade --install -f ./chart/values.yaml --set image.tag=$IMAGE_TAG --set fmToken=$FM_KEY --namespace=$NAMESPACE  $NAME ./chart
          '''
        }
      }
      gitHubDeployStatus(REPO_OWNER, REPO_NAME, stagingUrl, 'success', GITHUB_CREDENTIAL_ID)
      //only add comment for PRs - CHANGE_ID isn't populated for commits to regular branches
      if (env.CHANGE_ID) {
        def config = [message:"${env.DEPLOYMENT_ENV} environment deloyed by CloudBees CI and is available at: ${stagingUrl}"]
        gitHubComment(config)
      }
    }
  }
}
