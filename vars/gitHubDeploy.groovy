def call(String gitHubOrg, String gitHubRepo, String deployUrl = "", String environment = 'staging', String credentialId = env.credId, String transientEnv = 'false', String productionEnv = 'false') {
  gitHubCredentialsId = credentialId ?: GITHUB_CREDENTIAL_ID
  env.gitHubRepoOwner = gitHubOrg ?: REPO_OWNER
  env.gitHubRepo = gitHubRepo ?: REPO_NAME     
  def ref = env.CHANGE_BRANCH ?: env.BRANCH_NAME 
  description = "CloudBees_CI_Deployment"
  env.DATA="""{"ref":"${ref}","environment":"${environment}","required_contexts":[],"description":"${description}","auto_merge":false,"transient_environment":${transientEnv},"production_environment":${productionEnv}}"""
  withCredentials([usernamePassword(credentialsId: "${credentialId}", usernameVariable: 'GITHUB_APP', passwordVariable: 'GITHUB_ACCESS_TOKEN')]) {
   def deploymentId =  sh(script: '''
      curl \
        -X POST \
        -H "authorization: Bearer $GITHUB_ACCESS_TOKEN" \
        -H 'Accept: application/vnd.github.antiope-preview+json' \
        -H 'Accept: application/vnd.github.v3+json' \
        -H 'Accept: application/vnd.github.flash-preview+json' \
        -H 'Accept: application/vnd.github.ant-man-preview+json' \
        https://api.github.com/repos/$gitHubRepoOwner/$gitHubRepo/deployments \
        --data $DATA \
      | jq -r '.id' | tr -d '\n' 
    ''', returnStdout: true)
    env.GITHUB_DEPLOYMENT_ID = deploymentId
    echo "GitHub deployment id: ${env.GITHUB_DEPLOYMENT_ID}"
    gitHubDeployStatus(gitHubOrg, gitHubRepo, deployUrl, 'in_progress', gitHubCredentialsId)
  }
} 
