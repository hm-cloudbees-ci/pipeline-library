// vars/cloudRunDeploy.groovy
def call(Map config) {
  def podYaml = libraryResource 'podtemplates/google-cloud-sdk.yml'
  def label = "cloud-sdk-${UUID.randomUUID().toString()}"
  def CLOUD_RUN_URL
  podTemplate(inheritFrom: 'default-jnlp', name: 'cloud-sdk', label: label, yaml: podYaml) {
    node(label) {
      container(name: 'gcp-sdk') {
        if (config.deployType == "gke") {
          sh "gcloud run deploy ${config.serviceName} --image ${config.image} --platform gke --cluster ${config.clusterName} --cluster-location ${config.region} --namespace ${config.namespace}"
          sh "gcloud run services describe ${config.serviceName} --platform gke --cluster ${config.clusterName} --cluster-location ${config.region} --namespace ${config.namespace} --format=json > run.json 2>&1"
        }
        else if (config.deployType == "vmware") {
          sh "gcloud run deploy ${config.serviceName} --image ${config.image} --platform kubernetes --namespace ${config.namespace} --kubeconfig ${config.kubeconfig}"
          sh "gcloud run services describe ${config.serviceName} --platform kubernetes --kubeconfig ${config.kubeconfig} --format=json > run.json 2>&1"
        }
        else {
          sh "gcloud run deploy ${config.serviceName} --image ${config.image} --allow-unauthenticated --region ${config.region} --platform managed --port 8080 --project core-workshop"
          sh "gcloud run services describe ${config.serviceName} --region ${config.region} --platform managed --project core-workshop --format=json > run.json 2>&1"
        } 
      }
      //print detail description of deployed servce
      sh "cat run.json"

      CLOUD_RUN_URL = sh (script: "cat run.json | jq -r '.status.url' | tr -d '\n'", 
                returnStdout: true)
      gitHubDeployStatus(repoOwner, repo, CLOUD_RUN_URL, 'success')
      //only add comment for PRs - CHANGE_ID isn't populated for commits to regular branches
      if (env.CHANGE_ID) {
        config.message = "Preview Environment URL: ${CLOUD_RUN_URL}"
        config.credId = githubCredentialId
        config.issueId = env.CHANGE_ID
        config.repoOwner = repoOwner
        config.repo = repo
        gitHubComment(config)
      }
    }
  }
}
