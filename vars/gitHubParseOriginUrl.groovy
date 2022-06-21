def call() {
  env.GITHUB_ORIGIN_URL = scm.getUserRemoteConfigs()[0]?.getUrl()
  sh "echo ${GITHUB_ORIGIN_URL}"
  env.GITHUB_REPO = env.GITHUB_ORIGIN_URL.tokenize('/').last().split("\\.git")[0]
  env.GITHUB_ORG = env.GITHUB_ORIGIN_URL.tokenize('/')[2]
  env.CONTROLLER_FOLDER = env.GITHUB_ORG.toLowerCase()
  env.BUNDLE_ID = env.CASC_BUNDLE_ID

  sh "echo GITHUB_REPO: ${GITHUB_REPO}"
  sh "echo CONTROLLER_FOLDER: ${CONTROLLER_FOLDER}"
  sh "echo GITHUB_ORG: ${GITHUB_ORG}"
  sh "echo BUNDLE_ID: ${BUNDLE_ID}"
}
