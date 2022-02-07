def call(String gitHubOrg, String gitHubRepo, String deployUrl, String status = 'in_progress', String credentialId) {
  gitHubCredentialsId = credentialId ?: GITHUB_CREDENTIAL_ID
  env.gitHubRepoOwner = gitHubOrg ?: REPO_OWNER
  env.gitHubRepo = gitHubRepo ?: REPO_NAME
  env.DATA = """{"state":"${status}","environment_url":"${deployUrl}","log_url":"${BUILD_URL}"}"""
  withCredentials([usernamePassword(credentialsId: gitHubCredentialsId, usernameVariable: 'GITHUB_APP', passwordVariable: 'GITHUB_ACCESS_TOKEN')]) {
    sh(script: '''
      curl \
        -X POST \
        -H "authorization: Bearer $GITHUB_ACCESS_TOKEN" \
        -H 'Accept: application/vnd.github.antiope-preview+json' \
        -H "Accept: application/vnd.github.v3+json" \
        -H "Accept: application/vnd.github.ant-man-preview+json"  \
        -H "Accept: application/vnd.github.flash-preview+json" \
        https://api.github.com/repos/$gitHubRepoOwner/$gitHubRepo/deployments/$GITHUB_DEPLOYMENT_ID/statuses \
        --data $DATA
    ''')
  }
} 
