def call() {
  env.GITHUB_ORIGIN_URL = scm.getUserRemoteConfigs()[0]?.getUrl()
  sh "echo ${GITHUB_ORIGIN_URL}"
  env.GITHUB_REPO = env.GITHUB_ORIGIN_URL.tokenize('/').last().split("\\.git")[0]
  env.GITHUB_ORG = env.GITHUB_ORIGIN_URL.tokenize('/')[2]
  env.CONTROLLER_FOLDER = env.GITHUB_ORG.toLowerCase()
  if(env.GITHUB_APP=="cloudbees-ci-workshop") {
    env.BUNDLE_ID = "${CONTROLLER_FOLDER}-controller"
  } else {
    env.BUNDLE_ID = "${CONTROLLER_FOLDER}-${GITHUB_REPO}"
  }

  sh "echo GITHUB_REPO: ${GITHUB_REPO}"
  sh "echo CONTROLLER_FOLDER: ${CONTROLLER_FOLDER}"
  sh "echo GITHUB_ORG: ${GITHUB_ORG}"
  sh "echo BUNDLE_ID: ${BUNDLE_ID}"
}
