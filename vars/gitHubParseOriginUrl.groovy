def call() {
  env.GITHUB_ORIGIN_URL = scm.getUserRemoteConfigs()[0]?.getUrl()
  sh "echo ${GITHUB_ORIGIN_URL}"
  env.GITHUB_REPO = env.GITHUB_ORIGIN_URL.tokenize('/').last().split("\\.git")[0]
  env.GITHUB_ORG = env.GITHUB_ORIGIN_URL.tokenize('/')[2]
  sh "echo ${GITHUB_REPO}"
  sh "echo ${GITHUB_ORG}"
}
