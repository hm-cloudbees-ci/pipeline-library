// vars/cloudRunDelete.groovy
def call(Map config) {
  def podYaml = libraryResource 'podtemplates/google-cloud-sdk.yml'
  def label = "cloudrun-${UUID.randomUUID().toString()}"
  podTemplate(name: 'cloud-sdk', label: label, yaml: podYaml) {
    node(label) {
      container(name: 'gcp-sdk') {
        echo "region = ${config.region}"
        if (config.deployType == "gke") {
          sh "gcloud beta run services delete ${config.serviceName} --platform gke --cluster ${config.clusterName} --cluster-location ${config.region} --namespace ${config.namespace} --quiet"
        }
        else if (config.deployType == "vmware") {
          sh "gcloud beta run services delete ${config.serviceName} --platform kubernetes --namespace ${config.namespace} --kubeconfig ${config.kubeconfig} --quiet"
        }
        else {
          sh "gcloud beta run services delete ${config.serviceName} --region ${config.region} --platform managed --quiet --project core-workshop"
        } 
      }
    }
  }
}
