def call() {
  env.GITHUB_ORIGIN_URL = sh(returnStdout: true, script: 'git config remote.origin.url').trim().toLowerCase()
  sh "echo ${GITHUB_ORIGIN_URL}"
  env.GITHUB_REPO = env.GITHUB_ORIGIN_URL.tokenize('/').last().split("\\.git")[0]
  env.GITHUB_ORG = env.GITHUB_ORIGIN_URL.tokenize('/')[2]
  sh "echo ${GITHUB_REPO}"
  sh "echo ${GITHUB_ORG}"
}
