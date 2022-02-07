def call(Map config) {
  echo "gitHubComment"
  gitHubCredentialsId = config.credId ?: GITHUB_CREDENTIAL_ID
  gitHubRepoOwner = config.repoOwner ?: REPO_OWNER
  gitHubRepo = config.repo ?: REPO_NAME
  env.MESSAGE="${config.message}"
  withCredentials([usernamePassword(credentialsId: gitHubCredentialsId, usernameVariable: 'GITHUB_APP', passwordVariable: 'GITHUB_ACCESS_TOKEN')]) {
    def commentId = sh(script: '''
      curl -s -H "authorization: Bearer ${GITHUB_ACCESS_TOKEN}" \
        -X POST -d  '{"body":"'"${MESSAGE}"'"}' \
        "https://api.github.com/repos/$gitHubRepoOwner/$gitHubRepo/issues/$CHANGE_ID/comments" \
      | jq -r '.id' | tr -d '\n' 
    ''', returnStdout: true)
    echo "gitHubComment commentId: ${commentId}"
    return commentId
  }
}      
